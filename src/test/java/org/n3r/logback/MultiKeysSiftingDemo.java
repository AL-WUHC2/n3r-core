package org.n3r.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class MultiKeysSiftingDemo {
    private static final Logger LOG = LoggerFactory.getLogger(MultiKeysSiftingDemo.class);

    public static void main(String[] args) {
        MDC.put("eopkey", "bingoo");
        MDC.put("key2", "huang");
        LOG.info("bingoo huang is here!");
        MDC.put("eopkey", "bingoo");
        MDC.put("key2", "huang1");
        LOG.info("bingoo huang is here!");
        MDC.put("eopkey", "bingoo");
        MDC.put("key2", "huang2");
        LOG.info("bingoo huang is here!");
    }
}
