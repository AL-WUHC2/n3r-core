package org.n3r.core.xml.utils;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.n3r.core.xmltool.XMLTag;

public class RXmlFormatter {

    public static String formatXml(XMLTag xmlTag) {
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
            // ignore
        }
    }

}
