package org.n3r.esql.impl;

import org.n3r.core.lang.RStr;
import org.n3r.prizedraw.base.PropertyValueComparator;

public class StringPropertyValueComparetor implements PropertyValueComparator {

    public int compare(String propertyValue, String compareValue) {
        return RStr.toStr(propertyValue).compareTo(compareValue);
    }

}
