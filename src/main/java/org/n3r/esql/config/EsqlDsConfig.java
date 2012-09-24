package org.n3r.esql.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.n3r.esql.EsqlTran;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.trans.EsqlJdbcTransaction;
import org.n3r.esql.trans.EsqlJtaTransaction;

import static org.apache.commons.lang3.StringUtils.*;

public class EsqlDsConfig extends EsqlConfig {
    private String transactionType;
    private String jndiName;
    private String initial;
    private String url;
    private DataSource dataSource;
    private String userTransaction;

    private void createDataSource() {
        try {
            Hashtable<String, String> context = new Hashtable<String, String>();
            if (!isEmpty(url)) context.put("java.naming.provider.url", url);
            if (!isEmpty(initial)) context.put("java.naming.factory.initial", initial);

            dataSource = (DataSource) new InitialContext(context).lookup(jndiName);
        } catch (NamingException e) {
            throw new EsqlConfigException("create data source fail", e);
        }
    }

    public Connection getConnection() {
        try {
            if (dataSource == null) createDataSource();

            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new EsqlConfigException("create connection fail", e);
        }
    }

    @Override
    public EsqlTran getTran() {
        if ("jta".equals(transactionType)) return new EsqlJtaTransaction();

        return new EsqlJdbcTransaction(getConnection());
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserTransaction() {
        return userTransaction;
    }

    public void setUserTransaction(String userTransaction) {
        this.userTransaction = userTransaction;
    }
}
