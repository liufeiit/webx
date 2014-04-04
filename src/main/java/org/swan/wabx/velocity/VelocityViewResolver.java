package org.swan.wabx.velocity;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Velocity模板视图解析器。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年3月16日 下午5:19:16
 */
public class VelocityViewResolver extends AbstractTemplateViewResolver implements InitializingBean, Configurer {
	/** 时间工具名称 */
	protected String dateToolName = null;
	/** 数字工具名称 */
	protected String numberToolName = null;
	/** 响应的数据格式 {@link MediaType} */
	protected String mediaType = null;
	/** 响应的数据编码格式 */
	protected String encoding = null;
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
		applyConfigurer();
		setViewClass(requiredViewClass());
		setExposeRequestAttributes(true);
		setAllowRequestOverride(true);
		setExposeSessionAttributes(true);
		setAllowSessionOverride(true);
		setExposeSpringMacroHelpers(true);
		setExposePathVariables(true);
		setCache(true);
		setCacheLimit(DEFAULT_CACHE_LIMIT);
	}

	@Override
	protected Class<?> requiredViewClass() {
		return VelocityView.class;
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		VelocityView view = VelocityView.class.cast(super.buildView(viewName));
		view.setDateToolName(dateToolName);
		view.setNumberToolName(numberToolName);
		view.setMediaType(mediaType);
		view.setEncoding(encoding);
		view.setTemplates(templates);
		view.setScreen(screen);
		view.setLayout(layout);
		view.setDefaultLayoutTemplate(defaultLayoutTemplate);
		view.setScreenTemplateKey(screenTemplateKey);
		view.setViewName(viewName);
		view.setSuffix(suffix);
		return view;
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		try {
			init();
		} catch (Exception e) {
			logger.error("VelocityViewResolver init error.", e);
		}
	}

	protected void applyConfigurer() {
		if (StringUtils.isEmpty(templates)) {
			templates = PATH_SEPARATOR + WEB_INF + PATH_SEPARATOR + DEFAULT_TEMPLATES + PATH_SEPARATOR;
		} else {
			if (!StringUtils.startsWith(templates, PATH_SEPARATOR)) {
				templates = PATH_SEPARATOR + templates;
			}
			if (!StringUtils.endsWith(templates, PATH_SEPARATOR)) {
				templates = templates + PATH_SEPARATOR;
			}
		}
		if (StringUtils.isEmpty(screen)) {
			screen = DEFAULT_SCREEN + PATH_SEPARATOR;
		} else {
			if (StringUtils.startsWith(screen, PATH_SEPARATOR)) {
				screen = screen.substring(1);
			}
			if (!StringUtils.endsWith(screen, PATH_SEPARATOR)) {
				screen = screen + PATH_SEPARATOR;
			}
		}
		if (StringUtils.isEmpty(layout)) {
			layout = DEFAULT_LAYOUT + PATH_SEPARATOR;
		} else {
			if (StringUtils.startsWith(layout, PATH_SEPARATOR)) {
				layout = layout.substring(1);
			}
			if (!StringUtils.endsWith(layout, PATH_SEPARATOR)) {
				layout = layout + PATH_SEPARATOR;
			}
		}
		if (StringUtils.isEmpty(defaultLayoutTemplate)) {
			defaultLayoutTemplate = DEFAULT_VELOCITY_LAYOUT_TEMPLATE;
		}
		if (StringUtils.isEmpty(screenTemplateKey)) {
			screenTemplateKey = DEFAULT_SCREEN_TEMPLATE_KEY;
		}
		if (StringUtils.isEmpty(suffix)) {
			suffix = DEFAULT_VELOCITY_SUFFIX;
		}
		if (StringUtils.isEmpty(dateToolName)) {
			dateToolName = DEFAULT_DATE_TOOL_NAME;
		}
		if (StringUtils.isEmpty(numberToolName)) {
			numberToolName = DEFAULT_NUMBER_TOOL_NAME;
		}
		if (StringUtils.isEmpty(mediaType)) {
			mediaType = DEFAULT_MEDIA_TYPE;
		}
		if (StringUtils.isEmpty(encoding)) {
			encoding = DEFAULT_CHARSET;
		}
	}

	public void setDateToolName(String dateToolName) {
		this.dateToolName = dateToolName;
	}

	public void setNumberToolName(String numberToolName) {
		this.numberToolName = numberToolName;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setTemplates(String templates) {
		this.templates = templates;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public void setDefaultLayoutTemplate(String defaultLayoutTemplate) {
		this.defaultLayoutTemplate = defaultLayoutTemplate;
	}

	public void setScreenTemplateKey(String screenTemplateKey) {
		this.screenTemplateKey = screenTemplateKey;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}