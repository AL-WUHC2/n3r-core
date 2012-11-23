package jaxb.demo.demo4.step3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class Customer<T> {
    String name;
    int age;
    int id;
    T t;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ",name=" + name + ",age=" + age + ",t=" + t + "]";
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

}
