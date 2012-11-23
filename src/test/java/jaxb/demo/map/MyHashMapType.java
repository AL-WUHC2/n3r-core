package jaxb.demo.map;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MyHashMapType {

    @XmlElement(required = true)
    public List<MyHashEntryType> entries = new ArrayList<MyHashEntryType>();
}
