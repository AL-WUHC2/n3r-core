package org.n3r.core.xml.bean;

import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXNonWrap;
import org.n3r.core.xml.annotation.RXRootElement;

@RXRootElement("Root")
public class UserNonWrap extends RBaseBean {

    @RXNonWrap
    private Person person;
    private String id;

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
