/**
 *  Copyright (C) 2010 dennis zhuang (killme2008@gmail.com)
 *
 *  This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 **/
package com.googlecode.aviator.code;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.LiteralExpression;
import com.googlecode.aviator.code.asm.ASMCodeGenerator;
import com.googlecode.aviator.lexer.token.*;
import com.googlecode.aviator.lexer.token.DelegateToken.DelegateTokenType;
import com.googlecode.aviator.lexer.token.Token.TokenType;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorPattern;
import com.googlecode.aviator.runtime.type.AviatorString;

/**
 * Optimized code generator
 *
 * @author dennis
 *
 */
public class OptimizeCodeGenerator implements CodeGenerator {
    private final ASMCodeGenerator asmCodeGenerator;

    private final List<Token<?>> tokenList = new ArrayList<Token<?>>();

    private boolean trace = false;

    public OptimizeCodeGenerator(ClassLoader classLoader, OutputStream traceOutStream, boolean trace) {
        asmCodeGenerator = new ASMCodeGenerator(AviatorEvaluator.getAviatorClassLoader(), traceOutStream, trace);
        this.trace = trace;

    }

    private Map<Integer, DelegateTokenType> getIndex2DelegateTypeMap(OperatorType opType) {
        Map<Integer, DelegateTokenType> result = new HashMap<Integer, DelegateTokenType>();
        switch (opType) {
        case AND:
            result.put(2, DelegateTokenType.And_Left);
            break;
        case OR:
            result.put(2, DelegateTokenType.Join_Left);
            break;
        case TERNARY:
            result.put(4, DelegateTokenType.Ternary_Boolean);
            result.put(2, DelegateTokenType.Ternary_Left);
            break;
        default:
            break;
        }
        return result;
    }

    private int execute() {
        int exeCount = 0;
        final int size = tokenList.size();
        printTokenList();
        for (int i = 0; i < size; i++) {
            Token<?> token = tokenList.get(i);
            if (token.getType() == TokenType.Operator) {
                final OperatorToken op = (OperatorToken) token;
                final OperatorType operatorType = op.getOperatorType();
                final int operandCount = operatorType.getOperandCount();
                switch (operatorType) {
                case FUNC:
                case INDEX:
                    // Could not optimize function and index call
                    break;
                default:
                    Map<Integer, DelegateTokenType> index2DelegateType = getIndex2DelegateTypeMap(operatorType);
                    final int result = executeOperator(i, operatorType, operandCount, index2DelegateType);
                    if (result < 0) {
                        compactTokenList();
                        return exeCount;
                    }
                    exeCount += result;
                    break;
                }

            }
        }
        compactTokenList();
        return exeCount;
    }

    private int executeOperator(int operatorIndex, final OperatorType operatorType, int operandCount,
            Map<Integer, DelegateTokenType> index2DelegateType) {
        Token<?> token = null;
        operandCount += index2DelegateType.size();
        // check if literal expression can be executed
        boolean canExecute = true;
        // operand count
        int count = 0;
        // operand start index
        int operandStartIndex = -1;
        for (int j = operatorIndex - 1; j >= 0; j--) {
            token = tokenList.get(j);
            if (token == null) // we must compact token list and retry executing
                return -1;
            final TokenType tokenType = token.getType();
            // Check if operand is a literal operand
            if (!isLiteralOperand(token, tokenType, count + 1, index2DelegateType)) {
                canExecute = false;
                break;
            }
            count++;

            if (count == operandCount) {
                operandStartIndex = j;
                break;
            }
        }

        // if we can execute it on compile
        if (canExecute) {
            // arguments
            AviatorObject[] args = new AviatorObject[operandCount];
            int index = 0;
            for (int j = operandStartIndex; j < operatorIndex; j++) {
                token = tokenList.get(j);
                if (token.getType() == TokenType.Delegate) {
                    tokenList.set(j, null);
                    continue;
                }
                args[index++] = getAviatorObjectFromToken(token);
                // set argument token to null
                tokenList.set(j, null);

            }
            // execute it now
            AviatorObject result = operatorType.eval(args);
            // set result as token to tokenList for next executing
            tokenList.set(operatorIndex, getTokenFromOperand(result));
            return 1;
        }
        return 0;
    }

