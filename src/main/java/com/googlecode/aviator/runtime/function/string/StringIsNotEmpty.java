package com.googlecode.aviator.runtime.function.string;

import java.util.Map;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

public class StringIsNotEmpty extends AbstractFunction {
    @Override
    public String getName() {
        return "isNotEmpty";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Object obj = FunctionUtils.getJavaObject(arg1, env);
        return obj != null && obj.toString().length() > 0 ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }
}
