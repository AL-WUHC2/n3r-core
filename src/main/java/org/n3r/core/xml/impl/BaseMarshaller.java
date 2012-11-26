package org.n3r.core.xml.impl;

import java.util.ArrayList;
import java.util.List;

import org.n3r.core.xml.FieldsTraverser;
import org.n3r.core.xml.XMarshalAware;
import org.n3r.core.xml.utils.RXFeature;
import org.n3r.core.xmltool.XMLTag;

import static org.n3r.core.xmltool.XMLDoc.*;

public abstract class BaseMarshaller extends FieldsTraverser implements XMarshalAware {

    protected List<RXFeature> features = new ArrayList<RXFeature>();
    protected boolean enableFeature;

    public BaseMarshaller addFeatures(List<RXFeature> features) {
        this.features.addAll(features);
        return this;
    }

    public BaseMarshaller setEnableFeature(boolean enableFeature) {
        this.enableFeature = enableFeature;
        return this;
    }

    protected XMLTag buildCurrentTag(String tagName, XMLTag parent) {
        return parent == null ? newDocument(true).addRoot(tagName) : tagName == null ? parent : parent.addTag(tagName);
    }

    protected void writeObjectClass(Class<?> clazz, XMLTag tag) {
        if (enableFeature && features.contains(RXFeature.WrithObjectClass)) tag.addAttribute("_type_", clazz
                .getCanonicalName());
    }

}
