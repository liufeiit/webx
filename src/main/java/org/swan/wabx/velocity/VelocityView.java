package org.swan.wabx.velocity;

import java.io.File;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;
import org.springframework.web.util.NestedServletException;
import org.swan.wabx.velocity.cartridge.EventCartridgeConfigurer;

/**
 * Velocity模板视图。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年3月16日 下午5:36:32
 */
public class VelocityView extends AbstractTemplateView {
	private static final String DEFAULT_VELOCITY_SUFFIX = ".vm";
	private static final String DEFAULT_SCREEN_TEMPLATE_KEY = "screen_placeholder";
	private static final String DEFAULT_VELOCITY_LAYOUT_TEMPLATE = "default";
	private static final String DEFAULT_LAYOUT = "layout";
	private static final String DEFAULT_SCREEN = "screen";
	private static final String DEFAULT_TEMPLATES = "templates";
	private static final String WEB_INF = "WEB-INF";
	
	public static final String PATH_SEPARATOR = File.separator;
	public static final String DEFAULT_MEDIA_TYPE = MediaType.TEXT_HTML_VALUE;
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String DEFAULT_DATE_TOOL_NAME = "date";
	public static final String DEFAULT_NUMBER_TOOL_NAME = "number";
	
	protected String dateToolName = DEFAULT_DATE_TOOL_NAME;
	protected String numberToolName = DEFAULT_NUMBER_TOOL_NAME;

	protected String mediaType = DEFAULT_MEDIA_TYPE;
	protected String encoding = DEFAULT_CHARSET;

	protected VelocityEngine velocityEngine;
	protected EventCartridge eventCartridge;
	
	/** 模板文件的根路径目录 */
	protected String templates = null;
	/** screen模板解析的视图目录 */
	protected String screen = null;
	/** layout模板解析的视图目录 */
	protected String layout = null;

	/** velocity模板默认加载的layout模板 */
	protected String defaultLayoutTemplate = null;
	/** screen模板key */
	protected String screenTemplateKey = null;

	/** 本次请求对应的视图名称 */
	protected String viewName = null;
	/** 模板对应的扩展名称 */
	protected String suffix = null;
	
	protected void init() throws Exception {
		super.afterPropertiesSet();
		setContentType(mediaType + ";charset=" + encoding);
		applyConfigurer();
	}

	protected void applyConfigurer() {
		if(StringUtils.isEmpty(templates)) {
			templates = PATH_SEPARATOR + WEB_INF + PATH_SEPARATOR + DEFAULT_TEMPLATES + PATH_SEPARATOR;
		} else {
			if(!StringUtils.startsWith(templates, PATH_SEPARATOR)) {
				templates = PATH_SEPARATOR + templates;
			}
			if(!StringUtils.endsWith(templates, PATH_SEPARATOR)) {
				templates = templates + PATH_SEPARATOR;
			}
		}
		if(StringUtils.isEmpty(screen)) {
			screen = DEFAULT_SCREEN + PATH_SEPARATOR;
		} else {
			if(StringUtils.startsWith(screen, PATH_SEPARATOR)) {
				screen = screen.substring(1);
			}
			if(!StringUtils.endsWith(screen, PATH_SEPARATOR)) {
				screen = screen + PATH_SEPARATOR;
			}
		}
		if(StringUtils.isEmpty(layout)) {
			layout = DEFAULT_LAYOUT + PATH_SEPARATOR;
		} else {
			if(StringUtils.startsWith(layout, PATH_SEPARATOR)) {
				layout = layout.substring(1);
			}
			if(!StringUtils.endsWith(layout, PATH_SEPARATOR)) {
				layout = layout + PATH_SEPARATOR;
			}
		}
		if(StringUtils.isEmpty(defaultLayoutTemplate)) {
			defaultLayoutTemplate = DEFAULT_VELOCITY_LAYOUT_TEMPLATE;
		}
		if(StringUtils.isEmpty(screenTemplateKey)) {
			screenTemplateKey = DEFAULT_SCREEN_TEMPLATE_KEY;
		}
		if(StringUtils.isEmpty(suffix)) {
			suffix = DEFAULT_VELOCITY_SUFFIX;
		}
	}

