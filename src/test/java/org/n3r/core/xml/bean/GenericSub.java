package org.n3r.core.xml.bean;

import org.n3r.core.lang.RBaseBean;

public class GenericSub<T> extends RBaseBean {

    private T sub;

    public T getSub() {
        return sub;
    }

    public void setSub(T sub) {
        this.sub = sub;
    }

}
