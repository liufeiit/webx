package org.swan.wabx.velocity.engine;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.ResourceCacheImpl;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.JarResourceLoader;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;
import org.apache.velocity.tools.view.WebappResourceLoader;
import org.swan.wabx.velocity.Configurer;

/**
 * Velocity模板引擎配置。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月4日 下午11:25:02
 */
public class VelocityConfigurer extends org.springframework.web.servlet.view.velocity.VelocityConfigurer implements
		Configurer {
	public static final String DEFAULT_INPUT_ENCODING = "UTF-8";
	public static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";
	public static final String DEFAULT_TEMPLATE_MODIFICATION_CHECK_INTERVAL = "3";
	public static final String DEFAULT_TEMPLATE_CACHE_ON = "true";
	public static final String DEFAULT_PARSER_POOL_SIZE = "50";
	public static final String DEFAULT_UBERSPECT_CLASSNAME = Uberspect.class.getName();

	/** inputEncoding */
	protected String inputEncoding = null;
	/** outputEncoding */
	protected String outputEncoding = null;

	@Override
	public void afterPropertiesSet() throws IOException, VelocityException {
		setOverrideLogging(true);
		if (StringUtils.isEmpty(inputEncoding)) {
			inputEncoding = DEFAULT_INPUT_ENCODING;
		}
		if (StringUtils.isEmpty(outputEncoding)) {
			outputEncoding = DEFAULT_OUTPUT_ENCODING;
		}
		super.afterPropertiesSet();
	}

	@Override
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
		super.postProcessVelocityEngine(velocityEngine);

		velocityEngine.addProperty(RuntimeConstants.RESOURCE_LOADER, "webapp");
		velocityEngine.setProperty("webapp.resource.loader.class", WebappResourceLoader.class.getName());
		velocityEngine.setProperty("webapp.resource.loader.cache", DEFAULT_TEMPLATE_CACHE_ON);
		velocityEngine.setProperty("webapp.resource.loader.modificationCheckInterval",
				DEFAULT_TEMPLATE_MODIFICATION_CHECK_INTERVAL);

		velocityEngine.addProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		velocityEngine.setProperty("file.resource.loader.cache", DEFAULT_TEMPLATE_CACHE_ON);
		velocityEngine.setProperty("file.resource.loader.modificationCheckInterval",
				DEFAULT_TEMPLATE_MODIFICATION_CHECK_INTERVAL);

		velocityEngine.addProperty(RuntimeConstants.RESOURCE_LOADER, "class");
		velocityEngine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.setProperty("class.resource.loader.cache", DEFAULT_TEMPLATE_CACHE_ON);
		velocityEngine.setProperty("class.resource.loader.modificationCheckInterval",
				DEFAULT_TEMPLATE_MODIFICATION_CHECK_INTERVAL);

		velocityEngine.addProperty(RuntimeConstants.RESOURCE_LOADER, "url");
		velocityEngine.setProperty("url.resource.loader.class", URLResourceLoader.class.getName());
		velocityEngine.setProperty("url.resource.loader.cache", DEFAULT_TEMPLATE_CACHE_ON);
		velocityEngine.setProperty("url.resource.loader.modificationCheckInterval",
				DEFAULT_TEMPLATE_MODIFICATION_CHECK_INTERVAL);

		velocityEngine.addProperty(RuntimeConstants.RESOURCE_LOADER, "jar");
		velocityEngine.setProperty("jar.resource.loader.class", JarResourceLoader.class.getName());
		velocityEngine.setProperty("jar.resource.loader.cache", DEFAULT_TEMPLATE_CACHE_ON);
		velocityEngine.setProperty("jar.resource.loader.modificationCheckInterval",
				DEFAULT_TEMPLATE_MODIFICATION_CHECK_INTERVAL);

		velocityEngine.setProperty(VelocityEngine.RESOURCE_MANAGER_LOGWHENFOUND, "false");
		velocityEngine.setProperty(VelocityEngine.PARSER_POOL_SIZE, DEFAULT_PARSER_POOL_SIZE);
		velocityEngine.setProperty(VelocityEngine.UBERSPECT_CLASSNAME, DEFAULT_UBERSPECT_CLASSNAME);
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_MANAGER_CACHE_CLASS, ResourceCacheImpl.class.getName());
		velocityEngine.setProperty("resource.manager.cache.size", 2048);
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_MANAGER_CLASS, ResourceManagerImpl.class.getName());
		velocityEngine.setProperty(RuntimeConstants.PARSER_POOL_SIZE, "100");
		velocityEngine.setProperty(RuntimeConstants.COUNTER_INITIAL_VALUE, "1");
		velocityEngine.setProperty(RuntimeConstants.INPUT_ENCODING, inputEncoding);
		velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, outputEncoding);
		velocityEngine.setProperty(RuntimeConstants.VM_PERM_INLINE_LOCAL, "true");
		// org.apache.velocity.runtime.log.AvalonLogChute
		// org.apache.velocity.runtime.log.Log4JLogChute
		// org.apache.velocity.runtime.log.CommonsLogLogChute
		// org.apache.velocity.runtime.log.ServletLogChute
		// org.apache.velocity.runtime.log.JdkLogChute
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new Log4JLogChute());
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, true);
	}

	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
}