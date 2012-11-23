package org.n3r.esql.num;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.Test;
import org.n3r.esql.Esql;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class NumPerformanceTest {
    private static final int TIMES = 1000;

    @Test
    public void ibatisSimple() throws IOException, SQLException {
        String resource = "ibatis/sqlmap-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlMapClient sqlmap = SqlMapClientBuilder.buildSqlMapClient(reader);

        for (int i = 0, ii = TIMES; i < ii; ++i) {
            /*List list =*/sqlmap.queryForList("simpleNumPerformance");
            //            String json = new Eson().toString(list);
            //          System.out.println(json);
        }
    }

    @Test
    public void esqlSimple() {
        for (int i = 0, ii = TIMES; i < ii; ++i) {
            /*Object ret =*/new Esql("num").id("simpleNumPerformance").execute();
            //        String json = new Eson().toString(ret);
            //        System.out.println(json);
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void ibatisBind() throws IOException, SQLException {
        String resource = "ibatis/sqlmap-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlMapClient sqlmap = SqlMapClientBuilder.buildSqlMapClient(reader);

        HashMap map = new HashMap();
        map.put("provinceCode", "11");
        map.put("netTypeCode", "02");
        map.put("eparchyCode", "110");
        map.put("codeReverse", "311%");

        for (int i = 0, ii = TIMES; i < ii; ++i) {
//            List list =
                    sqlmap.queryForList("simpleNumPerformance", map);
//            String json = new Eson().toString(list);
//            System.out.println(json);
        }
    }

    @Test
    public void esqlBind() {
        for (int i = 0, ii = TIMES; i < ii; ++i) {
//            Object ret =
                    new Esql("num").id("bindNumPerformance")
                            .params("11", "02", "110", "311%").execute();
//            String json = new Eson().toString(ret);
//            System.out.println(json);
        }
    }

}
