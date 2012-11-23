package jaxb.demo.withoutann;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

// 在JAXB中，其实不用annotation也是可以的
public class Demo {
    public static void main(String[] args) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Customer.class);

        StreamSource xml = new StreamSource("src/test/java/jaxb/demo/withoutann/input.xml");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<Customer> je1 = unmarshaller.unmarshal(xml, Customer.class);
        Customer customer = je1.getValue();
        System.out.println(customer);

        JAXBElement<Customer> je2 = new JAXBElement<Customer>(new QName("customer"), Customer.class, customer);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(je2, System.out);
    }

}
