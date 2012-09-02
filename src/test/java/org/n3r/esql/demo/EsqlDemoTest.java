package org.n3r.esql.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.n3r.core.collection.RMap;
import org.n3r.core.lang.RDate;
import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlPage;
import org.n3r.esql.EsqlTransaction;
import org.n3r.esql.parser.EsqlTableSqlMapper;
import org.n3r.esql.util.EsqlUtils;

import com.alibaba.fastjson.JSON;

import static org.junit.Assert.*;

public class EsqlDemoTest {
    private EsqlDemo esqlDemo = new EsqlDemo();

    @Test
    public void getString() {
        assertEquals("x", esqlDemo.getString());
    }

    @Test
    public void getInt() {
        assertEquals(1, esqlDemo.getInt());
    }

    @Test
    public void getStringWithOneParam() {
        assertEquals("x", esqlDemo.getStringWithOneParam("x"));
        assertNull(esqlDemo.getStringWithOneParam("y"));
    }

    @Test
    public void getStringWithTwoParams() {
        assertEquals("x", esqlDemo.getStringWithTwoParams("x", "y"));
        assertNull(esqlDemo.getStringWithTwoParams("1", "y"));
    }

    @Test
    public void getStringWithTwoParamsAndSequence() {
        assertEquals("x", esqlDemo.getStringWithTwoParamsAndSequence("x", "y"));
        assertNull(esqlDemo.getStringWithTwoParams("1", "y"));
    }

