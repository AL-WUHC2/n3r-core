package org.n3r.logback;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.google.common.base.Splitter;

public class MDCBasedMultiKeysDiscriminator extends MDCBasedDiscriminator {
    private String splitter;

    @Override
    public String getDiscriminatingValue(ILoggingEvent event) {
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (mdcMap == null) return getDefaultValue();

        Iterable<String> keys = Splitter.on(getSplitter()).omitEmptyStrings().trimResults().split(getKey());
        StringBuilder values = new StringBuilder();
        for (String key : keys) {
            String value = mdcMap.get(key);
            values.append(value == null ? getDefaultValue() : value).append(getSplitter());
        }

        return StringUtils.removeEnd(values.toString(), getSplitter());

    }

    public String getSplitter() {
        return splitter;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

}
