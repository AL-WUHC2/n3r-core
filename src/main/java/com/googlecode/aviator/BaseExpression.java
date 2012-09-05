package com.googlecode.aviator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Base expression
 *
 * @author dennis
 *
 */
public abstract class BaseExpression implements Expression {

    private List<String> varNames;

    public BaseExpression(List<String> varNames) {
        super();
        LinkedHashSet<String> tmp = new LinkedHashSet<String>(varNames.size());
        // process nested names
        for (String name : varNames) {
            if (name.contains(".")) name = name.substring(0, name.indexOf("."));
            tmp.add(name);
        }
        this.varNames = new ArrayList<String>(tmp);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.googlecode.aviator.IExpression#execute()
     */
    @Override
    public Object execute() {
        return this.execute(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.googlecode.aviator.IExpression#getVariableNames()
     */
    @Override
    public List<String> getVariableNames() {
        return varNames;
    }

}
