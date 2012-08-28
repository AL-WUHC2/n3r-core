package org.n3r.core.lang;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RTypeRef<T> {

    private final Type type;

    protected RTypeRef(){
        Type superClass = getClass().getGenericSuperclass();

        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
    
}
