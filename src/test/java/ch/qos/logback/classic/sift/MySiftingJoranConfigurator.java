package ch.qos.logback.classic.sift;

import java.util.Map;

import org.slf4j.MDC;

import ch.qos.logback.core.joran.spi.InterpretationContext;

public class MySiftingJoranConfigurator extends SiftingJoranConfigurator {
    public MySiftingJoranConfigurator(String key, String value) {
        super(key, value);
    }

    @Override
    protected void buildInterpreter() {
        super.buildInterpreter();

        @SuppressWarnings("unchecked") Map<String, String> mdcPropertyMap = MDC.getMDCAdapter().getCopyOfContextMap();
        if (mdcPropertyMap == null) return;

        InterpretationContext interpretationContext = interpreter.getInterpretationContext();
        for (Map.Entry<String, String> entry : mdcPropertyMap.entrySet())
            interpretationContext.addSubstitutionProperty(entry.getKey(), entry.getValue());
    }
}
