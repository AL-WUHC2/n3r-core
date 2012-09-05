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
package com.googlecode.aviator.lexer.token;

import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;


/**
 * Operator type
 * 
 * @author dennis
 * 
 */
public enum OperatorType {
    BIT_OR("|", 2),

    BIT_AND("&", 2),

    BIT_XOR("^", 2),

    BIT_NOT("~", 1),

    SHIFT_LEFT("<<", 2),

    SHIFT_RIGHT(">>", 2),

    U_SHIFT_RIGHT(">>>", 2),

    NOT("!", 1),

    MULT("*", 2),

    DIV("/", 2),

    MOD("%", 2),

    ADD("+", 2),

    SUB("-sub", 2),

    LT("<", 2),

    LE("<=", 2),

    GT(">", 2),

    GE(">=", 2),

    EQ("==", 2),

    NEQ("!=", 2),

    AND("&&", 2),

    MATCH("=~", 2),

    OR("||", 2),

    INDEX("[]", 2),

    FUNC("()", Integer.MAX_VALUE),

    NEG("-neg", 1),

    TERNARY("?:", 3);

    private String token;

    private int operandCount;


    OperatorType(String token, int operandCount) {
        this.token = token;
        this.operandCount = operandCount;
    }


    public AviatorObject eval(AviatorObject[] args) {
        if (args.length < this.operandCount) {
            throw new ExpressionRuntimeException("There are not enough operands for " + this);
        }
        switch (this) {
        case ADD:
            return args[0].add(args[1], null);
        case SUB:
            return args[0].sub(args[1], null);
        case MOD:
            return args[0].mod(args[1], null);
        case DIV:
            return args[0].div(args[1], null);
        case MULT:
            return args[0].mult(args[1], null);
        case EQ:
            int result = args[0].compare(args[1], null);
            return AviatorBoolean.valueOf(result == 0);
        case NEQ:
            result = args[0].compare(args[1], null);
            return AviatorBoolean.valueOf(result != 0);
        case LT:
            result = args[0].compare(args[1], null);
            return AviatorBoolean.valueOf(result < 0);
        case LE:
            result = args[0].compare(args[1], null);
            return AviatorBoolean.valueOf(result <= 0);
        case GT:
            result = args[0].compare(args[1], null);
            return AviatorBoolean.valueOf(result > 0);
        case GE:
            result = args[0].compare(args[1], null);
            return AviatorBoolean.valueOf(result >= 0);
        case NOT:
            return args[0].not(null);
        case NEG:
            return args[0].neg(null);
        case MATCH:
            // swap arguments
            return args[1].match(args[0], null);
        case AND:
            return args[0].booleanValue(null) && args[1].booleanValue(null) ? AviatorBoolean.TRUE
                    : AviatorBoolean.FALSE;
        case OR:
            return args[0].booleanValue(null) || args[1].booleanValue(null) ? AviatorBoolean.TRUE
                    : AviatorBoolean.FALSE;
        case FUNC:
            // TODO
            break;
        case INDEX:
            return ((AviatorJavaType) args[0]).getElement(null, args[1]);
        case TERNARY:
            return args[0].booleanValue(null) ? args[1] : args[2];
        case BIT_OR:
            return args[0].bitOr(args[1], null);
        case BIT_AND:
            return args[0].bitAnd(args[1], null);
        case BIT_XOR:
            return args[0].bitXor(args[1], null);
        case SHIFT_LEFT:
            return args[0].shiftLeft(args[1], null);
        case SHIFT_RIGHT:
            return args[0].shiftRight(args[1], null);
        case U_SHIFT_RIGHT:
            return args[0].unsignedShiftRight(args[1], null);
        case BIT_NOT:
            return args[0].bitNot(null);

        }
        return null;
    }


    public String getToken() {
        return this.token;
    }


    public int getOperandCount() {
        return this.operandCount;
    }
}