    private boolean isLiteralOperand(Token<?> token, final TokenType tokenType, int index,
            Map<Integer, DelegateTokenType> index2DelegateType) {
        switch (tokenType) {
        case Variable:
            return token == Variable.TRUE || token == Variable.FALSE || token == Variable.NIL;
        case Delegate:
            DelegateTokenType targetDelegateTokenType = index2DelegateType.get(index);
            if (targetDelegateTokenType != null)
                return targetDelegateTokenType == ((DelegateToken) token).getDelegateTokenType();
            break;
        case Char:
        case Number:
        case Pattern:
        case String:
            return true;
        default:
            break;
        }
        return false;
    }

    private boolean isLiteralToken(Token<?> token) {
        switch (token.getType()) {
        case Variable:
            return token == Variable.TRUE || token == Variable.FALSE || token == Variable.NIL;
        case Char:
        case Number:
        case Pattern:
        case String:
            return true;
        default:
            break;
        }
        return false;
    }

    /**
     * Get token from executing result
     *
     * @param operand
     * @return
     */
    private Token<?> getTokenFromOperand(AviatorObject operand) {
        Token<?> token = null;
        switch (operand.getAviatorType()) {
        case Boolean:
            token = operand.booleanValue(null) ? Variable.TRUE : Variable.FALSE;
            break;
        case Nil:
            token = Variable.NIL;
            break;
        case Double:
        case Long:
            final Number value = (Number) operand.getValue(null);
            token = new NumberToken(value, value.toString());
            break;
        case String:
            final String str = (String) operand.getValue(null);
            token = new StringToken(str, -1);
            break;
        case Pattern:
            token = new PatternToken(((AviatorPattern) operand).getPattern().pattern(), -1);
            break;
        default:
            break;
        }
        return token;
    }

    private void compactTokenList() {
        Iterator<Token<?>> it = tokenList.iterator();
        while (it.hasNext())
            if (it.next() == null) it.remove();
    }

    private AviatorObject getAviatorObjectFromToken(Token<?> lookhead) {
        AviatorObject result = null;
        switch (lookhead.getType()) {
        case Number:
            // load numbers
            NumberToken numberToken = (NumberToken) lookhead;
            if (numberToken.getNumber() instanceof Double) result = AviatorDouble.valueOf(numberToken.getNumber());
            else result = AviatorLong.valueOf(numberToken.getNumber());
            break;
        case String:
            // load string
            result = new AviatorString((String) lookhead.getValue(null));
            break;
        case Pattern:
            // load pattern
            result = new AviatorPattern((String) lookhead.getValue(null));
            break;
        case Variable:
            if (lookhead == Variable.TRUE) result = AviatorBoolean.TRUE;
            else if (lookhead == Variable.FALSE) result = AviatorBoolean.FALSE;
            else if (lookhead == Variable.NIL) result = AviatorNil.NIL;
            break;
        case Char:
            result = new AviatorString(String.valueOf(lookhead.getValue(null)));
            break;
        default:
            break;
        }
        return result;
    }

