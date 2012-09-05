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
package com.googlecode.aviator.code.asm;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.asm.ClassVisitor;
import com.googlecode.aviator.asm.ClassWriter;
import com.googlecode.aviator.asm.Label;
import com.googlecode.aviator.asm.MethodVisitor;
import com.googlecode.aviator.asm.util.CheckClassAdapter;
import com.googlecode.aviator.asm.util.TraceClassVisitor;
import com.googlecode.aviator.code.CodeGenerator;
import com.googlecode.aviator.exception.CompileExpressionErrorException;
import com.googlecode.aviator.lexer.token.NumberToken;
import com.googlecode.aviator.lexer.token.Token;
import com.googlecode.aviator.lexer.token.Variable;
import com.googlecode.aviator.parser.AviatorClassLoader;

import static com.googlecode.aviator.asm.Opcodes.*;

/**
 * Code generator using asm
 *
 * @author dennis
 *
 */
public class ASMCodeGenerator implements CodeGenerator {
    private static final String FIELD_PREFIX = "var_";
    // Class Writer to generate class
    private final ClassWriter classWriter;
    // Trace visitor
    private ClassVisitor traceClassVisitor;
    // Check visitor
    private ClassVisitor checkClassAdapter;
    // Method visitor
    private MethodVisitor mv;
    // Class name
    private final String className;
    // Class loader to define generated class
    private final AviatorClassLoader classLoader;

    private static final AtomicLong CLASS_COUNTER = new AtomicLong();

    /**
     * Operands count to check stack frames
     */
    private int operandsCount = 0;

    private int maxStacks = 0;
    private int maxLocals = 2;

    private int fieldCounter = 0;

    private Map<String/* variable name */, String/* inner var name */> innerVarMap = new HashMap<String, String>();

    private Map<String/* method name */, String/* inner method name */> innerMethodMap = new HashMap<String, String>();

    private Map<String, Integer/* counter */> varTokens = new LinkedHashMap<String, Integer>();
    private Map<String, Integer/* counter */> methodTokens = new HashMap<String, Integer>();

    private Map<Label, Map<String/* inner name */, Integer/* local index */>> labelNameIndexMap =
            new HashMap<Label, Map<String, Integer>>();

    private static final Label START_LABEL = new Label();

    private Label currentLabel = START_LABEL;

    private void setMaxStacks(int newMaxStacks) {
        if (newMaxStacks > maxStacks) maxStacks = newMaxStacks;
    }

    public ASMCodeGenerator(AviatorClassLoader classLoader, OutputStream traceOut, boolean trace) {
        this.classLoader = classLoader;
        // Generate inner class name
        className = "Script_" + System.currentTimeMillis() + "_" + CLASS_COUNTER.getAndIncrement();
        // Auto compute frames
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        if (trace) {
            traceClassVisitor = new TraceClassVisitor(classWriter, new PrintWriter(traceOut));
            checkClassAdapter = new CheckClassAdapter(traceClassVisitor);
        } else checkClassAdapter = new CheckClassAdapter(classWriter);
        visitClass();
    }

    public void start() {
        makeConstructor();
        startVisitMethodCode();
    }

    private void startVisitMethodCode() {
        mv =
                checkClassAdapter.visitMethod(ACC_PUBLIC + +ACC_FINAL, "execute0",
                        "(Ljava/util/Map;)Ljava/lang/Object;",
                        "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)Ljava/lang/Object;", null);
        mv.visitCode();
    }

