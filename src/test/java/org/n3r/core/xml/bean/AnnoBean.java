package org.n3r.core.xml.bean;

import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXRootElement;

@RXRootElement("root")
public class AnnoBean extends RBaseBean {

    @RXElement("branch")
    private String param1;

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam1() {
        return param1;
    }

}
