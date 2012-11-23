package org.n3r.logback;

import ch.qos.logback.classic.sift.MyAppenderFactory;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AppenderFactoryBase;

public class MultiKeysSiftingAppender extends SiftingAppender {

    @Override
    public void setAppenderFactory(AppenderFactoryBase<ILoggingEvent> appenderFactory) {
        super.setAppenderFactory(new MyAppenderFactory(appenderFactory));
    }
}
