package jaxb.demo;

//
//This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
//See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
//Any modifications to this file will be lost upon recompilation of the source schema.
//Generated on: 2011.12.31 at 10:41:52 上午 CST
//

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
* This object contains factory methods for each
* Java content interface and Java element interface
* generated in the amos.note package.
* <p>An ObjectFactory allows you to programatically
* construct new instances of the Java representation
* for XML content. The Java representation of XML
* content can consist of schema derived interfaces
* and classes representing the binding of schema
* type definitions, element declarations and model
* groups.  Factory methods for each of these are
* provided in this class.
*
*/
@XmlRegistry
public class ObjectFactory {

    private final static QName _To_QNAME = new QName("http://www.w3school.com.cn", "to");
    private final static QName _Body_QNAME = new QName("http://www.w3school.com.cn", "body");
    private final static QName _From_QNAME = new QName("http://www.w3school.com.cn", "from");
    private final static QName _Heading_QNAME = new QName("http://www.w3school.com.cn", "heading");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: amos.note
     *
     */
    public ObjectFactory() {}

    /**
     * Create an instance of {@link Note }
     *
     */
    public Note createNote() {
        return new Note();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.w3school.com.cn", name = "to")
    public JAXBElement<String> createTo(String value) {
        return new JAXBElement<String>(_To_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.w3school.com.cn", name = "body")
    public JAXBElement<String> createBody(String value) {
        return new JAXBElement<String>(_Body_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.w3school.com.cn", name = "from")
    public JAXBElement<String> createFrom(String value) {
        return new JAXBElement<String>(_From_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.w3school.com.cn", name = "heading")
    public JAXBElement<String> createHeading(String value) {
        return new JAXBElement<String>(_Heading_QNAME, String.class, null, value);
    }

}
