package org.n3r.core.xml.bean;

public class PersonWithId extends Person {
    protected String idCode;

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getIdCode() {
        return idCode;
    }
}
