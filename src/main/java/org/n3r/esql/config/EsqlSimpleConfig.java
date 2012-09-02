package org.n3r.esql.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.n3r.core.lang.RClass;
import org.n3r.esql.EsqlTransaction;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.trans.EsqlJdbcTransaction;
import org.n3r.esql.trans.EsqlJtaTransaction;

public class EsqlSimpleConfig extends EsqlConfig {
    private String url;
    private String user;
    private String pass;
    private String driver;
    private String transactionType;
    private boolean driverLoaded;

    private Connection getConnection() {
        try {
            if (!driverLoaded) RClass.findClass(driver);
            return DriverManager.getConnection(url, user, pass);
        }
        catch (SQLException e) {
            throw new EsqlConfigException("create connection fail", e);
        }
    }

    public EsqlTransaction getTransaction() {
        if ("jta".equals(transactionType)) return new EsqlJtaTransaction();

        return new EsqlJdbcTransaction(getConnection());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

}
