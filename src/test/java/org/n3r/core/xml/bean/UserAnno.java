package org.n3r.core.xml.bean;

import java.util.List;

import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXRootElement;

@RXRootElement("Home")
public class UserAnno extends RBaseBean {
    private Person personInfo;
    @RXElement("Family")
    private List<Person> friends;

    public void setPersonInfo(Person personInfo) {
        this.personInfo = personInfo;
    }

    public Person getPersonInfo() {
        return personInfo;
    }

    public void setFriends(List<Person> friends) {
        this.friends = friends;
    }

    public List<Person> getFriends() {
        return friends;
    }

}
