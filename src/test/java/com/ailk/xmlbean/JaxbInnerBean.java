package com.ailk.xmlbean;

import org.n3r.core.lang.RBaseBean;

public class JaxbInnerBean extends RBaseBean {

    private String attrStr;

    private int attrInt;

    public void setAttrStr(String attrStr) {
        this.attrStr = attrStr;
    }

    public String getAttrStr() {
        return attrStr;
    }

    public void setAttrInt(int attrInt) {
        this.attrInt = attrInt;
    }

    public int getAttrInt() {
        return attrInt;
    }

}
