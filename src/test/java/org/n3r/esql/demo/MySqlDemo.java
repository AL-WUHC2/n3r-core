package org.n3r.esql.demo;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.n3r.eson.Eson;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlPage;

public class MySqlDemo {
    @Test
    public void pagingSqlShouldWork() {
        List<Map<String, Object>> result = new Esql("mysql").id("pagingDemo").limit(new EsqlPage(3,10)).execute();
        String json = new Eson().toString(result);
        System.out.println(json);


        String id = new Esql("mysql").selectFirst("autoIncr").execute().toString();
        System.out.println(id);
    }
}
