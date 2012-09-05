package com.googlecode.aviator.script;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import com.googlecode.aviator.AviatorEvaluator;


/**
 * 
 * @author libinsong1204@gmail.com
 * @author dennis
 * @date 2011-1-18 上午11:03:34
 * @version
 */
public class AviatorScriptEngineFactory implements ScriptEngineFactory {

    private static final List<String> extensions = Collections.unmodifiableList(Arrays.asList(new String[] {}));
    private static final List<String> mimeTypes =
            Collections.unmodifiableList(Arrays.asList(new String[] { "text/aviator" }));
    private static final List<String> names =
            Collections.unmodifiableList(Arrays.asList(new String[] { "Aviator", "aviator" }));

    private static final Map<String, String> parameterMap = new HashMap<String, String>();
    static {
        parameterMap.put(ScriptEngine.ENGINE, "Aviator");
        parameterMap.put(ScriptEngine.ENGINE_VERSION, AviatorEvaluator.VERSION);
        parameterMap.put(ScriptEngine.LANGUAGE, "A high performance expression evaluator for java");
        parameterMap.put(ScriptEngine.LANGUAGE_VERSION, AviatorEvaluator.VERSION);
    }


    public String getEngineName() {
        return parameterMap.get(ScriptEngine.ENGINE);
    }


    public String getEngineVersion() {
        return parameterMap.get(ScriptEngine.ENGINE_VERSION);
    }


    public List<String> getExtensions() {
        return extensions;
    }


    public String getLanguageName() {
        return parameterMap.get(ScriptEngine.LANGUAGE);
    }


    public String getLanguageVersion() {
        return parameterMap.get(ScriptEngine.LANGUAGE_VERSION);
    }


    public String getMethodCallSyntax(String obj, String m, String... args) {
        StringBuilder sb = new StringBuilder(m);
        sb.append("(").append(obj);
        if (args != null) {
            for (String s : args) {
                sb.append(",").append(s);
            }
        }
        sb.append(")");
        return sb.toString();

    }


    public List<String> getMimeTypes() {
        return mimeTypes;
    }


    public List<String> getNames() {
        return names;
    }


    public String getOutputStatement(String toDisplay) {
        return "print(+" + toDisplay + ")";
    }


    public Object getParameter(String key) {
        return parameterMap.get(key);
    }


    public String getProgram(String... statements) {
        return null;
    }


    public ScriptEngine getScriptEngine() {
        return new AviatorScriptEngine(this);
    }

}
