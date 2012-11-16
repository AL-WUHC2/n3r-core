package org.n3r.esql.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.core.collection.RMap;
import org.n3r.core.lang.RClose;
import org.n3r.core.lang.RDate;
import org.n3r.core.text.RRand;
import org.n3r.eson.Eson;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlPage;
import org.n3r.esql.parser.EsqlTableSqlMapper;

public class EsqlDemoTest {
    @BeforeClass
    public static void beforeClass() {
        new Esql().useSqlFile(EsqlDemo.class).update("beforeClass").execute();
    }

    @AfterClass
    public static void afterClass() {
        new Esql().useSqlFile(EsqlDemo.class).update("afterClass").execute();
    }

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
        assertEquals("[{A:1,B:A,C:#AC,D:1345478400000,E:101}," +
                "{A:2,B:B,C:#BC,D:1345478400000,E:102}," +
                "{A:3,B:C,C:CC,D:1345478400000,E:103}," +
                "{A:4,B:D,C:DC,D:1345478400000,E:104}]"
                , new Eson().on(beanList).toString());
    }

    @Test
    public void getFirstMap() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Map<String, Object> bean = esqlDemo.getFirstMap();
        assertEquals("{A:1,B:A,C:#AC,D:1345478400000,E:101}", new Eson().on(bean).toString());
    }

    @Test
    public void getFirstBean() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean firstBean = esqlDemo.getFirstBean();
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(firstBean).toString());
    }

    @Test
    public void getBeans() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        List<EsqlDemoBean> beans = esqlDemo.getBeans();
        assertEquals("[{a:1,b:A,c:#AC,d:1345478400000,e:101}," +
                "{a:2,b:B,c:#BC,d:1345478400000,e:102}," +
                "{a:3,b:C,c:CC,d:1345478400000,e:103}," +
                "{a:4,b:D,c:DC,d:1345478400000,e:104}]", new Eson().on(beans).toString());
    }

    @Test
    public void testSelectStringList() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        List<String> strs = new Esql().useSqlFile(EsqlDemo.class).select("getStringList").execute();
        assertEquals("[#AC,#BC,CC,DC]", new Eson().on(strs).toString());
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
        EsqlDemoBean esqlDemoBean = esqlDemo.selectBeanByBean(RMap.of("a", 1, "c", "#AC"));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());

        esqlDemoBean = esqlDemo.selectBeanByBean(esqlDemoBean);
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());

        Map<String, Object> map = esqlDemo.selectMapByBean(esqlDemoBean);
        assertEquals("{A:1,B:A,C:#AC,D:1345478400000,E:101}", new Eson().on(map).toString());
    }

    @Test
    public void selectByBean2() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectBeanByBean2(RMap.of("a", 1, "c", "#AC"));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());

        esqlDemoBean = esqlDemo.selectBeanByBean2(esqlDemoBean);
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());

        Map<String, Object> map = esqlDemo.selectMapByBean(esqlDemoBean);
        assertEquals("{A:1,B:A,C:#AC,D:1345478400000,E:101}", new Eson().on(map).toString());
    }

    @Test
    public void selectIf() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIf(RMap.of("a", 1, "c", "#AC", "e", 100));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());
        esqlDemoBean = esqlDemo.selectIf(RMap.of("a", 1, "c", "#AC", "e", 200));
        assertNull(esqlDemoBean);
    }

    @Test
    public void selectIfWoReturnType() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIfWoReturnType(RMap.of("a", 1, "c", "#AC", "e", 100));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());
        esqlDemoBean = esqlDemo.selectIfWoReturnType(RMap.of("a", 1, "c", "#AC", "e", 200));
        assertNull(esqlDemoBean);
    }

    @Test
    public void selectIfNotEmpty() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIfNotEmpty(RMap.of("a", 1));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());
        esqlDemoBean = esqlDemo.selectIfNotEmpty(RMap.of("a", ""));
        assertNotNull(esqlDemoBean);
    }

    @Test
    public void switchSelect() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.switchSelect(RMap.of("a", 1));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());
        esqlDemoBean = esqlDemo.switchSelect(RMap.of("a", ""));
        assertNotNull(esqlDemoBean);
    }

    @Test
    public void switchSelectWithDefault() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.switchSelectWithDefault(RMap.of("a", 1));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());
        esqlDemoBean = esqlDemo.switchSelectWithDefault(RMap.of("a", ""));
        assertNotNull(esqlDemoBean);
    }

    @Test
    public void selectIf2() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        EsqlDemoBean esqlDemoBean = esqlDemo.selectIf2(RMap.of("a", 1, "c", "#AC", "e", 100));
        assertEquals("{a:1,b:A,c:#AC,d:1345478400000,e:101}", new Eson().on(esqlDemoBean).toString());
        esqlDemoBean = esqlDemo.selectIf2(RMap.of("a", 2, "c", "#AC", "e", 200));
        assertNull(esqlDemoBean);
    }

    @Test
    public void useSqlFile1() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Esql esql = new Esql().useSqlFile(EsqlDemo.class);
        esql.update("updateBean")
                .params(1, "abc").execute();
        EsqlDemoBean demoBean = esql.select("selectBean").limit(1).params(1).returnType(EsqlDemoBean.class).execute();
        assertEquals("abc", demoBean.getB());
    }

    @Test
    public void useSqlFile2() {
        esqlDemo.executeImmediate(RDate.parse("2012-08-21"));
        Esql esql = new Esql().useSqlFile("/org/n3r/esql/demo/EsqlDemo.esql");
        esql.update("updateBean")
                .params(1, "abc").execute();
        EsqlDemoBean demoBean = esql.select("selectBean").limit(1).params(1).returnType(EsqlDemoBean.class).execute();
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

        EsqlDemoBean demoBean = esql.select("selectBean").limit(1).params(1).returnType(EsqlDemoBean.class).execute();
        assertEquals("abc", demoBean.getB());
    }

    @Test
    public void pageCall() {
        EsqlPage page = new EsqlPage(3, 10);
        /* Object execute =*/new Esql().useSqlFile(EsqlDemo.class).select("selectRecords").limit(page).execute();
        //        System.out.println(execute);
        //        System.out.println(page);
    }

    @Test
    public void selectLimitedRecords() {
        List<Object> execute = new Esql().useSqlFile(EsqlDemo.class).select("selectRecords").limit(3).execute();

        assertTrue(execute.size() <= 3);
    }

    @Test
    public void batch() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = new Esql().getConnection();
            ps = connection.prepareStatement("INSERT INTO ESQL_TEST_BINGOO(ORDER_NO, " +
                    "ACTIVITY_ID, ITEM_ID, USER_ID, BINGOO_TIME) VALUES(?, ?, ?, ?, SYSDATE)");
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
            /*int[] executeBatch = */ps.executeBatch();
            //            System.out.println(ArrayUtils.toString(executeBatch));
        } finally {
            RClose.closeQuietly(ps, connection);
        }

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

        new Esql().useSqlFile(EsqlDemo.class).id("deletePrizeBingoo").execute();
    }

    @Test
    public void insertPrizeBingoo2() {
        Esql esql = new Esql().useSqlFile(EsqlDemo.class);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ORDER_NO", RRand.randLetters(10));
        map.put("ACTIVITY_ID", "Olympic");
        map.put("ITEM_ID", RRand.randInt(10));
        map.put("USER_ID", RRand.randLetters(10));
        int ret = esql.id("insertPrizeBingoo2").params(map).execute();
        assertEquals(1, ret);

        map.put("ITEM_ID", RRand.randInt(10));
        ret = esql.id("updatePrizeBingoo2").params(map).execute();
        assertEquals(1, ret);

        map.put("ITEM_ID", RRand.randInt(10));
        ret = esql.id("mergePrizeBingoo2").params(map).execute();
        assertEquals(1, ret);

        esql.id("deletePrizeBingoo").execute();

        map.put("ITEM_ID", RRand.randInt(10));
        ret = esql.id("mergePrizeBingoo2").params(map).execute();
        assertEquals(1, ret);
        esql.id("deletePrizeBingoo").execute();
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

    @Test
    public void procedure1() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsql").execute();
        Connection connection = null;
        CallableStatement cs = null;
        try {
            connection = new Esql().getConnection();
            cs = connection.prepareCall("{call SP_ESQL(?, ?)}");
            cs.setString(1, "hjb");
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            //            System.out.println(cs.getString(2));
        } finally {
            RClose.closeQuietly(cs, connection);
        }

        String b = new Esql().useSqlFile(EsqlDemo.class)
                .procedure("callSpEsql").params("hjb")
                .execute();
        assertEquals("HELLO hjb", b);

    }

    @Test
    public void procedure2() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsql2").execute();
        List<String> bc = new Esql().useSqlFile(EsqlDemo.class)
                .procedure("callSpEsql2").params("hjb")
                .execute();
        assertEquals("HELLO hjb", bc.get(0));
        assertEquals("WORLD hjb", bc.get(1));
    }

    @Test
    public void procedure3() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsql2").execute();
        Map<String, Object> bc = new Esql().useSqlFile(EsqlDemo.class)
                .procedure("callSpEsql3").params("hjb")
                .execute();
        assertEquals("HELLO hjb", bc.get("a"));
        assertEquals("WORLD hjb", bc.get("b"));
    }

    public static class Ab {
        private String a;
        private String b;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

    }

    @Test
    public void procedure4() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsql2").execute();
        Ab ab = new Esql().useSqlFile(EsqlDemo.class)
                .procedure("callSpEsql4").params("hjb")
                .execute();
        assertEquals("HELLO hjb", ab.getA());
        assertEquals("WORLD hjb", ab.getB());
    }

    @Test
    public void procedureNoOut() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpNoOut").execute();
        Esql esql = new Esql().useSqlFile(EsqlDemo.class)
                .params("hjb")
                .limit(1);
        int ab = esql.returnType("int")
                .execute("{CALL SP_ESQL_NOOUT(##)}", "SELECT 1 FROM DUAL");

        assertEquals(1, ab);
    }

    @Test
    public void procedureAllOut() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsql12").execute();
        List<String> rets = new Esql().useSqlFile(EsqlDemo.class).procedure("callSpEsql12").execute();

        assertEquals("HELLO", rets.get(0));
        assertEquals("WORLD", rets.get(1));
    }

    @Test
    public void procedureInOut() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsqlInOut").execute();
        List<String> rets = new Esql().useSqlFile(EsqlDemo.class).params("A", "B").procedure("callSpEsqlInOut")
                .execute();

        assertEquals("HELLOA", rets.get(0));
        assertEquals("WORLDB", rets.get(1));
    }

    @Test
    public void createSpEsqlNULL() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("createSpEsqlNULL").execute();
        List<String> rets = new Esql().useSqlFile(EsqlDemo.class)
                .procedure("callSpEsqlNULL")
                .dynamics("SP_ESQLNULL")
                .execute();

        assertNull(rets.get(0));
        assertNull(rets.get(1));
    }

    @Test
    public void returning() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("prepareTable4MyProcedure").execute();
        String ret = new Esql().useSqlFile(EsqlDemo.class).procedure("myprocedure")
                .execute();

        assertTrue(ret.length() > 0);
    }

    @Test
    public void returning2() throws SQLException {
        new Esql().useSqlFile(EsqlDemo.class).update("prepareTable4MyProcedure").execute();
        List<String> ret = new Esql().useSqlFile(EsqlDemo.class).params(10).procedure("myprocedure2")
                .execute();

        assertTrue(ret.size() > 0);
    }

    @Test
    public void callPLSQL() throws SQLException {
        /*  String ret = */new Esql().useSqlFile(EsqlDemo.class).params(10).procedure("callPLSQL")
                .execute();
        /*  System.out.println(ret);*/
    }

    @Test
    public void twoTrans() {

    }

    public static class AsResult {
        private String state;
        private String remark;
        private int seq;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

    }

    @Test
    public void testSelectAs() {
        new Esql().useSqlFile(EsqlDemo.class).update("prepareTable4MyProcedure").execute();
        AsResult ret = new Esql().useSqlFile(EsqlDemo.class)
                .id("testSelectAs").limit(1).params("1").returnType(AsResult.class)
                .execute();
        assertEquals("EsqlDemoTest.AsResult[state=0,remark=AAAA,seq=1]",
                ReflectionToStringBuilder.toString(ret, ToStringStyle.SHORT_PREFIX_STYLE));
    }
}