    @Override
    public Expression getResult() {
        // execute literal expression
        while (execute() > 0);

        Map<String, Integer/* counter */> variables = new LinkedHashMap<String, Integer>();
        Map<String, Integer/* counter */> methods = new HashMap<String, Integer>();
        for (Token<?> token : tokenList)
            switch (token.getType()) {
            case Variable:
                String varName = token.getLexeme();
                if (!variables.containsKey(varName)) variables.put(varName, 1);
                else variables.put(varName, variables.get(varName) + 1);

                break;
            case Delegate:
                DelegateToken delegateToken = (DelegateToken) token;
                if (delegateToken.getDelegateTokenType() == DelegateTokenType.Method_Name) {
                    Token<?> realToken = delegateToken.getToken();
                    if (realToken.getType() == TokenType.Variable) {
                        String methodName = token.getLexeme();
                        if (!methods.containsKey(methodName)) methods.put(methodName, 1);
                        else methods.put(methodName, methods.get(methodName) + 1);
                    }
                }
                else if (delegateToken.getDelegateTokenType() == DelegateTokenType.Array) {
                    Token<?> realToken = delegateToken.getToken();
                    if (realToken.getType() == TokenType.Variable) {
                        varName = token.getLexeme();
                        if (!variables.containsKey(varName)) variables.put(varName, 1);
                        else variables.put(varName, variables.get(varName) + 1);
                    }
                }
                break;
            default:
                break;
            }
        // call asm to generate byte codes
        callASM(variables, methods);

        // Last token is a literal token,then return a LiteralExpression
        if (tokenList.size() <= 1) {
            if (tokenList.isEmpty()) return new LiteralExpression(null, new ArrayList<String>(variables.keySet()));
            final Token<?> lastToken = tokenList.get(0);
            if (isLiteralToken(lastToken))
                return new LiteralExpression(getAviatorObjectFromToken(lastToken).getValue(null),
                        new ArrayList<String>(variables.keySet()));
        }

        // get result from asm
        return asmCodeGenerator.getResult();
    }

    private void callASM(Map<String, Integer/* counter */> variables, Map<String, Integer/* counter */> methods) {
        asmCodeGenerator.initVariables(variables);
        asmCodeGenerator.initMethods(methods);
        asmCodeGenerator.start();

        for (int i = 0; i < tokenList.size(); i++) {
            Token<?> token = tokenList.get(i);
            switch (token.getType()) {
            case Operator:
                OperatorToken op = (OperatorToken) token;

                switch (op.getOperatorType()) {
                case ADD:
                    asmCodeGenerator.onAdd(token);
                    break;
                case SUB:
                    asmCodeGenerator.onSub(token);
                    break;
                case MULT:
                    asmCodeGenerator.onMult(token);
                    break;
                case DIV:
                    asmCodeGenerator.onDiv(token);
                    break;
                case MOD:
                    asmCodeGenerator.onMod(token);
                    break;
                case EQ:
                    asmCodeGenerator.onEq(token);
                    break;
                case NEQ:
                    asmCodeGenerator.onNeq(token);
                    break;
                case LT:
                    asmCodeGenerator.onLt(token);
                    break;
                case LE:
                    asmCodeGenerator.onLe(token);
                    break;
                case GT:
                    asmCodeGenerator.onGt(token);
                    break;
                case GE:
                    asmCodeGenerator.onGe(token);
                    break;
                case NOT:
                    asmCodeGenerator.onNot(token);
                    break;
                case NEG:
                    asmCodeGenerator.onNeg(token);
                    break;
                case AND:
                    asmCodeGenerator.onAndRight(token);
                    break;
                case OR:
                    asmCodeGenerator.onJoinRight(token);
                    break;
                case FUNC:
                    asmCodeGenerator.onMethodInvoke(token);
                    break;
                case INDEX:
                    asmCodeGenerator.onArrayIndexEnd(token);
                    break;
                case MATCH:
                    asmCodeGenerator.onMatch(token);
                    break;
                case TERNARY:
                    asmCodeGenerator.onTernaryRight(token);
                    break;
                case BIT_AND:
                    asmCodeGenerator.onBitAnd(token);
                    break;
                case BIT_OR:
                    asmCodeGenerator.onBitOr(token);
                    break;
                case BIT_XOR:
                    asmCodeGenerator.onBitXor(token);
                    break;
                case BIT_NOT:
                    asmCodeGenerator.onBitNot(token);
                    break;
                case SHIFT_LEFT:
                    asmCodeGenerator.onShiftLeft(token);
                    break;
                case SHIFT_RIGHT:
                    asmCodeGenerator.onShiftRight(token);
                    break;
                case U_SHIFT_RIGHT:
                    asmCodeGenerator.onUnsignedShiftRight(token);
                    break;
                }
                break;
            case Delegate:
                DelegateToken delegateToken = (DelegateToken) token;
                final Token<?> realToken = delegateToken.getToken();
                switch (delegateToken.getDelegateTokenType()) {
                case And_Left:
                    asmCodeGenerator.onAndLeft(realToken);
                    break;
                case Join_Left:
                    asmCodeGenerator.onJoinLeft(realToken);
                    break;
                case Array:
                    asmCodeGenerator.onArray(realToken);
                    break;
                case Index_Start:
                    asmCodeGenerator.onArrayIndexStart(realToken);
                    break;
                case Ternary_Boolean:
                    asmCodeGenerator.onTernaryBoolean(realToken);
                    break;
                case Ternary_Left:
                    asmCodeGenerator.onTernaryLeft(realToken);
                    break;
                case Method_Name:
                    asmCodeGenerator.onMethodName(realToken);
                    break;
                case Method_Param:
                    asmCodeGenerator.onMethodParameter(realToken);
                    break;
                }
                break;

            default:
                asmCodeGenerator.onConstant(token);
                break;
            }

        }
    }