    private void endVisitMethodCode() {
        if (operandsCount > 0) {
            loadEnv();
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "getValue",
                    "(Ljava/util/Map;)Ljava/lang/Object;");
            mv.visitInsn(ARETURN);
            popOperand();
            popOperand();
        }
        else {
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            pushOperand(0);
            popOperand();
        }
        if (operandsCount > 0)
            throw new CompileExpressionErrorException("operand stack is not empty,count=" + operandsCount);
        mv.visitMaxs(maxStacks, maxLocals);
        mv.visitEnd();

    }

    private void endVisitClass() {
        checkClassAdapter.visitEnd();
    }

    /**
     * Make a default constructor
     */
    private void makeConstructor() {
        {
            mv = checkClassAdapter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/List;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "com/googlecode/aviator/ClassExpression", "<init>", "(Ljava/util/List;)V");
            if (!innerVarMap.isEmpty()) for (Map.Entry<String, String> entry : innerVarMap.entrySet()) {
                String outterName = entry.getKey();
                String innerName = entry.getValue();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "com/googlecode/aviator/runtime/type/AviatorJavaType");
                mv.visitInsn(DUP);
                mv.visitLdcInsn(outterName);
                mv.visitMethodInsn(INVOKESPECIAL, "com/googlecode/aviator/runtime/type/AviatorJavaType",
                        "<init>", "(Ljava/lang/String;)V");
                mv.visitFieldInsn(PUTFIELD, className, innerName,
                        "Lcom/googlecode/aviator/runtime/type/AviatorJavaType;");
            }
            if (!innerMethodMap.isEmpty()) for (Map.Entry<String, String> entry : innerMethodMap.entrySet()) {
                String outterName = entry.getKey();
                String innerName = entry.getValue();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn(outterName);
                mv.visitMethodInsn(INVOKESTATIC, "com/googlecode/aviator/AviatorEvaluator", "getFunction",
                        "(Ljava/lang/String;)Lcom/googlecode/aviator/runtime/type/AviatorFunction;");
                mv.visitFieldInsn(PUTFIELD, className, innerName,
                        "Lcom/googlecode/aviator/runtime/type/AviatorFunction;");
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 1);
            mv.visitEnd();
        }
    }

    private void visitClass() {
        checkClassAdapter.visit(AviatorEvaluator.BYTECODE_VER, ACC_PUBLIC + ACC_SUPER, className, null,
                "com/googlecode/aviator/ClassExpression", null);
    }

    /**
     * Make a label
     *
     * @return
     */
    private Label makeLabel() {
        return new Label();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onAdd(com.googlecode.aviator
     * .lexer.token.Token)
     */
    @Override
    public void onAdd(Token<?> lookhead) {
        doArthOperation("add");
    }

    /**
     * Do arithmetic operation
     *
     * @param methodName
     */
    private void doArthOperation(String methodName) {
        loadEnv();
        mv
                .visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/googlecode/aviator/runtime/type/AviatorObject",
                        methodName,
                        "(Lcom/googlecode/aviator/runtime/type/AviatorObject;Ljava/util/Map;)Lcom/googlecode/aviator/runtime/type/AviatorObject;");
        popOperand();
        popOperand();
    }

    /**
     * Pop a operand from stack
     */
    private void popOperand() {
        operandsCount--;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onSub(com.googlecode.aviator
     * .lexer.token.Token)
     */
    @Override
    public void onSub(Token<?> lookhead) {
        doArthOperation("sub");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onMult(com.googlecode.aviator
     * .lexer.token.Token)
     */
    @Override
    public void onMult(Token<?> lookhead) {
        doArthOperation("mult");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onDiv(com.googlecode.aviator
     * .lexer.token.Token)
     */
    @Override
    public void onDiv(Token<?> lookhead) {
        doArthOperation("div");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onMod(com.googlecode.aviator
     * .lexer.token.Token)
     */
    @Override
    public void onMod(Token<?> lookhead) {
        doArthOperation("mod");
    }

    /**
     * Do logic operation "&&" left operand
     */
    @Override
    public void onAndLeft(Token<?> lookhead) {
        loadEnv();
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "booleanValue",
                "(Ljava/util/Map;)Z");
        Label l0 = makeLabel();
        pushLabel0(l0);
        mv.visitJumpInsn(IFEQ, l0);

        popOperand(); // boolean object
        popOperand(); // environment

    }

    private void pushLabel0(Label l0) {
        l0stack.push(l0);
    }

    /**
     * Do logic operation "&&" right operand
     */
    @Override
    public void onAndRight(Token<?> lookhead) {
        loadEnv();
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "booleanValue",
                "(Ljava/util/Map;)Z");
        mv.visitJumpInsn(IFEQ, peekLabel0());
        // Result is true
        mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "TRUE",
                "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
        Label l1 = makeLabel();
        mv.visitJumpInsn(GOTO, l1);
        visitLabel(popLabel0());
        // Result is false
        mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "FALSE",
                "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
        visitLabel(l1);

        popOperand(); // boolean object
        popOperand(); // environment
        pushOperand(0);
    }

    /**
     * Label stack for ternary operator
     */
    private final Stack<Label> l0stack = new Stack<Label>();
    private final Stack<Label> l1stack = new Stack<Label>();

    @Override
    public void onTernaryBoolean(Token<?> lookhead) {
        loadEnv();
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "booleanValue",
                "(Ljava/util/Map;)Z");
        Label l0 = makeLabel();
        Label l1 = makeLabel();
        pushLabel0(l0);
        pushLabel1(l1);
        mv.visitJumpInsn(IFEQ, l0);
        popOperand();
        popOperand();
        pushOperand(1); // add two booleans

        popOperand(); // pop the last result
    }

    private void pushLabel1(Label l1) {
        l1stack.push(l1);
    }

    @Override
    public void onTernaryLeft(Token<?> lookhead) {
        mv.visitJumpInsn(GOTO, peekLabel1());
        visitLabel(popLabel0());
        popOperand(); // pop one boolean
    }

    private Label peekLabel1() {
        return l1stack.peek();
    }

    @Override
    public void onTernaryRight(Token<?> lookhead) {
        visitLabel(popLabel1());
        popOperand(); // pop one boolean
    }

    private Label popLabel1() {
        return l1stack.pop();
    }

    /**
     * Do logic operation "||" right operand
     */
    @Override
    public void onJoinRight(Token<?> lookhead) {
        loadEnv();
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "booleanValue",
                "(Ljava/util/Map;)Z");
        Label l1 = makeLabel();
        mv.visitJumpInsn(IFNE, peekLabel0());
        // Result is False
        mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "FALSE",
                "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
        mv.visitJumpInsn(GOTO, l1);
        visitLabel(popLabel0());
        // Result is True
        mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "TRUE",
                "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
        visitLabel(l1);
        popOperand();
        popOperand();
        pushOperand(0);

    }

    private void visitLabel(Label label) {
        mv.visitLabel(label);
        currentLabel = label;
    }

    private Label peekLabel0() {
        return l0stack.peek();
    }

    private Label popLabel0() {
        return l0stack.pop();
    }

    /**
     * Do logic operation "||" left operand
     */
    @Override
    public void onJoinLeft(Token<?> lookhead) {
        loadEnv();
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "booleanValue",
                "(Ljava/util/Map;)Z");
        Label l0 = makeLabel();
        pushLabel0(l0);
        mv.visitJumpInsn(IFNE, l0);

        popOperand();
        popOperand();

    }

    @Override
    public void onEq(Token<?> lookhead) {
        doCompareAndJump(IFNE);
    }

    @Override
    public void onMatch(Token<?> lookhead) {
        mv.visitInsn(SWAP);
        loadEnv();
        mv
                .visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/googlecode/aviator/runtime/type/AviatorObject",
                        "match",
                        "(Lcom/googlecode/aviator/runtime/type/AviatorObject;Ljava/util/Map;)Lcom/googlecode/aviator/runtime/type/AviatorObject;");

        popOperand();
        popOperand();
        popOperand();
        pushOperand(0);
    }

    @Override
    public void onNeq(Token<?> lookhead) {
        doCompareAndJump(IFEQ);
    }

    private void doCompareAndJump(int ints) {
        loadEnv();
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "compare",
                "(Lcom/googlecode/aviator/runtime/type/AviatorObject;Ljava/util/Map;)I");
        Label l0 = makeLabel();
        Label l1 = makeLabel();
        mv.visitJumpInsn(ints, l0);
        mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "TRUE",
                "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
        mv.visitJumpInsn(GOTO, l1);
        visitLabel(l0);
        mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "FALSE",
                "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
        visitLabel(l1);
        popOperand();
        popOperand();
        popOperand();
        pushOperand(0);
    }

    @Override
    public void onGe(Token<?> lookhead) {
        doCompareAndJump(IFLT);
    }

    @Override
    public void onGt(Token<?> lookhead) {
        doCompareAndJump(IFLE);
    }

    @Override
    public void onLe(Token<?> lookhead) {
        doCompareAndJump(IFGT);

    }

    @Override
    public void onLt(Token<?> lookhead) {
        doCompareAndJump(IFGE);
    }

    /**
     *
     * @param extras
     *            额外的栈空间大小
     */
    public void pushOperand(int extras) {
        operandsCount++;
        operandsCount += extras;
        setMaxStacks(operandsCount);
    }

    /**
     * Logic operation '!'
     */
    @Override
    public void onNot(Token<?> lookhead) {
        pushOperand(0);

        mv.visitTypeInsn(CHECKCAST, "com/googlecode/aviator/runtime/type/AviatorObject");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "not",
                "(Ljava/util/Map;)Lcom/googlecode/aviator/runtime/type/AviatorObject;");

        popOperand();
        popOperand();
        pushOperand(0);
    }

    /**
     * Bit operation '~'
     */
    @Override
    public void onBitNot(Token<?> lookhead) {
        pushOperand(0);
        mv.visitTypeInsn(CHECKCAST, "com/googlecode/aviator/runtime/type/AviatorObject");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "bitNot",
                "(Ljava/util/Map;)Lcom/googlecode/aviator/runtime/type/AviatorObject;");
        popOperand();
        popOperand();
        pushOperand(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onNeg(com.googlecode.aviator
     * .lexer.token.Token, int)
     */
    @Override
    public void onNeg(Token<?> lookhead) {
        pushOperand(0);

        mv.visitTypeInsn(CHECKCAST, "com/googlecode/aviator/runtime/type/AviatorObject");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/googlecode/aviator/runtime/type/AviatorObject", "neg",
                "(Ljava/util/Map;)Lcom/googlecode/aviator/runtime/type/AviatorObject;");
        popOperand();
        popOperand();
        pushOperand(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.googlecode.aviator.code.CodeGenerator#getResult()
     */
    @Override
    public Expression getResult() {
        end();

        byte[] bytes = classWriter.toByteArray();
        try {
            Class<?> defineClass = classLoader.defineClass(className, bytes);
            Constructor<?> constructor = defineClass.getConstructor(List.class);
            return (Expression) constructor.newInstance(new ArrayList<String>(varTokens.keySet()));
        } catch (Exception e) {
            throw new CompileExpressionErrorException("define class error", e);
        }
    }

    private void end() {
        endVisitMethodCode();
        endVisitClass();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.aviator.code.CodeGenerator#onConstant(com.googlecode.aviator
     * .lexer.token.Token)
     */
    @Override
    public void onConstant(Token<?> lookhead) {
        if (lookhead == null) return;
        // load token to stack
        switch (lookhead.getType()) {
        case Number:
            // load numbers
            NumberToken numberToken = (NumberToken) lookhead;
            if (numberToken.getNumber() instanceof Double) {
                mv.visitLdcInsn(numberToken.getNumber());
                mv.visitMethodInsn(INVOKESTATIC, "com/googlecode/aviator/runtime/type/AviatorDouble", "valueOf",
                        "(D)Lcom/googlecode/aviator/runtime/type/AviatorDouble;");
            }
            else {
                mv.visitLdcInsn(numberToken.getNumber());
                mv.visitMethodInsn(INVOKESTATIC, "com/googlecode/aviator/runtime/type/AviatorLong", "valueOf",
                        "(J)Lcom/googlecode/aviator/runtime/type/AviatorLong;");
            }
            pushOperand(0);
            // this.popOperand();
            // this.popOperand();
            break;
        case String:
            // load string
            mv.visitTypeInsn(NEW, "com/googlecode/aviator/runtime/type/AviatorString");
            mv.visitInsn(DUP);
            mv.visitLdcInsn(lookhead.getValue(null));
            mv.visitMethodInsn(INVOKESPECIAL, "com/googlecode/aviator/runtime/type/AviatorString", "<init>",
                    "(Ljava/lang/String;)V");
            pushOperand(2);
            popOperand();
            popOperand();
            break;
        case Pattern:
            // load pattern
            mv.visitTypeInsn(NEW, "com/googlecode/aviator/runtime/type/AviatorPattern");
            mv.visitInsn(DUP);
            mv.visitLdcInsn(lookhead.getValue(null));
            mv.visitMethodInsn(INVOKESPECIAL, "com/googlecode/aviator/runtime/type/AviatorPattern", "<init>",
                    "(Ljava/lang/String;)V");
            pushOperand(2);
            popOperand();
            popOperand();
            break;
        case Variable:
            // load variable
            Variable variable = (Variable) lookhead;

            if (variable.equals(Variable.TRUE)) {
                mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "TRUE",
                        "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
                pushOperand(0);
            }
            else if (variable.equals(Variable.FALSE)) {
                mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorBoolean", "FALSE",
                        "Lcom/googlecode/aviator/runtime/type/AviatorBoolean;");
                pushOperand(0);
            }
            else if (variable.equals(Variable.NIL)) {
                mv.visitFieldInsn(GETSTATIC, "com/googlecode/aviator/runtime/type/AviatorNil", "NIL",
                        "Lcom/googlecode/aviator/runtime/type/AviatorNil;");
                pushOperand(0);
            }
            else {
                String outterVarName = variable.getLexeme();
                String innerVarName = innerVarMap.get(outterVarName);
                if (innerVarName != null) {
                    // Is it stored in local?
                    Map<String, Integer> name2Index = labelNameIndexMap.get(currentLabel);
                    if (name2Index != null && name2Index.get(innerVarName) != null) {
                        int localIndex = name2Index.get(innerVarName);
                        mv.visitVarInsn(ALOAD, localIndex);
                        pushOperand(0);
                    }
                    else {
                        // Get field at first time
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, className, innerVarName,
                                "Lcom/googlecode/aviator/runtime/type/AviatorJavaType;");
                        // Variable is used more than once,store it to local
                        if (varTokens.get(outterVarName) > 1) {
                            mv.visitInsn(DUP);
                            int localIndex = getLocalIndex();
                            mv.visitVarInsn(ASTORE, localIndex);
                            if (name2Index == null) {
                                name2Index = new HashMap<String, Integer>();
                                labelNameIndexMap.put(currentLabel, name2Index);
                            }
                            name2Index.put(innerVarName, localIndex);
                            pushOperand(2);
                            popOperand();
                            popOperand();
                        }
                        else {
                            pushOperand(1);
                            popOperand();
                        }
                    }

                }
                else {
                    mv.visitTypeInsn(NEW, "com/googlecode/aviator/runtime/type/AviatorJavaType");
                    mv.visitInsn(DUP);
                    mv.visitLdcInsn(outterVarName);
                    mv.visitMethodInsn(INVOKESPECIAL, "com/googlecode/aviator/runtime/type/AviatorJavaType",
                            "<init>", "(Ljava/lang/String;)V");
                    pushOperand(2);
                    popOperand();
                    popOperand();
                }

            }
            break;
        default:
            break;
        }

    }

    public void initVariables(Map<String, Integer/* counter */> varTokens) {
        this.varTokens = varTokens;
        for (String outterVarName : varTokens.keySet()) {
            // Use inner variable name instead of outter variable name
            String innerVarName = getInnerName(outterVarName);
            innerVarMap.put(outterVarName, innerVarName);
            checkClassAdapter.visitField(ACC_PRIVATE + ACC_FINAL, innerVarName,
                    "Lcom/googlecode/aviator/runtime/type/AviatorJavaType;", null, null).visitEnd();

        }
    }

    public void initMethods(Map<String, Integer/* counter */> methods) {
        methodTokens = methods;
        for (String outterMethodName : methods.keySet()) {
            // Use inner method name instead of outter method name
            String innerMethodName = getInnerName(outterMethodName);
            innerMethodMap.put(outterMethodName, innerMethodName);
            checkClassAdapter.visitField(ACC_PRIVATE + ACC_FINAL, innerMethodName,
                    "Lcom/googlecode/aviator/runtime/type/AviatorFunction;", null, null).visitEnd();

        }
    }

    private String getInnerName(String varName) {
        return FIELD_PREFIX + fieldCounter++;
    }

    private String getInvokeMethodDesc(int paramCount) {
        StringBuilder sb = new StringBuilder("(Ljava/util/Map;");
        for (int i = 0; i < paramCount; i++)
            sb.append("Lcom/googlecode/aviator/runtime/type/AviatorObject;");
        sb.append(")Lcom/googlecode/aviator/runtime/type/AviatorObject;");
        return sb.toString();
    }

    @Override
    public void onMethodInvoke(Token<?> lookhead) {
        final MethodMetaData methodMetaData = methodMetaDataStack.pop();
        final int parameterCount = methodMetaData.parameterCount;
        mv.visitMethodInsn(INVOKEINTERFACE, "com/googlecode/aviator/runtime/type/AviatorFunction", "call",
                getInvokeMethodDesc(parameterCount));

        popOperand(); // method object
        popOperand(); // env map
        // pop operands
        for (int i = 0; i < parameterCount; i++)
            popOperand();
        // push result
        pushOperand(0);
    }

    @Override
    public void onMethodParameter(Token<?> lookhead) {
        methodMetaDataStack.peek().parameterCount++;
        // // add parameter to list
        // this.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add",
        // "(Ljava/lang/Object;)Z");
        // // pop boolean
        // this.mv.visitInsn(POP);
        // this.mv.visitVarInsn(ALOAD,
        // this.methodMetaDataStack.peek().parameterListIndex);
    }

    private static class MethodMetaData {
        //        final String methodName;

        int parameterCount;

        public MethodMetaData(String methodName) {
            super();
            //            this.methodName = methodName;
            parameterCount = 0;
        }

    }

    private final Stack<MethodMetaData> methodMetaDataStack = new Stack<MethodMetaData>();

    @Override
    public void onArray(Token<?> lookhead) {
        onConstant(lookhead);
    }

    @Override
    public void onArrayIndexStart(Token<?> token) {
        loadEnv();
    }

    @Override
    public void onArrayIndexEnd(Token<?> lookhead) {
        mv
                .visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/googlecode/aviator/runtime/type/AviatorObject",
                        "getElement",
                        "(Ljava/util/Map;Lcom/googlecode/aviator/runtime/type/AviatorObject;)Lcom/googlecode/aviator/runtime/type/AviatorObject;");

        popOperand();
        popOperand();
        popOperand();
        pushOperand(0);
    }

    public int getLocalIndex() {
        return maxLocals++;
    }

    @Override
    public void onMethodName(Token<?> lookhead) {
        String outtterMethodName = lookhead.getLexeme();
        String innerMethodName = innerMethodMap.get(outtterMethodName);
        if (innerMethodName != null) loadAviatorFunction(outtterMethodName, innerMethodName);
        else createAviatorFunctionObject(outtterMethodName);
        loadEnv();
        methodMetaDataStack.push(new MethodMetaData(outtterMethodName));
    }

    private void loadAviatorFunction(String outterMethodName, String innerMethodName) {
        Map<String, Integer> name2Index = labelNameIndexMap.get(currentLabel);
        // Is it stored in local?
        if (name2Index != null && name2Index.containsKey(innerMethodName)) {
            int localIndex = name2Index.get(innerMethodName);
            mv.visitVarInsn(ALOAD, localIndex);
            pushOperand(0);
        }
        else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, innerMethodName,
                    "Lcom/googlecode/aviator/runtime/type/AviatorFunction;");
            // Method is used more than once,store it to local for reusing
            if (methodTokens.get(outterMethodName) > 1) {
                mv.visitInsn(DUP);
                int localIndex = getLocalIndex();
                mv.visitVarInsn(ASTORE, localIndex);
                if (name2Index == null) {
                    name2Index = new HashMap<String, Integer>();
                    labelNameIndexMap.put(currentLabel, name2Index);
                }
                name2Index.put(innerMethodName, localIndex);
                pushOperand(1);
                popOperand();
            } else pushOperand(0);
        }
    }

    // private int createArugmentList() {
    // // create argument list
    // this.pushOperand(0);
    // this.pushOperand(0);
    // this.mv.visitTypeInsn(NEW, "java/util/ArrayList");
    // this.mv.visitInsn(DUP);
    // this.popOperand();
    // this.mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>",
    // "()V");
    // // store to local variable
    // final int parameterLocalIndex = this.getLocalIndex();
    // this.mv.visitVarInsn(ASTORE, parameterLocalIndex);
    // this.mv.visitVarInsn(ALOAD, parameterLocalIndex);
    // return parameterLocalIndex;
    // }

    private void loadEnv() {
        // load env
        pushOperand(0);
        mv.visitVarInsn(ALOAD, 1);
    }

    private void createAviatorFunctionObject(String methodName) {
        pushOperand(0);
        mv.visitLdcInsn(methodName);
        mv.visitMethodInsn(INVOKESTATIC, "com/googlecode/aviator/AviatorEvaluator", "getFunction",
                "(Ljava/lang/String;)Lcom/googlecode/aviator/runtime/type/AviatorFunction;");
        popOperand();
        pushOperand(0);
    }

    @Override
    public void onBitAnd(Token<?> lookhead) {
        doArthOperation("bitAnd");
    }

    @Override
    public void onBitOr(Token<?> lookhead) {
        doArthOperation("bitOr");
    }

    @Override
    public void onBitXor(Token<?> lookhead) {
        doArthOperation("bitXor");
    }

    @Override
    public void onShiftLeft(Token<?> lookhead) {
        doArthOperation("shiftLeft");

    }

    @Override
    public void onShiftRight(Token<?> lookhead) {
        doArthOperation("shiftRight");

    }

    @Override
    public void onUnsignedShiftRight(Token<?> lookhead) {
        doArthOperation("unsignedShiftRight");

    }

}