	/**
	 * Process the model map by merging it with the Velocity template.
	 * Output is directed to the servlet response.
	 * <p>This method can be overridden if custom behavior is needed.
	 */
	@Override
	protected void renderMergedTemplateModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Context velocityContext = createVelocityContext(model, request, response);
		Context attachContext = attachToContextEvent(velocityContext);
		exposeToolAttributes(attachContext, request);
		doRender(attachContext, response);
	}
	
	protected Context attachToContextEvent(Context velocityContext) {
		if(getEventCartridge() == null) {
			return velocityContext;
		}
		Context eventContext;
		if (velocityContext instanceof InternalEventContext) {
            eventContext = velocityContext;
        } else {
            eventContext = new VelocityContext(velocityContext);
        }
		EventCartridge ec = getEventCartridge();
		ec.attachToContext(eventContext);
		return eventContext;
	}

	/**
	 * Render the Velocity view to the given response, using the given Velocity
	 * context which contains the complete template model to use.
	 * <p>The default implementation renders the template specified by the "url"
	 * bean property, retrieved via {@code getTemplate}. It delegates to the
	 * {@code mergeTemplate} method to merge the template instance with the
	 * given Velocity context.
	 * <p>Can be overridden to customize the behavior, for example to render
	 * multiple templates into a single view.
	 * @param context the Velocity context to use for rendering
	 * @param response servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception if thrown by Velocity
	 * @see #setUrl
	 * @see #getTemplate()
	 * @see #mergeTemplate
	 */
	protected void doRender(Context context, HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering Velocity template [" + getUrl() + "] in VelocityView '" + getBeanName() + "'");
		}
		Template screenTemplate = getTemplate();
		// 同名的layout
		String layoutTemplateURL = templates + layout + viewName + suffix;
		Template layoutTemplate = checkAndGetTemplate(layoutTemplateURL);

		if (layoutTemplate == null) {// 默认的layout
			layoutTemplateURL = templates + layout + defaultLayoutTemplate + suffix;
			layoutTemplate = checkAndGetTemplate(layoutTemplateURL);
		}

		if (layoutTemplate == null) {// 没有找到layout就只解析screen
			mergeTemplate(screenTemplate, context, response);
			return;
		}

		context.put(screenTemplateKey, templateRender(context, screenTemplate));
		mergeTemplate(layoutTemplate, context, response);
	}

	/**
	 * 渲染一个模板。
	 * 
	 * @param context
	 * @param template
	 * @return
	 */
	protected String templateRender(Context context, Template template) {
		if (template == null || context == null) {
			return "";
		}
		try {
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			return sw.toString();
		} catch (Exception e) {
			logger.error(String.format("View named [%s] Render Error.", template.getName()), e);
		}
		return "";
	}

	/**
	 * 安全的校验并获取模板。
	 * 
	 * @param name
	 * @return
	 */
	protected Template checkAndGetTemplate(String name) {
		try {//TODO 优化
			return getTemplate(name);
		} catch (Exception e) {
			logger.error("loading view template [" + name + "] error.", e);
		}
		return null;
	}

	/**
 	 * Invoked on startup. Looks for a single VelocityConfig bean to
 	 * find the relevant VelocityEngine for this factory.
 	 */
	@Override
	protected void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		if (getVelocityEngine() == null) {
			// No explicit VelocityEngine: try to autodetect one.
			setVelocityEngine(autodetectVelocityEngine());
		}
		if(getEventCartridge() == null) {
			setEventCartridge(autodetectEventCartridge());
		}
	}
	
	protected EventCartridge autodetectEventCartridge() {
		try {
			EventCartridgeConfigurer eventCartridgeConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(
					getApplicationContext(), EventCartridgeConfigurer.class, true, false);
			return eventCartridgeConfigurer.getEventCartridge();
		} catch (NoSuchBeanDefinitionException ex) {
			logger.warn("No EventCartridgeConfigurer bean in this web application context.");
		}
		return null;
	}

	/**
	 * Autodetect a VelocityEngine via the ApplicationContext.
	 * Called if no explicit VelocityEngine has been specified.
	 * @return the VelocityEngine to use for VelocityViews
	 * @throws BeansException if no VelocityEngine could be found
	 * @see #getApplicationContext
	 * @see #setVelocityEngine
	 */
	protected VelocityEngine autodetectVelocityEngine() throws BeansException {
		try {
			VelocityConfig velocityConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(
					getApplicationContext(), VelocityConfig.class, true, false);
			return velocityConfig.getVelocityEngine();
		} catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException(
					"Must define a single VelocityConfig bean in this web application context " +
					"(may be inherited): VelocityConfigurer is the usual implementation. " +
					"This bean may be given any name.", ex);
		}
	}

	/**
	 * Create a Velocity Context instance for the given model,
	 * to be passed to the template for merging.
	 * <p>The default implementation delegates to {@link #createVelocityContext(Map)}.
	 * Can be overridden for a special context class, for example ChainedContext which
	 * is part of the view package of Velocity Tools. ChainedContext is needed for
	 * initialization of ViewTool instances.
	 * <p>Have a look at {@link VelocityToolboxView}, which pre-implements
	 * ChainedContext support. This is not part of the standard VelocityView class
	 * in order to avoid a required dependency on the view package of Velocity Tools.
	 * @param model the model Map, containing the model attributes to be exposed to the view
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return the Velocity Context
	 * @throws Exception if there's a fatal error while creating the context
	 * @see #createVelocityContext(Map)
	 * @see org.apache.velocity.tools.view.context.ChainedContext
	 * @see VelocityToolboxView
	 */
	protected Context createVelocityContext(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		return createVelocityContext(model);
	}

	/**
	 * Create a Velocity Context instance for the given model,
	 * to be passed to the template for merging.
	 * <p>Default implementation creates an instance of Velocity's
	 * VelocityContext implementation class.
	 * @param model the model Map, containing the model attributes
	 * to be exposed to the view
	 * @return the Velocity Context
	 * @throws Exception if there's a fatal error while creating the context
	 * @see org.apache.velocity.VelocityContext
	 */
	protected Context createVelocityContext(Map<String, Object> model) throws Exception {
		return new VelocityContext(model);
	}

	/**
	 * Expose the tool attributes, according to corresponding bean property settings.
	 * <p>Do not override this method unless for further tools driven by bean properties.
	 * Override one of the {@code exposeHelpers} methods to add custom helpers.
	 * @param velocityContext Velocity context that will be passed to the template
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding model attributes
	 * @see #setDateToolAttribute
	 * @see #setNumberToolAttribute
	 */
	protected void exposeToolAttributes(Context velocityContext, HttpServletRequest request) throws Exception {
		// Expose locale-aware DateTool/NumberTool attributes.
		if (dateToolName != null || numberToolName != null) {
			if (dateToolName != null) {
				velocityContext.put(dateToolName, new LocaleAwareDateTool(request));
			}
			if (numberToolName != null) {
				velocityContext.put(numberToolName, new LocaleAwareNumberTool(request));
			}
		}
	}

	/**
	 * Retrieve the Velocity template to be rendered by this view.
	 * <p>By default, the template specified by the "url" bean property will be
	 * retrieved: either returning a cached template instance or loading a fresh
	 * instance (according to the "cacheTemplate" bean property)
	 * @return the Velocity template to render
	 * @throws Exception if thrown by Velocity
	 * @see #setUrl
	 * @see #setCacheTemplate
	 * @see #getTemplate(String)
	 */
	protected Template getTemplate() throws Exception {
		// We already hold a reference to the template, but we might want to load it
		// if not caching. Velocity itself caches templates, so our ability to
		// cache templates in this class is a minor optimization only.
		return getTemplate(getUrl());
	}

	/**
	 * Retrieve the Velocity template specified by the given name,
	 * using the encoding specified by the "encoding" bean property.
	 * <p>Can be called by subclasses to retrieve a specific template,
	 * for example to render multiple templates into a single view.
	 * @param name the file name of the desired template
	 * @return the Velocity template
	 * @throws Exception if thrown by Velocity
	 * @see org.apache.velocity.app.VelocityEngine#getTemplate
	 */
	protected Template getTemplate(String name) throws Exception {
		return (encoding != null ?
				getVelocityEngine().getTemplate(name, encoding) :
				getVelocityEngine().getTemplate(name));
	}

	/**
	 * Merge the template with the context.
	 * Can be overridden to customize the behavior.
	 * @param template the template to merge
	 * @param context the Velocity context to use for rendering
	 * @param response servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception if thrown by Velocity
	 * @see org.apache.velocity.Template#merge
	 */
	protected void mergeTemplate(
			Template template, Context context, HttpServletResponse response) throws Exception {
		try {
			template.merge(context, response.getWriter());
		} catch (MethodInvocationException ex) {
			Throwable cause = ex.getWrappedThrowable();
			throw new NestedServletException(
					"Method invocation failed during rendering of Velocity view with name '" +
					getBeanName() + "': " + ex.getMessage() + "; reference [" + ex.getReferenceName() +
					"], method '" + ex.getMethodName() + "'",
					cause==null ? ex : cause);
		}
	}

	/**
	 * Check that the Velocity template used for this view exists and is valid.
	 * <p>Can be overridden to customize the behavior, for example in case of
	 * multiple templates to be rendered into a single view.
	 */
	@Override
	public boolean checkResource(Locale locale) throws Exception {
		return true;
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		try {
			init();
		} catch (Exception e) {
			logger.error("VelocityView init error.", e);
		}
	}

	/**
	 * Set the name of the DateTool helper object to expose in the Velocity context
	 * of this view, or {@code null} if not needed. The exposed DateTool will be aware of
	 * the current locale, as determined by Spring's LocaleResolver.
	 * <p>DateTool is part of the generic package of Velocity Tools 1.0.
	 * Spring uses a special locale-aware subclass of DateTool.
	 * @see org.apache.velocity.tools.generic.DateTool
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.LocaleResolver
	 */
	public void setDateToolName(String dateToolName) {
		this.dateToolName = dateToolName;
	}

	/**
	 * Set the name of the NumberTool helper object to expose in the Velocity context
	 * of this view, or {@code null} if not needed. The exposed NumberTool will be aware of
	 * the current locale, as determined by Spring's LocaleResolver.
	 * <p>NumberTool is part of the generic package of Velocity Tools 1.1.
	 * Spring uses a special locale-aware subclass of NumberTool.
	 * @see org.apache.velocity.tools.generic.NumberTool
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.LocaleResolver
	 */
	public void setNumberToolName(String numberToolName) {
		this.numberToolName = numberToolName;
	}

	/**
	 * Set the encoding of the Velocity template file. Default is determined
	 * by the VelocityEngine: "ISO-8859-1" if not specified otherwise.
	 * <p>Specify the encoding in the VelocityEngine rather than per template
	 * if all your templates share a common encoding.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	protected EventCartridge getEventCartridge() {
		return eventCartridge;
	}

	public void setEventCartridge(EventCartridge eventCartridge) {
		this.eventCartridge = eventCartridge;
	}

	/**
	 * Set the VelocityEngine to be used by this view.
	 * <p>If this is not set, the default lookup will occur: A single VelocityConfig
	 * is expected in the current web application context, with any bean name.
	 * @see VelocityConfig
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	/**
	 * Return the VelocityEngine used by this view.
	 */
	protected VelocityEngine getVelocityEngine() {
		return this.velocityEngine;
	}

	/**
	 * @param templates
	 *            the templates to set
	 */
	public void setTemplates(String templates) {
		this.templates = templates;
	}

	/**
	 * @param screen
	 *            the screen to set
	 */
	public void setScreen(String screen) {
		this.screen = screen;
	}

	/**
	 * @param layout
	 *            the layout to set
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * @param defaultLayoutTemplate
	 *            the defaultLayoutTemplate to set
	 */
	public void setDefaultLayoutTemplate(String defaultLayoutTemplate) {
		this.defaultLayoutTemplate = defaultLayoutTemplate;
	}

	/**
	 * @param screenTemplateKey
	 *            the screenTemplateKey to set
	 */
	public void setScreenTemplateKey(String screenTemplateKey) {
		this.screenTemplateKey = screenTemplateKey;
	}

	/**
	 * @param viewName
	 *            the viewName to set
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * @param suffix
	 *            the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Subclass of DateTool from Velocity Tools, using a Spring-resolved
	 * Locale and TimeZone instead of the default Locale.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
	 */
	private static class LocaleAwareDateTool extends DateTool {

		private final HttpServletRequest request;

		public LocaleAwareDateTool(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public Locale getLocale() {
			return RequestContextUtils.getLocale(this.request);
		}

		@Override
		public TimeZone getTimeZone() {
			TimeZone timeZone = RequestContextUtils.getTimeZone(this.request);
			return (timeZone != null ? timeZone : super.getTimeZone());
		}
	}


	/**
	 * Subclass of NumberTool from Velocity Tools, using a Spring-resolved
	 * Locale instead of the default Locale.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 */
	private static class LocaleAwareNumberTool extends NumberTool {
		private final HttpServletRequest request;

		public LocaleAwareNumberTool(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public Locale getLocale() {
			return RequestContextUtils.getLocale(this.request);
		}
	}
}