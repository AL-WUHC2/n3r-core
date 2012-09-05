package com.googlecode.aviator.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.googlecode.aviator.Expression;

/**
 *
 * @author libinsong1204@gmail.com
 * @date 2011-1-18 上午11:03:34
 * @version
 */
public class CompiledAviatorScript extends CompiledScript {

    private final AviatorScriptEngine engine;
    private final Expression expression;

    CompiledAviatorScript(AviatorScriptEngine engine, Expression expression) {
        this.engine = engine;
        this.expression = expression;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object eval(ScriptContext context) throws ScriptException {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            for (Integer integer : context.getScopes()) {
                int scope = integer.intValue();
                Bindings bindings = context.getBindings(scope);
                Set<String> keys = bindings.keySet();

                for (String key : keys)
                    map.put(key, bindings.get(key));
            }
            return expression.execute(map);
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }

}
