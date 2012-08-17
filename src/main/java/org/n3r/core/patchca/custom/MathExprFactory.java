package org.n3r.core.patchca.custom;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;
import org.n3r.core.text.RRand;

public class MathExprFactory implements WordFactory {
    private static String[] operations = new String[] { "加+", "减", "乘X" };
    private static char[] exprOperations = new char[] { '+', '-', '*' };

    public char getOperation(int index) {
        String operation = operations[index];
        return operation.charAt(RRand.randInt(operation.length()));
    }

    private static ScriptEngine engine;
    static {
        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");
    }

    private String evalExpr(String expr) {
        try {
            return "" + ((Double) engine.eval(expr)).intValue();
        }
        catch (ScriptException e) {
            return "error";
        }
    }

    @Override
    public WordBean getNextWord() {
        int a = RRand.randInt(9) + 1;
        int b = RRand.randInt(9) + 1;
        int c = RRand.randInt(9) + 1;

        int op1 = RRand.randInt(operations.length);
        int op2 = RRand.randInt(operations.length);

        StringBuilder answerExpr = new StringBuilder();
        answerExpr.append(a).append(exprOperations[op1]).append(b).append(exprOperations[op2]).append(c);
        String answer = evalExpr(answerExpr.toString());

        StringBuilder word = new StringBuilder();
        word.append(MathArithmeticFactory.rand(a)).append(getOperation(op1)).append(b).append(getOperation(op2))
                .append(c).append("=?");

        return new WordBean(word.toString(), answer,"请输入图片中?代表的数字");
    }

    @Override
    public String[] getSupportedFontFamilies() {
        return new String[]{"宋体"};
    }

}
