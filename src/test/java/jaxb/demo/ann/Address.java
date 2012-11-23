package jaxb.demo.ann;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Address {
    private String addr;
    private String postno;
    private int id;

    @Override
    public String toString() {
        return String.format("Address[%s,%s,%d]", addr, postno, id);
    }

    public Address() {
        setAddr("default");
        setPostno("000000");
        setId(1);
    }

    @XmlElement
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @XmlElement
    public String getPostno() {
        return postno;
    }

    public void setPostno(String postno) {
        this.postno = postno;
    }

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
