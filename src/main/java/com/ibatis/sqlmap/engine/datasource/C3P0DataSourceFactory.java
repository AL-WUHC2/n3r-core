package com.ibatis.sqlmap.engine.datasource;

import java.util.Map;

import javax.sql.DataSource;

@SuppressWarnings("rawtypes")
public class C3P0DataSourceFactory implements DataSourceFactory {

    private DataSource dataSource;

    @Override
    public void initialize(Map map) {
        C3P0Configuration dbcp = new C3P0Configuration(map);
        dataSource = dbcp.getDataSource();
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

}
