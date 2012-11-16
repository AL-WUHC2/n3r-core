package org.n3r.core.xml.bean;

import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXTransient;

public class Person extends RBaseBean {
    private String name;
    private int age;
    @RXTransient
    private String remark;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}
