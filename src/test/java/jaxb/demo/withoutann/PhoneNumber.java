package jaxb.demo.withoutann;

import org.n3r.core.lang.RBaseBean;

public class PhoneNumber extends RBaseBean {
    private String foo;
    private String bar;

    public String getType() {
        return foo;
    }

    public void setType(String type) {
        foo = type;
    }

    public String getNumber() {
        return bar;
    }

    public void setNumber(String number) {
        bar = number;
    }
}
