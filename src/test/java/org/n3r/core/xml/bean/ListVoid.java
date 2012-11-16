package org.n3r.core.xml.bean;

import java.util.List;

import org.n3r.core.lang.RBaseBean;

public class ListVoid extends RBaseBean {

    private List items;

    public void setItems(List items) {
        this.items = items;
    }

    public List getItems() {
        return items;
    }

}
