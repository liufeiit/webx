package org.swan.wabx.velocity.cartridge;

import org.apache.velocity.app.event.EventCartridge;
import org.springframework.beans.factory.FactoryBean;

/**
 * 用于增强Velocity渲染功能的模板事件桥接器配置管理器。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年3月22日 下午8:31:57
 */
public interface EventCartridgeConfigurer extends FactoryBean<EventCartridge> {

}