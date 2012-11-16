package org.n3r.core.xml.bean;

import java.util.List;

import org.n3r.core.lang.RBaseBean;

public class User extends RBaseBean {
    private Person personInfo;
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
