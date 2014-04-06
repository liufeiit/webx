package org.swan.wabx.velocity.engine;

import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.runtime.parser.node.BooleanPropertyExecutor;
import org.apache.velocity.runtime.parser.node.GetExecutor;
import org.apache.velocity.runtime.parser.node.MapGetExecutor;
import org.apache.velocity.runtime.parser.node.PropertyExecutor;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.VelPropertyGet;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月5日 上午12:02:04
 */
public class Uberspect extends org.apache.velocity.util.introspection.UberspectImpl {
    /**
     * Property getter
     * 
     * @param obj
     * @param identifier
     * @param i
     * @return A Velocity Getter Method.
     * @throws Exception
     */
    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        if (obj == null) {
            return null;
        }

        Class<?> claz = obj.getClass();

        /*
         * first try for a getFoo() type of property (also getfoo() )
         */
        AbstractExecutor executor = new PropertyExecutor(log, introspector, claz, identifier);

        /*
         * Let's see if we are a map...
         */
        if (!executor.isAlive()) {
            executor = new MapGetExecutor(log, claz, identifier);
        }

        /*
         * if that didn't work, look for boolean isFoo()
         */
        if (!executor.isAlive()) {
            executor = new BooleanPropertyExecutor(log, introspector, claz, identifier);
        }

        /*
         * finally, look for get("foo")
         */
        if (!executor.isAlive()) {
            executor = new GetExecutor(log, introspector, claz, identifier);
        }

        return (executor.isAlive()) ? new VelGetterImpl(executor) : null;
    }
}
