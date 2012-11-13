package org.n3r.esql;

import org.apache.commons.lang3.StringUtils;
import org.n3r.esql.res.EsqlSub;

public class DbType {
    private String driverName;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public EsqlSub createPageSql(EsqlSub subSql, EsqlPage page ) {
        if (StringUtils.containsIgnoreCase(driverName, "oracle"))
            return createOraclePageSql(subSql, page);

        if (StringUtils.containsIgnoreCase(driverName, "mysql"))
            return createMySqlPageSql(subSql, page);

        return subSql;
    }

    private EsqlSub createMySqlPageSql(EsqlSub subSql, EsqlPage page) {
        EsqlSub pageSubSql = subSql.clone();
        String pageSql = subSql.getSql() + " LIMIT ?,?";
        pageSubSql.setSql(pageSql);
        pageSubSql.setExtraBindParams(page.getStartIndex(), page.getPageRows());

        return pageSubSql;
    }

    private EsqlSub createOraclePageSql(EsqlSub subSql, EsqlPage page) {
        EsqlSub pageSubSql = subSql.clone();
        String pageSql = "SELECT * FROM ( SELECT ROW__.*, ROWNUM RN__ FROM ( " + subSql.getSql()
                + " ) ROW__  WHERE ROWNUM <= ?) WHERE RN__ > ?";
        pageSubSql.setSql(pageSql);
        pageSubSql.setExtraBindParams(page.getStartIndex() + page.getPageRows(), page.getStartIndex());

        return pageSubSql;
    }
}