    private void printTokenList() {
        if (trace) {
            for (Token<?> t : tokenList)
                System.out.print(t.getLexeme() + " ");
            System.out.println();
        }
    }

    @Override
    public void onAdd(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.ADD));

    }

    @Override
    public void onAndLeft(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.And_Left));
    }

    @Override
    public void onAndRight(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.AND));

    }

    @Override
    public void onConstant(Token<?> lookhead) {
        tokenList.add(lookhead);
    }

    @Override
    public void onDiv(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.DIV));

    }

    @Override
    public void onArrayIndexStart(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Index_Start));

    }

    @Override
    public void onArrayIndexEnd(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.INDEX));
    }

    @Override
    public void onArray(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Array));

    }

    @Override
    public void onEq(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.EQ));

    }

    @Override
    public void onGe(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.GE));

    }

    @Override
    public void onGt(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.GT));

    }

    @Override
    public void onJoinLeft(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Join_Left));
    }

    @Override
    public void onJoinRight(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.OR));

    }

    @Override
    public void onLe(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.LE));

    }

    @Override
    public void onLt(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.LT));

    }

    @Override
    public void onMatch(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.MATCH));

    }

    @Override
    public void onMethodInvoke(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.FUNC));

    }

    @Override
    public void onMethodName(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Method_Name));

    }

    @Override
    public void onMethodParameter(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Method_Param));

    }

    @Override
    public void onMod(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.MOD));

    }

    @Override
    public void onMult(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.MULT));

    }

    @Override
    public void onNeg(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.NEG));

    }

    @Override
    public void onNeq(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.NEQ));

    }

    @Override
    public void onNot(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.NOT));

    }

    @Override
    public void onSub(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.SUB));

    }

    @Override
    public void onTernaryBoolean(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Ternary_Boolean));

    }

    @Override
    public void onTernaryLeft(Token<?> lookhead) {
        tokenList.add(new DelegateToken(lookhead == null ? -1 : lookhead.getStartIndex(), lookhead,
                DelegateTokenType.Ternary_Left));

    }

    @Override
    public void onTernaryRight(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.TERNARY));
    }

    @Override
    public void onBitAnd(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.BIT_AND));

    }

    @Override
    public void onBitNot(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.BIT_NOT));
    }

    @Override
    public void onBitOr(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.BIT_OR));

    }

    @Override
    public void onShiftLeft(Token<?> lookhead) {
        tokenList
                .add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.SHIFT_LEFT));

    }

    @Override
    public void onShiftRight(Token<?> lookhead) {
        tokenList
                .add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.SHIFT_RIGHT));

    }

    @Override
    public void onUnsignedShiftRight(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(),
                OperatorType.U_SHIFT_RIGHT));

    }

    @Override
    public void onBitXor(Token<?> lookhead) {
        tokenList.add(new OperatorToken(lookhead == null ? -1 : lookhead.getStartIndex(), OperatorType.BIT_XOR));

    }

}
