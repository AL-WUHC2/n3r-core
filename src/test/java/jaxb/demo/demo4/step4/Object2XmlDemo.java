package jaxb.demo.demo4.step4;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//Marshaller
public class Object2XmlDemo {
    public static void main(String[] args) throws JAXBException {

        Customer<BookSet> customer = new Customer<BookSet>();
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

        BookSet bookSet = new BookSet();
        bookSet.addBook(book);
        bookSet.addBook(book2);

        customer.setT(bookSet);

        File file = new File("file1.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class,
                BookSet.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(customer, file);
        jaxbMarshaller.marshal(customer, System.out);
    }
}
