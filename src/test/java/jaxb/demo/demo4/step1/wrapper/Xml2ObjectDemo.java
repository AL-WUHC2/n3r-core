package jaxb.demo.demo4.step1.wrapper;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

//Unmarshaller
public class Xml2ObjectDemo {
    public static void main(String[] args) throws JAXBException {
        File file = new File("file.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Customer customer = (Customer) jaxbUnmarshaller.unmarshal(file);
        System.out.println(customer);
    }
}
