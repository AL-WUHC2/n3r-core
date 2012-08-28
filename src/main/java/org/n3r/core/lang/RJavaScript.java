package org.n3r.core.lang;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

public class RJavaScript {
    static class RJavaScriptEngine {
        ScriptEngine jsEngine;
        Compilable compilableEngine;
        Map<String, CompiledScript> cache;
        Bindings bindings;
        Map<String, Object> lastParams;

        public RJavaScriptEngine() {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            jsEngine = scriptEngineManager.getEngineByName("js");
            bindings = jsEngine.createBindings();
            if (jsEngine instanceof Compilable) {
                compilableEngine = (Compilable) jsEngine;
                cache = new HashMap<String, CompiledScript>();
            }
        }
    }

    private static ThreadLocal<RJavaScriptEngine> threadLocal = new ThreadLocal<RJavaScriptEngine>() {
        @Override
        protected RJavaScriptEngine initialValue() {
            return new RJavaScriptEngine();
        }

    };

    public static RJavaScriptEngine getEngine() {
        RJavaScriptEngine engine = threadLocal.get();

        return engine;
    }

    public static Map<String, Object> createBinds(Map<String, Object> params) {
        Bindings bindings = getEngine().bindings;
        if (getEngine().lastParams != null) {
            for (String key : getEngine().lastParams.keySet())
                bindings.remove(key);
        }

        getEngine().lastParams = params;
        bindings.putAll(params);

        return bindings;
    }

    public static Object eval(String script) {
        if (StringUtils.isBlank(script)) return null;

        compile(script);

        CompiledScript compiledScript = getEngine().cache.get(script);

        try {
            return compiledScript != null ? compiledScript.eval(getEngine().bindings)
                    : getEngine().jsEngine.eval(script, getEngine().bindings);
        }
        catch (ScriptException ex) {
            throw new RuntimeException(script, ex);
        }
    }

    public static void compile(String script) {
        if (getEngine().cache == null) return;

        CompiledScript compiledScript = getEngine().cache.get(script);
        if (compiledScript == null) {
            try {
                compiledScript = getEngine().compilableEngine.compile(script);
            }
            catch (ScriptException ex) {
                throw new RuntimeException(script, ex);
            }
            getEngine().cache.put(script, compiledScript);
        }
    }
}
