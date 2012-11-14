package com.ailk.xmlbean;

import javax.xml.bind.annotation.XmlRootElement;

import org.n3r.core.lang.RBaseBean;

@XmlRootElement
public class JaxbBean extends RBaseBean {

    private JaxbInnerBean inner;

    public void setInner(JaxbInnerBean inner) {
        this.inner = inner;
    }

    public JaxbInnerBean getInner() {
        return inner;
    }

}
