package jaxb.demo.demo4.step4;

import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class BookSet {

    private HashSet<Book> bookSet = new HashSet<Book>();

    //仅包含get方法，未包含set方法
    @XmlElementWrapper(name = "bookSet")
    //该注解非必须，仅是标注集合元素
    @XmlElement(name = "book")
    public HashSet<Book> getBookSet() {
        return bookSet;
    }

    public void addBook(Book book) {
        bookSet.add(book);
    }

}
