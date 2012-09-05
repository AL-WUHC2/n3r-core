package org.n3r.prizedraw.checker;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.joor.Reflect;
import org.n3r.esql.map.AfterProperitesSet;
import org.n3r.prizedraw.base.PropertyValueComparator;
import org.n3r.prizedraw.impl.StringPropertyValueComparetor;

public class DefaultPrizeCheck implements AfterProperitesSet {
    private String activityId;
    private String checkProperty;
    private boolean notexistsTag;
    private String checkValue;
    private String checkCond;
    private String comparator;
    private String remark;
    private PropertyValueComparator propertyValueComparator;

    @Override
    public void afterPropertiesSet() {
        propertyValueComparator = StringUtils.isEmpty(comparator)
                ? new StringPropertyValueComparetor()
                : Reflect.on(comparator).create().<PropertyValueComparator> get();
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCheckProperty() {
        return checkProperty;
    }

    public void setCheckProperty(String checkProperty) {
        this.checkProperty = checkProperty;
    }

    public boolean isNotexistsTag() {
        return notexistsTag;
    }

    public void setNotexistsTag(boolean notexistsTag) {
        this.notexistsTag = notexistsTag;
    }

    public String getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    public String getCheckCond() {
        return checkCond;
    }

    public void setCheckCond(String checkCond) {
        this.checkCond = checkCond;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public PropertyValueComparator getPropertyValueComparator() {
        return propertyValueComparator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
