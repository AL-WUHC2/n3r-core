package org.n3r.core.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.n3r.config.Config;
import org.n3r.core.lang.RClassPath;

import static org.apache.commons.lang3.Validate.*;

public class RJaxb {

    private static List<Class<?>> xmlRootClasses;
    private static JAXBContext jaxbContext = null;
    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;

    static {
        xmlRootClasses = RClassPath.getAnnotatedClasses(Config.getStr("XmlBeansPkg", "org.n3r"), XmlRootElement.class);
        Class<?>[] xmlRootClassesArray = xmlRootClasses.toArray(new Class<?>[0]);

        try {
            jaxbContext = JAXBContext.newInstance(xmlRootClassesArray);
            marshaller = jaxbContext.createMarshaller();
            unmarshaller = jaxbContext.createUnmarshaller();
        }
        catch (JAXBException e) {
            notNull(jaxbContext, "Load XmlRootElement classes Failed.");
            notNull(marshaller, "Create Marshaller Failed.");
            notNull(unmarshaller, "Create Unmarshaller Failed.");
        }
    }

    public static String bean2Xml(Object obj) throws JAXBException {
        StringWriter sw = new StringWriter();

        getMarshaller(obj.getClass()).marshal(obj, sw);

        return sw.toString();
    }

    public static <T> T xml2Bean(String xml, Class<T> clz) throws JAXBException {
        StringReader sr = new StringReader(xml);

        return clz.cast(getUnmarshaller(clz).unmarshal(sr));
    }

    private static void checkXmlAnnotation(Class<?> clz) {
        XmlRootElement rootAnno = clz.getAnnotation(XmlRootElement.class);
        notNull(rootAnno, "Class %s is not annotated with @XmlRootElement.", clz.getName());
    }

    private static Marshaller getMarshaller(Class<?> clz) throws JAXBException {
        if (xmlRootClasses.contains(clz)) return marshaller;

        checkXmlAnnotation(clz);

        return JAXBContext.newInstance(clz).createMarshaller();
    }

    private static Unmarshaller getUnmarshaller(Class<?> clz) throws JAXBException {
        if (xmlRootClasses.contains(clz)) return unmarshaller;

        checkXmlAnnotation(clz);

        return JAXBContext.newInstance(clz).createUnmarshaller();
    }

}
