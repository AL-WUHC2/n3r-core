package org.n3r.esql.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.n3r.core.lang.RClose;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTransaction;

public class EsqlDemo {

    public String getString() {
        return new Esql().selectFirst("getString").execute();
    }

    public int getInt() {
        return new Esql().selectFirst("getInt").execute();
    }

    public String getStringWithOneParam(String str) {
        return new Esql().selectFirst("getStringWithOneParam")
                .params(str)
                .execute();
    }

    public String getStringWithTwoParams(String s1, String s2) {
        return new Esql().selectFirst("getStringWithTwoParams")
                .params(s1, s2)
                .execute();
    }

    public String getStringWithTwoParamsAndSequence(String s1, String s2) {
        return new Esql().selectFirst("getStringWithTwoParamsAndSequence")
                .params(s2, s1)
                .execute();
    }

    public void executeImmediate(Date date) {
        new Esql().update("initialize").params(date).execute();
    }

    public List<Map<String, Object>> getMaps() {
        return new Esql().select("getBeanList").execute();
    }

    public Map<String, Object> getFirstMap() {
        return new Esql().selectFirst("getBeanList").execute();
    }

    public EsqlDemoBean getFirstBean() {
        return new Esql().selectFirst("getBeanList")
                .returnType(EsqlDemoBean.class)
                .execute();
    }

    public List<EsqlDemoBean> getBeans() {
        return new Esql().select("getBeanList")
                .returnType(EsqlDemoBean.class)
                .execute();

    }

    public int updateBean(int a, String b) {
        return new Esql().update("updateBean")
                .params(a, b)
                .execute();
    }

    public EsqlDemoBean selectBean(int a) {
        return new Esql().selectFirst("selectBean").returnType(EsqlDemoBean.class).params(a).execute();
    }

    public EsqlDemoBean selectBeanByBean(Object params) {
        return new Esql().selectFirst("selectByBean")
                .params(params)
                .returnType(EsqlDemoBean.class)
                .execute();
    }

    public Map<String, Object> selectMapByBean(Object params) {
        return new Esql().selectFirst("selectByBean")
                .params(params)
                .execute();
    }

    public EsqlDemoBean selectIf(Object params) {
        return new Esql().selectFirst("selectIf")
                .returnType(EsqlDemoBean.class)
                .params(params)
                .execute();
    }

    public EsqlDemoBean selectIfWoReturnType(Object params) {
        return new Esql().selectFirst("selectIf")
                .params(params)
                .execute();
    }

    public EsqlDemoBean selectIfNotEmpty(Object params) {
        return new Esql().selectFirst("selectIfNotEmpty")
                .params(params)
                .execute();
    }

    public EsqlDemoBean switchSelect(Object params) {
        return new Esql().selectFirst("switchSelect")
                .params(params)
                .execute();
    }

    public EsqlDemoBean switchSelectWithDefault(Object params) {
        return new Esql().selectFirst("switchSelectWithDefault")
                .params(params)
                .execute();
    }

    public EsqlDemoBean selectIf2(Object params) {
        return new Esql().selectFirst("selectIf2")
                .returnType(EsqlDemoBean.class)
                .params(params)
                .execute();
    }

    public void rollback(int a, String b) {
        Esql esql = new Esql();
        EsqlTransaction tran = esql.newTransaction();
        try {
            tran.start();
            esql.update("updateBean")
                    .params(a, b)
                    .execute();

            tran.rollback();
        } finally {
            RClose.closeQuietly(tran);
        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "orcl", "orcl");
        connection.setAutoCommit(false);
        PreparedStatement ps = connection.prepareStatement("update ESQL_TEST set B = 'BBB'");
        boolean execute = ps.execute();
        System.out.println(execute);
        ps.close();
        connection.close();
    }
}