    @Test
    public void getMapList() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        List<Map<String, Object>> beanList = esqlDemo.getMaps();
        assertEquals("[{A:1,B:A,C:AC,D:2012-08-21 00:00:00.0,E:101}," +
                "{A:2,B:B,C:BC,D:2012-08-21 00:00:00.0,E:102}," +
                "{A:3,B:C,C:CC,D:2012-08-21 00:00:00.0,E:103}," +
                "{A:4,B:D,C:DC,D:2012-08-21 00:00:00.0,E:104}]"
                , JSON.toJSONString(beanList).replaceAll("\"", ""));
    }

    @Test
    public void getFirstMap() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Map<String, Object> bean = esqlDemo.getFirstMap();
        assertEquals("{A:1,B:A,C:AC,D:2012-08-21 00:00:00.0,E:101}", JSON.toJSONString(bean).replaceAll("\"", ""));
    }

    @Test
    public void getFirstBean() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean firstBean = esqlDemo.getFirstBean();
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(firstBean).replaceAll("\"", ""));
    }

    @Test
    public void getBeans() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        List<EsqlDemoBean> beans = esqlDemo.getBeans();
        assertEquals("[{a:1,b:A,c:AC,d:1345478400000,e:101}," +
                "{a:2,b:B,c:BC,d:1345478400000,e:102}," +
                "{a:3,b:C,c:CC,d:1345478400000,e:103}," +
                "{a:4,b:D,c:DC,d:1345478400000,e:104}]", JSON.toJSONString(beans).replaceAll("\"", ""));
    }

    @Test
    public void updateBean() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        int effectedRows = esqlDemo.updateBean(1, "A1A1");
        assertEquals(1, effectedRows);

        effectedRows = esqlDemo.updateBean(100, "A1A1");
        assertEquals(0, effectedRows);

        EsqlDemoBean selectBean = esqlDemo.selectBean(1);
        assertEquals("A1A1", selectBean.getB());
    }

    @Test
    public void rollback() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        esqlDemo.rollback(1, "A1A1");

        EsqlDemoBean selectBean = esqlDemo.selectBean(1);
        assertEquals("A", selectBean.getB());
    }

    @Test
    public void selectByBean() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectBeanByBean(RMap.of("a", 1, "c", "AC"));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));

        esqlDemoBean = esqlDemo.selectBeanByBean(esqlDemoBean);
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));

        Map<String, Object> map = esqlDemo.selectMapByBean(esqlDemoBean);
        assertEquals("{A:1,B:A,C:AC,D:2012-08-21 00:00:00.0,E:101}", JSON.toJSONString(map).replaceAll("\"", ""));
    }

    @Test
    public void selectIf() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIf(RMap.of("a", 1, "c", "AC", "e", 100));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));
        esqlDemoBean = esqlDemo.selectIf(RMap.of("a", 1, "c", "AC", "e", 200));
        assertNull(esqlDemoBean);
    }

    @Test
    public void selectIfWoReturnType() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIfWoReturnType(RMap.of("a", 1, "c", "AC", "e", 100));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));
        esqlDemoBean = esqlDemo.selectIfWoReturnType(RMap.of("a", 1, "c", "AC", "e", 200));
        assertNull(esqlDemoBean);
    }

    @Test
    public void selectIfNotEmpty() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIfNotEmpty(RMap.of("a", 1));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));
        esqlDemoBean = esqlDemo.selectIfNotEmpty(RMap.of("a", ""));
        assertNotNull(esqlDemoBean);
    }

    @Test
    public void switchSelect() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.switchSelect(RMap.of("a", 1));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));
        esqlDemoBean = esqlDemo.switchSelect(RMap.of("a", ""));
        assertNotNull(esqlDemoBean);
    }

    @Test
    public void switchSelectWithDefault() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.switchSelectWithDefault(RMap.of("a", 1));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));
        esqlDemoBean = esqlDemo.switchSelectWithDefault(RMap.of("a", ""));
        assertNotNull(esqlDemoBean);
    }

    @Test
    public void selectIf2() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIf2(RMap.of("a", 1, "c", "AC", "e", 100));
        assertEquals("{a:1,b:A,c:AC,d:1345478400000,e:101}", JSON.toJSONString(esqlDemoBean).replaceAll("\"", ""));
        esqlDemoBean = esqlDemo.selectIf2(RMap.of("a", 2, "c", "AC", "e", 200));
        assertNull(esqlDemoBean);
    }

    @Test
    public void useSqlFile1() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Esql esql = new Esql().useSqlFile(EsqlDemo.class);
        esql.update("updateBean")
                .params(1, "abc").execute();
        EsqlDemoBean demoBean = esql.selectFirst("selectBean").params(1).returnType(EsqlDemoBean.class).execute();
        assertEquals("abc", demoBean.getB());
    }

    @Test
    public void useSqlFile2() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Esql esql = new Esql().useSqlFile("/org/n3r/esql/demo/EsqlDemo.esql");
        esql.update("updateBean")
                .params(1, "abc").execute();
        EsqlDemoBean demoBean = esql.selectFirst("selectBean").params(1).returnType(EsqlDemoBean.class).execute();
        assertEquals("abc", demoBean.getB());
    }

    @Test
    public void useSqlTable() {
        List<EsqlTableSqlMapper> ret = new Esql()
                .returnType(EsqlTableSqlMapper.class)
                .params(1)
                .execute("SELECT ID, OPTIONS, SQL FROM  ESQL_SQL WHERE VALID = ##");
        assertNotNull(ret);

        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Esql esql = new Esql().useSqlFile("/org/n3r/esql/demo/EsqlDemo.esql");
        esql.update("demo.updateBean")
                .params(1, "abc").execute();

        EsqlDemoBean demoBean = esql.selectFirst("selectBean").params(1).returnType(EsqlDemoBean.class).execute();
        assertEquals("abc", demoBean.getB());
    }

    @Test
    public void pageCall() {
        EsqlPage page = new EsqlPage(3, 10);
        Object execute = new Esql().useSqlFile(EsqlDemo.class).select("selectRecords").limit(page).execute();
        System.out.println(execute);
        System.out.println(page);
    }

    @Test
    public void batch() throws SQLException {
        EsqlTransaction configTran = EsqlUtils.getConfigTran(Esql.DEFAULT_CONNECTION_NAME);
        Connection connection = configTran.getConnection();
        PreparedStatement ps = connection
                .prepareStatement("INSERT INTO PRIZE_BINGOO(ORDER_NO, ACTIVITY_ID, ITEM_ID, USER_ID, BINGOO_TIME) VALUES(?, ?, ?, ?, SYSDATE)");
        for (int i = 0; i < 10; ++i) {
            String orderNo = RRand.randLetters(10);
            String userId = RRand.randLetters(10);
            int prizeItem = RRand.randInt(10);
            ps.setString(1, orderNo);
            ps.setString(2, "Olympic");
            ps.setString(3, "" + prizeItem);
            ps.setString(4, userId);
            ps.addBatch();
        }
        int[] executeBatch = ps.executeBatch();
        System.out.println(ArrayUtils.toString(executeBatch));
        ps.close();
        connection.close();

    }

    @Test
    public void batchEsql() {
        Esql esql = new Esql().useSqlFile(EsqlDemo.class);
        esql.startBatch();
        for (int i = 0; i < 10; ++i) {
            String orderNo = RRand.randLetters(10);
            String userId = RRand.randLetters(10);
            int prizeItem = RRand.randInt(10);
            int ret = esql.insert("insertPrizeBingoo").params(orderNo, "Olympic", "" + prizeItem, userId).execute();
            assertEquals(0, ret);
        }

        esql.executeBatch();
    }

    @Test
    public void insertPrizeBingooDynamic() {
        String orderNo = RRand.randLetters(10);
        String userId = RRand.randLetters(10);
        int prizeItem = RRand.randInt(10);

        int row = new Esql().useSqlFile(EsqlDemo.class).insert("insertPrizeBingooDynamic")
                .params(orderNo, "Olympic", "" + prizeItem, userId)
                .dynamics("BINGOO")
                .execute();
        assertEquals(1, row);
    }
}
