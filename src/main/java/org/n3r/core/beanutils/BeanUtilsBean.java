package org.n3r.core.beanutils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.n3r.core.beanutils.expression.Resolver;

/**
 * <p>JavaBean property population methods.</p>
 *
 * <p>This class provides implementations for the utility methods in
 * {@link BeanUtils}.
 * Different instances can be used to isolate caches between classloaders
 * and to vary the value converters registered.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey Francois
 * @author Gregor Rayman
 * @version $Revision: 834031 $ $Date: 2009-11-09 12:26:52 +0000 (Mon, 09 Nov 2009) $
 * @see BeanUtils
 * @since 1.7
 */

public class BeanUtilsBean {

    // ------------------------------------------------------ Private Class Variables

    /** 
     * Contains <code>BeanUtilsBean</code> instances indexed by context classloader.
     */
    private static final ContextClassLoaderLocal BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal() {
        // Creates the default instance used when the context classloader is unavailable
        @Override
        protected Object initialValue() {
            return new BeanUtilsBean();
        }
    };

    /** 
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @return The (pseudo-singleton) BeanUtils bean instance
     */
    public static BeanUtilsBean getInstance() {
        return (BeanUtilsBean) BEANS_BY_CLASSLOADER.get();
    }

    /** 
     * Sets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     * 
     * @param newInstance The (pseudo-singleton) BeanUtils bean instance
     */
    public static void setInstance(BeanUtilsBean newInstance) {
        BEANS_BY_CLASSLOADER.set(newInstance);
    }

    // --------------------------------------------------------- Attributes

    /** Used to perform conversions between object types when setting properties */
    private ConvertUtilsBean convertUtilsBean;

    /** Used to access properties*/
    private PropertyUtilsBean propertyUtilsBean;

    /** A reference to Throwable's initCause method, or null if it's not there in this JVM */
    private static final Method INIT_CAUSE_METHOD = getInitCauseMethod();

    // --------------------------------------------------------- Constuctors

    /** 
     * <p>Constructs an instance using new property 
     * and conversion instances.</p>
     */
    public BeanUtilsBean() {
        this(new ConvertUtilsBean(), new PropertyUtilsBean());
    }

    /** 
     * <p>Constructs an instance using given conversion instances
     * and new {@link PropertyUtilsBean} instance.</p>
     *
     * @param convertUtilsBean use this <code>ConvertUtilsBean</code> 
     * to perform conversions from one object to another
     *
     * @since 1.8.0
     */
    public BeanUtilsBean(ConvertUtilsBean convertUtilsBean) {
        this(convertUtilsBean, new PropertyUtilsBean());
    }

