package org.swan.wabx.velocity.cartridge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

/**
 * 用于增强Velocity渲染功能的模板事件桥接器配置管理器。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年3月22日 下午8:40:56
 */
public class DefaultEventCartridgeConfigurer implements EventCartridgeConfigurer, InitializingBean {

	protected final Log log = LogFactory.getLog(getClass());
	protected List<String> handlers;
	protected EventCartridge eventCartridge;
	
	@Override
	public final void afterPropertiesSet() throws Exception {
		try {
			init();
		} catch (Exception e) {
			log.error("EventCartridgeConfigurer init error.", e);
		}
	}
	
	protected void init() throws Exception {
		if(handlers == null || handlers.isEmpty()) {
			handlers = new ArrayList<String>();
		}
		handlers.add(RenderableInsertionEventHandler.class.getName());
		eventCartridge = new EventCartridge();
		for (String h : handlers) {
			try {
				Object handler = BeanUtils.instantiateClass(ClassUtils.getDefaultClassLoader().loadClass(h));
				boolean result = false;
                if (handler instanceof ReferenceInsertionEventHandler) {
                    result = getObject().addEventHandler((ReferenceInsertionEventHandler) handler);
                }
                if (handler instanceof NullSetEventHandler) {
                    result = getObject().addEventHandler((NullSetEventHandler) handler);
                }
                if (handler instanceof MethodExceptionEventHandler) {
                    result = getObject().addEventHandler((MethodExceptionEventHandler) handler);
                }
                log.info("Added EventCartridge : " + handler.getClass().getName() + " : " + result);
			} catch (Exception e) {
				log.error(String.format("EventHandler[%s] init error.", h), e);
			}
		}
	}
	
	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}

	@Override
	public EventCartridge getObject() throws Exception {
		return eventCartridge;
	}

	@Override
	public Class<?> getObjectType() {
		return EventCartridge.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}