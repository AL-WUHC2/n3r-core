package org.n3r.core.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.n3r.core.joor.Reflect;

/**
 * <p>Utility methods for populating JavaBeans properties via reflection.</p>
 *
 * <p>The implementations are provided by {@link BeanUtilsBean}.
 * These static utility methods use the default instance.
 * More sophisticated behaviour can be provided by using a <code>BeanUtilsBean</code> instance.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey Francois
 * @author Gregor Rayman
 * @version $Revision: 690380 $ $Date: 2008-08-29 21:04:38 +0100 (Fri, 29 Aug 2008) $
 * @see BeanUtilsBean
 */

public class BeanUtils {

    /**
     * <p>Populate the JavaBeans properties of the specified bean, based on
     * the specified name/value pairs.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @param bean JavaBean whose properties are being populated
     * @param properties Map keyed by property name, with the
     *  corresponding (String or String[]) value(s) to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @see BeanUtilsBean#populate
     */
    public static void populate(Object bean, Map properties) throws IllegalAccessException, InvocationTargetException {

        BeanUtilsBean.getInstance().populate(bean, properties);
    }

    public static <T> T populate(Class<T> clazz, Map properties) throws IllegalAccessException,
            InvocationTargetException {
        T bean = Reflect.on(clazz).create().get();

        BeanUtilsBean.getInstance().populate(bean, properties);
        return bean;
    }

    /** 
     * If we're running on JDK 1.4 or later, initialize the cause for the given throwable.
     * 
     * @param  throwable The throwable.
     * @param  cause     The cause of the throwable.
     * @return  true if the cause was initialized, otherwise false.
     * @since 1.8.0
     */
    public static boolean initCause(Throwable throwable, Throwable cause) {
        return BeanUtilsBean.getInstance().initCause(throwable, cause);
    }

}
