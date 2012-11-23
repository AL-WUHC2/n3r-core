package jaxb.demo.demo3;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.n3r.core.lang.RBaseBean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "学生")
public class Sutdent extends RBaseBean {
    @XmlElement(name = "姓名")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws JAXBException {
        String xml = "<?xml version=\"1.0\" encoding=\"GB2312\" ?>"
                + "<学生>"
                + "<姓名>王小明</姓名>"
                + "</学生>";
        JAXBContext context = JAXBContext.newInstance(Sutdent.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Sutdent s = (Sutdent) unmarshaller.unmarshal(new StringReader(xml));
        System.out.println(s);
    }
}
