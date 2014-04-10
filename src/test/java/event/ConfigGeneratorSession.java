/*
 * Copyright 2010 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package event;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;
 
public class ConfigGeneratorSession {
    protected final ConfigGenerator generator;
    protected final Map props;
    private final Map<String, Object[]> descriptorLogs;
    private final Set<String> processedDestfiles;
    private final Map<String, LazyGenerateItem> lazyGenerateItems;
    private ConfigGenerate currentGenerate;
    private InputStream currentInputStream;
    private OutputStream currentOutputStream;
 
    protected ConfigGeneratorSession(ConfigGenerator generator, PropertiesSet propSet) {
        this.generator = generator;
        this.props = propSet.getMergedProperties();
        this.descriptorLogs = new HashMap<String, Object[]>();
        this.processedDestfiles = new HashSet<String>();
        this.lazyGenerateItems = new HashMap<String, LazyGenerateItem>();
        ConfigDescriptor[] descriptors = generator.getConfigDescriptors();
        Date now = new Date();
 
        for (ConfigDescriptor descriptor : descriptors) {
            String descriptorName = descriptor.getName();
            StringWriter logBuffer = new StringWriter();
            PrintWriter log = new PrintWriter(logBuffer, true);
            descriptorLogs.put(descriptorName, new Object[] { logBuffer, log, descriptor });
            log.println("Last Configured at: " + now);
            log.println();
            log.println("Base URL: " + descriptor.getBaseURL());
            log.println("Descriptor: " + descriptorName);
            log.println();
        }
    }
 
    public void setInputStream(InputStream istream) {
        this.currentInputStream = istream;
    }
 
    public void setOutputStream(OutputStream ostream) {
        this.currentOutputStream = ostream;
    }
 
    public Context getVelocityContext() {
        if (currentGenerate == null) {
            throw new IllegalStateException("Have not call nextEntry method yet");
        }
 
        final Map descriptorProps = new HashMap(currentGenerate.getConfigDescriptor().getContext());
 
        EventCartridge eventCartridge = new EventCartridge();
        eventCartridge.addEventHandler(new PropertiesReferenceInsertionHandler(currentGenerate.getConfigDescriptor(),
                props));
 
        Context context = new AbstractContext() {
            @Override
            public Object internalRemove(Object key) {
                return descriptorProps.remove(key);
            }
 
            @Override
            public Object internalPut(String key, Object value) {
                return descriptorProps.put(key, value);
            }
 
            @Override
            public Object[] internalGetKeys() {
                Set<Object> keys = new LinkedHashSet<Object>(props.keySet());
                keys.addAll(descriptorProps.keySet());
                return keys.toArray(new Object[keys.size()]);
            }
 
            @Override
            public Object internalGet(String key) {
                if (descriptorProps.containsKey(key)) {
                    return descriptorProps.get(key);
                } else {
                    return PropertiesLoader.evaluate(key, props);
                }
            }
 
            @Override
            public boolean internalContainsKey(Object key) {
                return descriptorProps.containsKey(key) || props.containsKey(key);
            }
        };
 
        eventCartridge.attachToContext(context);
        context.put("D", "$");
 
        return context;
    }
 
    public boolean generate(ConfigGeneratorCallback callback) {
        boolean allSuccess = true;
        for (String template : generator.generateTemplateFiles.keySet()) {
            allSuccess &= generate(template, callback);
        }
        generateLog(callback);
        return allSuccess;
    }
 
    public boolean generate(String template, ConfigGeneratorCallback callback) {
        List<ConfigGenerate> generates = generator.generateTemplateFilesIncludingMetaInfos.get(template);
        if (generates == null || generates.isEmpty()) {
            throw new ConfigException("No defined template " + template);
        }
        boolean allSuccess = true;
        for (ConfigGenerate generate : generates) {
            try {
                currentGenerate = generate;
                template = callback.nextEntry(template, currentGenerate);
                if (currentInputStream == null || currentOutputStream == null) {
                    throw new IllegalStateException("InputStream/OutputStream has not been set");
                }
                allSuccess &= generate(template, currentGenerate, currentInputStream, currentOutputStream);
            } finally {
                try {
                    callback.closeEntry();
                } finally {
                    currentGenerate = null;
                    currentInputStream = null;
                    currentOutputStream = null;
                }
            }
        }
        return allSuccess;
    }
 
    private boolean generate(String template, ConfigGenerate generate, InputStream istream, OutputStream ostream) {
        processedDestfiles.add(generate.getDestfile());
 
        String charset = generate.getCharset();
        String outputCharset = generate.getOutputCharset();
        PrintWriter descriptorLog = (PrintWriter) descriptorLogs.get(generate.getConfigDescriptor().getName())[1];
 
        if (StringUtil.isBlank(charset)) {
            istream = new BufferedInputStream(istream);
            charset = guessCharsetEncoding((BufferedInputStream) istream);
        }
 
        if (StringUtil.isBlank(charset)) {
            charset = "ISO-8859-1";
        }
 
        if (StringUtil.isBlank(outputCharset)) {
            outputCharset = charset;
        }
 
        Reader reader = null;
        Writer writer = null;
 
        try {
            reader = new BufferedReader(new InputStreamReader(istream, charset)) {
                @Override
                public void close() throws IOException {
                    // ??????
                }
            };
            writer = new BufferedWriter(new OutputStreamWriter(ostream, outputCharset)) {
                @Override
                public void close() throws IOException {
                    // ??????
                }
            };
 
            descriptorLog.println("Generating " + template + " [" + charset + "] => " + generate.getDestfile() + " ["
                    + outputCharset + "]");
 
            generator.logger.info("<" + generate.getConfigDescriptor().getBaseURL() + ">\n    Generating " + template
                    + " [" + charset + "] => " + generate.getDestfile() + " [" + outputCharset + "]\n");
 
            return VelocityTemplateEngine.getInstance().render(getVelocityContext(), reader, writer, template,
                    generate.getConfigDescriptor().getName(), generate.getConfigDescriptor().getBaseURL());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new ConfigException(e);
            }
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                }
            }
        }
    }
 
    private final static Pattern encodingPattern = Pattern.compile("encoding\\s*=\\s*[\\\"|']([^\\\"|']+)[\\\"|']");
 
    private String guessCharsetEncoding(BufferedInputStream istream) {
        String str;
 
        try {
            byte[] buf = new byte[1024];
            int count = 0;
 
            try {
                istream.mark(buf.length);
                count = istream.read(buf);
            } finally {
                istream.reset();
            }
 
            str = new String(buf, 0, count, "ISO_8859_1");
        } catch (Exception e) {
            str = "";
        }
 
        Matcher m = encodingPattern.matcher(str);
        String charset = null;
 
        if (m.find()) {
            charset = m.group(1).trim();
        }
 
        return charset;
    }
 
    public void generateLog(ConfigGeneratorCallback callback) {
        for (Object[] logPair : descriptorLogs.values()) {
            try {
                StringWriter logBuffer = (StringWriter) logPair[0];
                PrintWriter log = (PrintWriter) logPair[1];
                ConfigDescriptor descriptor = (ConfigDescriptor) logPair[2];
                String logfile = generator.getDescriptorLogFile(descriptor);
 
                callback.logEntry(descriptor, logfile);
 
                String logContent = logBuffer.toString();
                Writer writer = null;
 
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(currentOutputStream, LocaleInfo.getDefault()
                            .getCharset())) {
                        @Override
                        public void close() throws IOException {
                        }
                    };
 
                    generator.logger.info("<" + descriptor.getBaseURL() + ">\n    Generating log file: " + logfile
                            + "\n");
 
                    writer.write(logContent);
                } catch (IOException e) {
                    throw new ConfigException(e);
                } finally {
                    if (writer != null) {
                        try {
                            writer.flush();
                        } catch (IOException e) {
                        }
                    }
                }
            } finally {
                callback.closeEntry();
 
                currentGenerate = null;
                currentInputStream = null;
                currentOutputStream = null;
            }
        }
    }
 
    public void addLazyGenerateItem(String name, byte[] bytes) {
        lazyGenerateItems.put(name,
                new LazyGenerateItem(name, generator.generateTemplateFilesIncludingMetaInfos.get(name), bytes));
    }
 
    public boolean generateLazyItems(ConfigGeneratorCallback callback) {
        boolean allSuccess = true;
 
        for (LazyGenerateItem item : lazyGenerateItems.values()) {
            String name = item.getTemplateName();
 
            for (ConfigGenerate generate : item.getGenerates()) {
                String destname = generate.getDestfile();
 
                if (!processedDestfiles.contains(destname)) {
                    try {
                        currentGenerate = generate;
 
                        callback.nextEntry(generate.getConfigDescriptor(), item.getTemplateContentStream(),
                                generate.getTemplateBase() + name);
 
                        if (currentInputStream == null || currentOutputStream == null) {
                            throw new IllegalStateException("InputStream/OutputStream has not been set");
                        }
 
                        StreamUtil.io(currentInputStream, currentOutputStream, true, false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            callback.closeEntry();
                        } finally {
                            currentGenerate = null;
                            currentInputStream = null;
                            currentOutputStream = null;
                        }
                    }
 
                    try {
                        currentGenerate = generate;
 
                        callback.nextEntry(generate.getConfigDescriptor(), item.getTemplateContentStream(),
                                generate.getDestfile());
 
                        if (currentInputStream == null || currentOutputStream == null) {
                            throw new IllegalStateException("InputStream/OutputStream has not been set");
                        }
 
                        allSuccess &= generate(name, generate, currentInputStream, currentOutputStream);
                    } finally {
                        try {
                            callback.closeEntry();
                        } finally {
                            currentGenerate = null;
                            currentInputStream = null;
                            currentOutputStream = null;
                        }
                    }
                }
            }
        }
 
        return allSuccess;
    }
 
    public void checkNonprocessedTemplates() {
        for (String destfile : generator.generateDestFiles.keySet()) {
            if (!processedDestfiles.contains(destfile)) {
                ConfigGenerate generate = generator.generateDestFiles.get(destfile);
                String template = generate.getTemplate();
 
                throw new ConfigException("Could not find template file: " + template + " for descriptor: "
                        + generate.getConfigDescriptor().getURL());
            }
        }
    }
 
    public void close() {
    }
}