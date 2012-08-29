package org.n3r.core.lang;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.Test;

public class RJavaScriptTest {

    @Test
    public void test1() {
        RJavaScript.eval("function isNotEmpty(str) { return str == null || str == ''; }");
        Object eval = RJavaScript.eval("isNotEmpty('a')");
        System.out.println(eval);
        System.out.println(Thread.currentThread().getId());
    }

    @Test
    public void test2() {
        System.out.println(Thread.currentThread().getId());
        Object eval = RJavaScript.eval("isNotEmpty('a')");
        System.out.println(eval);

    }

    @Test
    public void test3() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        engine.put("x", "hello");
        // print global variable "x"
        engine.eval("println(x);");
        Bindings createBindings = engine.createBindings();
        engine.eval("function add(a,b) { return a + b; }", createBindings);
//        System.out.println(engine.eval("add(1,2)"));
        
        createBindings.put("x", "hello");
//        createBindings.clear();
        System.out.println(createBindings.keySet());
        System.out.println(engine.eval("add(1,2)", createBindings));
        // the above line prints "hello"

        // Now, pass a different script context
        ScriptContext newContext = new SimpleScriptContext();
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        // add new variable "x" to the new engineScope        
        engineScope.put("x", "world");

        // execute the same script - but this time pass a different script context
        engine.eval("println(x);", newContext);
        // the above line prints "world"
    }
}
