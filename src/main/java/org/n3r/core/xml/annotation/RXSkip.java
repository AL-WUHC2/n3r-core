package org.n3r.core.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.n3r.core.xml.utils.RXSkipWhen;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RXSkip {

    RXSkipWhen value() default RXSkipWhen.Absolute;

}
