package com.ibatis.sqlmap.engine.datasource;

import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@SuppressWarnings("rawtypes")
public class C3P0Configuration {

    private DataSource dataSource;

    /**
     * Constructor to supply a map of properties
     *
     * @param properties - the map of configuration properties
     */

    public C3P0Configuration(Map properties) {
        try {
            dataSource = legacyC3P0Configuration(properties);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing C3P0DataSourceFactory.  Cause: " + e, e);
        }
    }

    /**
     * Getter for DataSource
     *
     * @return The DataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    private ComboPooledDataSource legacyC3P0Configuration(Map map) throws Exception {
        ComboPooledDataSource basicDataSource = null;
        if (map.containsKey("JDBC.Driver")) {
            basicDataSource = new ComboPooledDataSource();
            String driver = (String) map.get("JDBC.Driver");
            String url = (String) map.get("JDBC.ConnectionURL");
            String username = (String) map.get("JDBC.Username");
            String password = (String) map.get("JDBC.Password");
            //                String validationQuery = (String) map.get("Pool.ValidationQuery");
            //                String maxActive = (String) map.get("Pool.MaximumActiveConnections");
            //                String maxIdle = (String) map.get("Pool.MaximumIdleConnections");
            //                String maxWait = (String) map.get("Pool.MaximumWait");

            basicDataSource.setJdbcUrl(url);
            basicDataSource.setDriverClass(driver);
            basicDataSource.setUser(username);
            basicDataSource.setPassword(password);

            //                if (notEmpty(validationQuery)) {
            //                  basicDataSource.setValidationQuery(validationQuery);
            //                }
            //
            //                if (notEmpty(maxActive)) {
            //                  basicDataSource.setMaxActive(Integer.parseInt(maxActive));
            //                }
            //
            //                if (notEmpty(maxIdle)) {
            //                  basicDataSource.setMaxIdle(Integer.parseInt(maxIdle));
            //                }
            //
            //                if (notEmpty(maxWait)) {
            //                  basicDataSource.setMaxWait(Integer.parseInt(maxWait));
            //                }

            //                Iterator props = map.keySet().iterator();
            //                while (props.hasNext()) {
            //                  String propertyName = (String) props.next();
            //                  if (propertyName.startsWith(ADD_DRIVER_PROPS_PREFIX)) {
            //                    String value = (String) map.get(propertyName);
            //                    basicDataSource.addConnectionProperty(propertyName.substring(ADD_DRIVER_PROPS_PREFIX_LENGTH), value);
            //                  }
            //                }
        }
        return basicDataSource;
    }

    //      private boolean notEmpty(String s) {
    //        return s != null && s.length() > 0;
    //      }

}
