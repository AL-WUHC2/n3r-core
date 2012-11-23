package jaxb.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 *  XML文件校验及与java对象相互映射的工具
 */
public class XMLUtil {
    //  private static Logger logger = LoggerFactory.getLogger(XMLUtil.class);//slf4j logging

    /**
     * 按照指定的上下文包路径构建实例
     * @param contextPath
     * @throws JAXBException
     */
    public XMLUtil(String contextPath) throws JAXBException {
        resetContext(contextPath);
    }

    /**
     * 按照指定的上下文包路径构重新设置上下文
     * @param contextPath
     * @throws JAXBException
     */
    public void resetContext(String contextPath) throws JAXBException {
        jaxbContext = JAXBContext.newInstance(contextPath);
        unmarshaller = jaxbContext.createUnmarshaller();
        marshaller = jaxbContext.createMarshaller();
    }

    /**
     * 根据xsd文件校验一个xml文件是否有效
     * @param xmlFilePath
     * @param xsdFilePath
     * @return true-有效 false-无效
     * @throws IOException
     * @throws SAXException
     */
    public static boolean validate(String xmlFilePath, String xsdFilePath) throws IOException, SAXException {
        return XMLUtil.doValidate(xmlFilePath, xsdFilePath, null);
    }

    /**
     * 根据xsd文件校验一个xml文件是否有效，当无效异常时交由errorHandler处理异常
     * @param xmlFilePath
     * @param xsdFilePath
     * @param errorHandler
     * @throws IOException
     * @throws SAXException
     */
    public static void validate(String xmlFilePath, String xsdFilePath, ErrorHandler errorHandler) throws IOException,
            SAXException {
        XMLUtil.doValidate(xmlFilePath, xsdFilePath, errorHandler);
    }

    /**
     * 根据xsd文件校验一个xml文件是否有效，当无效异常时交由errorHandler处理异常
     *
     * @param xmlFilePath
     * @param xsdFilePath
     * @param errorHandler
     * @return
     * @throws IOException
     * @throws SAXException
     */
    private static boolean doValidate(String xmlFilePath, String xsdFilePath,
            ErrorHandler errorHandler) throws IOException, SAXException {
        boolean rt = false;
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // 2. Compile the schema.
        // Here the schema is loaded from a java.io.File, but you could use
        // a java.net.URL or a javax.xml.transform.Source instead.
        File schemaLocation = new File(xsdFilePath);
        Schema schema = factory.newSchema(schemaLocation);

        // 3. Get a validator from the schema.
        Validator validator = schema.newValidator();
        validator.setErrorHandler(errorHandler);

        // 4. Parse the document you want to check.
        Source source = new StreamSource(xmlFilePath);

        // 5. Check the document
        try {
            validator.validate(source);
            rt = true;
        } catch (SAXException ex) {
            rt = false;
        }
        return rt;
    }

    /**
     * 将java对象映射到xml文件中
     * @param jaxbElement
     * @param outputXMLFile
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public void marshal(Object jaxbElement, String outputXMLFile)
            throws FileNotFoundException, JAXBException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                new Boolean(true));
        marshaller.marshal(jaxbElement,
                new FileOutputStream(outputXMLFile));
    }

    /**
     * 将java对象映射为输出流
     * @param jaxbElement
     * @param os 输入输出参数
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public void marshal(Object jaxbElement, OutputStream os)
            throws FileNotFoundException, JAXBException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                new Boolean(true));
        marshaller.marshal(jaxbElement, os);
    }

    /**
     * 将java对象映射为DOM node
     * @param jaxbElement
     * @param node_output 输入输出参数
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public void marshal(Object jaxbElement, Node node_output)
            throws FileNotFoundException, JAXBException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                new Boolean(true));
        marshaller.marshal(jaxbElement, node_output);
    }

    /**
     * 将xml文件映射为java对象
     * @param inputXMLFile
     * @return
     * @throws JAXBException
     */
    public Object unmarshal(String inputXMLFile) throws JAXBException {
        return unmarshaller.unmarshal(new File(inputXMLFile));
    }

    /**
     * 将DOM node映射为java对象
     * @param node
     * @return
     * @throws JAXBException
     */
    public Object unmarshal(Node node) throws JAXBException {
        return unmarshaller.unmarshal(node);
    }

    /**
     * 将输入流映射为java对象
     * @param is
     * @return
     * @throws JAXBException
     */
    public Object unmarshal(InputStream is) throws JAXBException {
        return unmarshaller.unmarshal(is);
    }

    //jaxb用到的变量
    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;

    /**
     * @param args
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    public static void main(String[] args) throws JAXBException, IOException, SAXException {
        XMLUtil util = new XMLUtil("amos.note");
        //验证
        boolean flag = XMLUtil.validate("src/test/resources/jaxb/demo/t.xml", "src/test/resources/jaxb/demo/t.xsd");
        System.out.println(flag);
        //映射为java对象
        Object obj = util.unmarshal("src/test/resources/jaxb/demo/t.xml");
        //更新
        Note note = (Note) obj;
        note.setBody("hello,this is new xml file");
        //映射为xml文件
        util.marshal(note, "src/test/resources/jaxb/demo//t.xml");
    }
}
