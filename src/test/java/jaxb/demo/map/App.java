package jaxb.demo.map;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class App {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Foo.class);

        Foo foo = new Foo();
        foo.getHashmap().put(1, "One");
        foo.getHashmap().put(2, "Two");
        foo.getHashmap().put(3, "Three");

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Output the generated XML:
        marshaller.marshal(foo, System.out);

        // Save the output to a foo.xml
        File xmlFile = new File("foo.xml");
        marshaller.marshal(foo, xmlFile);

        // Restore the Foo class from xml file
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Foo createdFoo = (Foo) unmarshaller.unmarshal(xmlFile);

        // See the result
        System.out.println(createdFoo.hashmap);
    }
}
