package jaxb.demo.ann;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Customer {

    private String name;
    private int age;
    private int id;
    private Address contact;

    public String getName() {
        return name;
    }

    @XmlElement(name = "NAME")
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    @XmlElement
    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    @XmlAttribute
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d,name=%s,age=%d,contact=%s", getClass().getName(), getId(), getName(), getAge(),
                getContact());
    }

    @XmlElement
    public Address getContact() {
        return contact;
    }

    public void setContact(Address contact) {
        this.contact = contact;
    }

    public String toXML() throws JAXBException {
        //writer，用于保存XML内容
        StringWriter writer = new StringWriter();
        //获取一个关于Customer类的 JAXB 对象
        JAXBContext context = JAXBContext.newInstance(Customer.class);
        //由  Jaxbcontext 得到一个Marshaller（马歇尔）
        Marshaller marshaller = context.createMarshaller();
        //设置为格式化输出，就是XML自动格式化。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        //使用marshaller将对象输出到writer。
        marshaller.marshal(this, writer);
        //writer.toString()，将所有写入的内容转成String
        return writer.toString();
    }

    public static Customer createInstanceFromXML(Reader reader) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        //marshaller是类到XML 的转化，那么 unmashaller是XML到类的转化。
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Customer customer = (Customer) jaxbUnmarshaller.unmarshal(reader);
        return customer;
    }

    public static void main(String[] args) throws Exception {
        Customer customer = new Customer();
        customer.setId(100);
        customer.setName("jaxbExample");
        customer.setAge(20);
        customer.setContact(new Address());
        //将Customer对象输出XML信息
        System.out.println(customer.toXML());
        //从XML文件生成一个Customer对象
        File file = new File("src/test/java/jaxb/demo/ann/jaxbdemo.xml");
        java.io.InputStreamReader sr = new InputStreamReader(new FileInputStream(file));
        Customer cust = Customer.createInstanceFromXML(sr);
        sr.close();
        System.out.println(cust);
    }
}
