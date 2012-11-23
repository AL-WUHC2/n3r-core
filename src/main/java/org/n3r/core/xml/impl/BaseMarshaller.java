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

    public BaseMarshaller addFeatures(List<RXFeature> features) {
        this.features.addAll(features);
        return this;
    }

    protected XMLTag buildCurrentTag(String tagName, XMLTag parent) {
        return parent == null ? newDocument(false).addRoot(tagName) : tagName == null ? parent : parent.addTag(tagName);
    }

    protected void writeObjectClass(Class<?> clazz, XMLTag tag) {
        if (features.contains(RXFeature.WrithObjectClass)) tag.addAttribute("_type_", clazz.getCanonicalName());
    }

}
