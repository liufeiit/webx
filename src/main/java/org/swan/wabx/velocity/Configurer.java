package org.swan.wabx.velocity;

import java.io.File;

import org.springframework.http.MediaType;

/**
 * Velocity的约定配置常量.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月4日 下午5:34:21
 */
public interface Configurer {
	/** 默认的velocity文件扩展名 */
	String DEFAULT_VELOCITY_SUFFIX = ".vm";
	/** 在Layout中引用screen的名称 */
	String DEFAULT_SCREEN_TEMPLATE_KEY = "screen_placeholder";
	/** 默认是加载与screen同名的layout文件，如果找不到的话就用默认的 */
	String DEFAULT_VELOCITY_LAYOUT_TEMPLATE = "default";
	/** layout文件的目录 */
	String DEFAULT_LAYOUT = "layout";
	/** screen文件的目录 */
	String DEFAULT_SCREEN = "screen";
	/** 所有模板文件的跟路径 */
	String DEFAULT_TEMPLATES = "templates";
	/** 约定将模板文件放在WEB-INF目录下 */
	String WEB_INF = "WEB-INF";
	String PATH_SEPARATOR = File.separator;
	String DEFAULT_MEDIA_TYPE = MediaType.TEXT_HTML_VALUE;
	String DEFAULT_CHARSET = "UTF-8";
	String DEFAULT_DATE_TOOL_NAME = "date";
	String DEFAULT_NUMBER_TOOL_NAME = "number";
}