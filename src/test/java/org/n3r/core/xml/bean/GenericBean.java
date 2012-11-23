package org.n3r.core.xml.bean;

import java.util.List;

import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXCData;

public class GenericBean<T> extends RBaseBean {

    private T genericField;
    private NormalBean normal;
    private GenericSub<T> subField;
    private List<T> beans;
    private List<NormalBean> normal2s;
    @RXCData
    private T cdataField;
    @RXCData
    private GenericSub<T> genericCData;

    public T getGenericField() {
        return genericField;
    }

    public void setGenericField(T genericField) {
        this.genericField = genericField;
    }

    public NormalBean getNormal() {
        return normal;
    }

    public void setNormal(NormalBean normal) {
        this.normal = normal;
    }

    public GenericSub<T> getSubField() {
        return subField;
    }

    public void setSubField(GenericSub<T> subField) {
        this.subField = subField;
    }

    public List<T> getBeans() {
        return beans;
    }

    public void setBeans(List<T> beans) {
        this.beans = beans;
    }

    public List<NormalBean> getNormal2s() {
        return normal2s;
    }

    public void setNormal2s(List<NormalBean> normal2s) {
        this.normal2s = normal2s;
    }

    public T getCdataField() {
        return cdataField;
    }

    public void setCdataField(T cdataField) {
        this.cdataField = cdataField;
    }

    public void setGenericCData(GenericSub<T> genericCData) {
        this.genericCData = genericCData;
    }

    public GenericSub<T> getGenericCData() {
        return genericCData;
    }

}