    /** 
     * <p>Constructs an instance using given property and conversion instances.</p>
     *
     * @param convertUtilsBean use this <code>ConvertUtilsBean</code> 
     * to perform conversions from one object to another
     * @param propertyUtilsBean use this <code>PropertyUtilsBean</code>
     * to access properties
     */
    public BeanUtilsBean(
                            ConvertUtilsBean convertUtilsBean,
                            PropertyUtilsBean propertyUtilsBean) {

        this.convertUtilsBean = convertUtilsBean;
        this.propertyUtilsBean = propertyUtilsBean;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * <p>Populate the JavaBeans properties of the specified bean, based on
     * the specified name/value pairs.  This method uses Java reflection APIs
     * to identify corresponding "property setter" method names, and deals
     * with setter arguments of type <code>String</code>, <code>boolean</code>,
     * <code>int</code>, <code>long</code>, <code>float</code>, and
     * <code>double</code>.  In addition, array setters for these types (or the
     * corresponding primitive types) can also be identified.</p>
     * 
     * <p>The particular setter method to be called for each property is
     * determined using the usual JavaBeans introspection mechanisms.  Thus,
     * you may identify custom setter methods using a BeanInfo class that is
     * associated with the class of the bean itself.  If no such BeanInfo
     * class is available, the standard method name conversion ("set" plus
     * the capitalized name of the property in question) is used.</p>
     * 
     * <p><strong>NOTE</strong>:  It is contrary to the JavaBeans Specification
     * to have more than one setter method (with different argument
     * signatures) for the same property.</p>
     *
     * <p><strong>WARNING</strong> - The logic of this method is customized
     * for extracting String-based request parameters from an HTTP request.
     * It is probably not what you want for general property copying with
     * type conversion.  For that purpose, check out the
     * <code>copyProperties()</code> method instead.</p>
     *
     * @param bean JavaBean whose properties are being populated
     * @param properties Map keyed by property name, with the
     *  corresponding (String or String[]) value(s) to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void populate(Object bean, Map properties)
            throws IllegalAccessException, InvocationTargetException {

        // Do nothing unless both arguments have been specified
        if (bean == null || properties == null) { return; }

        // Loop through the property name/value pairs to be set
        Iterator entries = properties.entrySet().iterator();
        while (entries.hasNext()) {

            // Identify the property name and value(s) to be assigned
            Map.Entry entry = (Map.Entry) entries.next();
            String name = (String) entry.getKey();
            if (name == null) {
                continue;
            }

            // Perform the assignment for this property
            setProperty(bean, name, entry.getValue());

        }
    }

    /**
     * <p>Set the specified property value, performing type conversions as
     * required to conform to the type of the destination property.</p>
     *
     * <p>If the property is read only then the method returns 
     * without throwing an exception.</p>
     *
     * <p>If <code>null</code> is passed into a property expecting a primitive value,
     * then this will be converted as if it were a <code>null</code> string.</p>
     *
     * <p><strong>WARNING</strong> - The logic of this method is customized
     * to meet the needs of <code>populate()</code>, and is probably not what
     * you want for general property copying with type conversion.  For that
     * purpose, check out the <code>copyProperty()</code> method instead.</p>
     *
     * <p><strong>WARNING</strong> - PLEASE do not modify the behavior of this
     * method without consulting with the Struts developer community.  There
     * are some subtleties to its functionality that are not documented in the
     * Javadoc description above, yet are vital to the way that Struts utilizes
     * this method.</p>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void setProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {

        // Trace logging (if enabled)

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            }
            catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
        }

        // Declare local variables we will require
        String propName = resolver.getProperty(name); // Simple name of target property
        Class type = null; // Java type of target property
        int index = resolver.getIndex(name); // Indexed subscript value (if any)
        String key = resolver.getKey(name); // Mapped key value (if any)

        // Calculate the property type
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) { return; // Skip this property setter
            }
            type = dynaProperty.getType();
        }
        else if (target instanceof Map) {
            type = Object.class;
        }
        else if (target != null && target.getClass().isArray() && index >= 0) {
            type = Array.get(target, index).getClass();
        }
        else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor =
                        getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) { return; // Skip this property setter
                }
            }
            catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                if (((MappedPropertyDescriptor) descriptor).getMappedWriteMethod() == null) { return; // Read-only, skip this property setter
                }
                type = ((MappedPropertyDescriptor) descriptor).
                        getMappedPropertyType();
            }
            else if (index >= 0 && descriptor instanceof IndexedPropertyDescriptor) {
                if (((IndexedPropertyDescriptor) descriptor).getIndexedWriteMethod() == null) { return; // Read-only, skip this property setter
                }
                type = ((IndexedPropertyDescriptor) descriptor).
                        getIndexedPropertyType();
            }
            else if (key != null) {
                if (descriptor.getReadMethod() == null) { return; // Read-only, skip this property setter
                }
                type = value == null ? Object.class : value.getClass();
            }
            else {
                if (descriptor.getWriteMethod() == null) { return; // Read-only, skip this property setter
                }
                type = descriptor.getPropertyType();
            }
        }

        // Convert the specified value to the required type
        Object newValue = null;
        if (type.isArray() && index < 0) { // Scalar value into array
            if (value == null) {
                String[] values = new String[1];
                values[0] = null;
                newValue = getConvertUtils().convert(values, type);
            }
            else if (value instanceof String) {
                newValue = getConvertUtils().convert(value, type);
            }
            else if (value instanceof String[]) {
                newValue = getConvertUtils().convert((String[]) value, type);
            }
            else {
                newValue = convert(value, type);
            }
        }
        else if (type.isArray()) { // Indexed value into array
            if (value instanceof String || value == null) {
                newValue = getConvertUtils().convert((String) value,
                                                type.getComponentType());
            }
            else if (value instanceof String[]) {
                newValue = getConvertUtils().convert(((String[]) value)[0],
                                                type.getComponentType());
            }
            else {
                newValue = convert(value, type.getComponentType());
            }
        }
        else { // Value into scalar
            if (value instanceof String) {
                newValue = getConvertUtils().convert((String) value, type);
            }
            else if (value instanceof String[]) {
                newValue = getConvertUtils().convert(((String[]) value)[0],
                                                type);
            }
            else {
                newValue = convert(value, type);
            }
        }

        // Invoke the setter method
        try {
            getPropertyUtils().setProperty(target, name, newValue);
        }
        catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, "Cannot set " + propName);
        }

    }

    /**
     * <p>Convert the value to an object of the specified class (if
     * possible).</p>
     *
     * @param value Value to be converted (may be null)
     * @param type Class of the value to be converted to
     * @return The converted value
     *
     * @exception ConversionException if thrown by an underlying Converter
     * @since 1.8.0
     */
    protected Object convert(Object value, Class type) {
        Converter converter = getConvertUtils().lookup(type);
        if (converter != null) return converter.convert(type, value);
        return value;
    }

    /** 
     * If we're running on JDK 1.4 or later, initialize the cause for the given throwable.
     * 
     * @param  throwable The throwable.
     * @param  cause     The cause of the throwable.
     * @return  true if the cause was initialized, otherwise false.
     * @since 1.8.0
     */
    public boolean initCause(Throwable throwable, Throwable cause) {
        if (INIT_CAUSE_METHOD != null && cause != null) {
            try {
                INIT_CAUSE_METHOD.invoke(throwable, new Object[] { cause });
                return true;
            }
            catch (Throwable e) {
                return false; // can't initialize cause
            }
        }
        return false;
    }

    /** 
     * Gets the <code>ConvertUtilsBean</code> instance used to perform the conversions.
     *
     * @return The ConvertUtils bean instance
     */
    public ConvertUtilsBean getConvertUtils() {
        return convertUtilsBean;
    }

    /**
     * Gets the <code>PropertyUtilsBean</code> instance used to access properties.
     *
     * @return The ConvertUtils bean instance
     */
    public PropertyUtilsBean getPropertyUtils() {
        return propertyUtilsBean;
    }

    /**
     * Returns a <code>Method<code> allowing access to
     * {@link Throwable#initCause(Throwable)} method of {@link Throwable},
     * or <code>null</code> if the method
     * does not exist.
     * 
     * @return A <code>Method<code> for <code>Throwable.initCause</code>, or
     * <code>null</code> if unavailable.
     */
    private static Method getInitCauseMethod() {
        try {
            Class[] paramsClasses = new Class[] { Throwable.class };
            return Throwable.class.getMethod("initCause", paramsClasses);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (Throwable e) {
            return null;
        }
    }
}
