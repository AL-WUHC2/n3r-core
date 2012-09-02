package org.n3r.esql.impl;

import java.util.List;

import org.n3r.esql.EsqlTransaction;

import com.google.common.collect.Lists;

public class EsqlExecInfo {
    private String sql;
    private EsqlTransaction transaction;
    private List<Object> returns = Lists.newArrayList();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public EsqlTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(EsqlTransaction transaction) {
        this.transaction = transaction;
    }

    public List<Object> getReturns() {
        return returns;
    }

    public void addReturn(Object ret) {
        this.returns.add(ret);
    }

    public void setReturns(List<Object> returns) {
        this.returns = returns;
    }
}
