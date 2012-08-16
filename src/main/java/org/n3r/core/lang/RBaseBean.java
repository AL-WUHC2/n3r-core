package org.n3r.core.lang;

import org.n3r.core.lang3.builder.EqualsBuilder;
import org.n3r.core.lang3.builder.HashCodeBuilder;
import org.n3r.core.lang3.builder.ToStringBuilder;

public class RBaseBean {

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
