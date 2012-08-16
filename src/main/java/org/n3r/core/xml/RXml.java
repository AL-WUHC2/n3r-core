package org.n3r.core.xml;

import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RField;
import org.n3r.core.xmltool.XMLDoc;
import org.n3r.core.xmltool.XMLDocumentException;
import org.n3r.core.xmltool.XMLTag;

public class RXml {
    /**
     * Convert a java bean to XML presentation with bean's class as root name.
     * @param bean java bean
     * @return XML string
     */
    public static String toXML(Object bean) {
        return toXML(bean, bean.getClass().getSimpleName());
    }

    /**
     * Convert a java bean to XML presentation.
     * @param bean java bean
     * @param rootTagName root tag's name
     * @return XML string
     */
    public static String toXML(Object bean, String rootTagName) {
        XMLTag xmlTag = XMLDoc.newDocument(false).addRoot(rootTagName);
        for (Field f : bean.getClass().getDeclaredFields()) {
            if (RField.isNotNormal(f)) continue;

            String fieldValue = "" + Reflect.on(bean).get(f.getName());
            xmlTag.addTag(f.getName()).addText(fieldValue);
        }

        return toXML(xmlTag);
    }

    private static String toXML(XMLTag xmlTag) {
        StringWriter out = new StringWriter();
        transform(xmlTag.toSource(), new StreamResult(out));
        return out.toString();
    }

    private static void transform(Source xmlSource, Result out) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(xmlSource, out);
        }
        catch (TransformerException e) {
            throw new XMLDocumentException("Transformation error", e);
        }
    }
}
