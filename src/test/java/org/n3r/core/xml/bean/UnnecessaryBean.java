package org.n3r.core.xml.bean;

import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXRootElement;
import org.n3r.core.xml.annotation.RXSkip;
import org.n3r.core.xml.utils.RXSkipWhen;

@RXRootElement("Root")
public class UnnecessaryBean extends RBaseBean {

    private String name;
    @RXSkip(RXSkipWhen.Null)
    private String nickName;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

}
