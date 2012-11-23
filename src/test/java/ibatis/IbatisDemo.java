package ibatis;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class IbatisDemo {
    public static void main(String[] args) throws IOException, SQLException {
        String resource = "ibatis/sqlmap-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlMapClient sqlmap = SqlMapClientBuilder.buildSqlMapClient(reader);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("a", "1");
        Object obj = sqlmap.queryForObject("testSelectAs", param);
        System.out.println(obj);

    }
}
