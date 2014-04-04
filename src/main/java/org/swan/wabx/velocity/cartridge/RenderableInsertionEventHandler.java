package org.swan.wabx.velocity.cartridge;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

/**
 * 渲染<code>Renderable</code>的event handler。
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年3月22日 下午8:47:33
 */
public class RenderableInsertionEventHandler implements ReferenceInsertionEventHandler {

	@Override
	public Object referenceInsert(String reference, Object value) {
		if (value instanceof Renderable) {
            return ((Renderable) value).render();
        }
        return value;
	}
}