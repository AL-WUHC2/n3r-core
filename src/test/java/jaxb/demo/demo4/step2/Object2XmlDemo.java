package jaxb.demo.demo4.step2;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//Marshaller
public class Object2XmlDemo {
    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setId(100);
        customer.setName("suo");
        customer.setAge(29);

        Book book = new Book();
        book.setId("1");
        book.setName("哈里波特");
        book.setPrice(100);

        Book book2 = new Book();
        book2.setId("2");
        book2.setName("苹果");
        book2.setPrice(50);

        Set<Book> bookSet = new HashSet<Book>();
        bookSet.add(book);
        bookSet.add(book2);

        customer.setBook(bookSet);

        try {
            File file = new File("file1.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(customer, file);
            jaxbMarshaller.marshal(customer, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
