package org.n3r.esql;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RArray;
import org.n3r.core.lang.RClose;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.ex.EsqlIdNotFoundException;
import org.n3r.esql.impl.EsqlBatch;
import org.n3r.esql.impl.EsqlExecInfo;
import org.n3r.esql.map.AfterProperitesSet;
import org.n3r.esql.map.EsqlBeanRowMapper;
import org.n3r.esql.map.EsqlCallableResultBeanMapper;
import org.n3r.esql.map.EsqlCallableReturnMapMapper;
import org.n3r.esql.map.EsqlCallableReturnMapper;
import org.n3r.esql.map.EsqlMapMapper;
import org.n3r.esql.map.EsqlRowMapper;
import org.n3r.esql.param.EsqlParamPlaceholder.InOut;
import org.n3r.esql.param.EsqlParamsBinder;
import org.n3r.esql.param.EsqlParamsParser;
import org.n3r.esql.parser.EsqlDynamicReplacer;
import org.n3r.esql.parser.EsqlSqlParser;
import org.n3r.esql.res.EsqlItem;
import org.n3r.esql.res.EsqlSub;
import org.n3r.esql.res.EsqlSub.EsqlType;
import org.n3r.esql.util.EsqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import static org.n3r.esql.util.EsqlUtils.*;

import static org.apache.commons.lang3.StringUtils.*;

public class Esql {
    public static final String DEFAULT_CONNECTION_NAME = "DEFAULT";
    private Class<?> returnType;
    private Logger logger = LoggerFactory.getLogger(Esql.class);
    private String connectionName = DEFAULT_CONNECTION_NAME;
    private EsqlItem esqlItem;
    private Object[] params;
    private String sqlClassPath;
    private EsqlPage page;
    private EsqlBatch batch;
    private EsqlTransaction transaction;
    private Object[] dynamics;
    private int maxRows = Integer.MAX_VALUE;
    private boolean externalTrans;
    private Connection connection;

    public static ThreadLocal<EsqlExecInfo> execContext = new ThreadLocal<EsqlExecInfo>() {
        @Override
        protected EsqlExecInfo initialValue() {
            return new EsqlExecInfo();
        }
    };

    public static EsqlExecInfo getExecContextInfo() {
        return execContext.get();
    }

    public static void setExecContextInfo(EsqlExecInfo execContextInfo) {
        execContext.set(execContextInfo);

    }

    public Connection getConnection() {
        return getConfigTran(connectionName).getConnection();
    }

    @SuppressWarnings("unchecked")
    public <T> T execute(String... directSqls) {
        checkPreconditions(directSqls);

        Object ret = null;
        try {
            if (batch == null) tranStart();

            for (EsqlSub subSql : createSqlSubs(directSqls))
                ret = execSub(ret, subSql);

            if (batch == null) tranCommit();
        } catch (SQLException e) {
            if (batch != null) batch.cleanupBatch();
            throw new EsqlExecuteException("exec sql failed[" + Esql.getExecContextInfo().getSql() + "]"
                    + e.getMessage());
        } finally {
            if (batch == null) tranClose();
        }

        return (T) ret;
    }

    private void checkPreconditions(String... directSqls) {
        if (esqlItem != null || directSqls.length > 0) return;

        throw new EsqlExecuteException("No sqlid defined!");
    }

    private Object execSub(Object ret, EsqlSub subSql) throws SQLException {
        Esql.getExecContextInfo().setSql(subSql.getSql());

        try {
            return isDdl(subSql) ? execDdl(realSql(subSql)) : pageExecute(ret, subSql);
        } catch (EsqlExecuteException ex) {
            if (!subSql.getEsqlItem().isOnerrResume()) throw ex;
        }

        return 0;
    }

    private boolean isDdl(EsqlSub subSql) {
        switch (subSql.getSqlType()) {
        case CREATE:
        case DROP:
        case TRUNCATE:
        case ALTER:
        case COMMENT:
            return true;
        default:
            break;
        }
        return false;
    }

    private boolean execDdl(String sql) {
        Statement stmt = null;
        logger.info("sql:{}", sql);
        try {
            stmt = connection.createStatement();
            return stmt.execute(sql);
        } catch (SQLException ex) {
            throw new EsqlExecuteException(ex);
        } finally {
            RClose.closeQuietly(stmt);
        }
    }

    private Object pageExecute(Object ret, EsqlSub subSql) throws SQLException {
        if (page == null || !subSql.isLastSelectSql())
            return execDml(ret, subSql);

        if (page.getTotalRows() == 0) page.setTotalRows(executeTotalRowsSql(ret, subSql));

        return executePageSql(ret, subSql);
    }

    private Object executePageSql(Object ret, EsqlSub subSql) throws SQLException {
        // oracle专用物理分页。
        EsqlSub pageSql = createOraclePageSql(subSql);

        return execDml(ret, pageSql);
    }

    private int executeTotalRowsSql(Object ret, EsqlSub subSql) throws SQLException {
        EsqlSub totalSqlSub = subSql.clone();
        createTotalSql(totalSqlSub);

        return (Integer) execDml(ret, totalSqlSub);
    }

    private EsqlSub createOraclePageSql(EsqlSub subSql) {
        EsqlSub pageSubSql = subSql.clone();
        String pageSql = "SELECT * FROM ( SELECT ROW__.*, ROWNUM RN__ FROM ( " + subSql.getSql()
                + " ) ROW__  WHERE ROWNUM <= ?) WHERE RN__ > ?";
        pageSubSql.setSql(pageSql);
        pageSubSql.setExtraBindParams(page.getStartIndex() + page.getPageRows(), page.getStartIndex());

        return pageSubSql;
    }

    private void createTotalSql(EsqlSub subSql) {
        String sql = subSql.getSql().toUpperCase();
        int fromPos1 = sql.indexOf("FROM");

        int fromPos2 = sql.indexOf("DISTINCT");
        fromPos2 = fromPos2 < 0 ? sql.indexOf("FROM", fromPos1 + 4) : fromPos2;

        subSql.setSql(fromPos2 > 0 ? "SELECT COUNT(*) CNT__ FROM (" + sql + ")"
                : "SELECT COUNT(*) AS CNT " + sql.substring(fromPos1));
        subSql.setWillReturnOnlyOneRow(true);
    }

    private Object execDml(Object ret, EsqlSub subSql) throws SQLException {
        Object execRet = batch != null ? execDmlInBatch(ret, subSql) : execDmlNoBatch(ret, subSql);
        logger.info("result: {}", execRet);

        return execRet;
    }

    private Object execDmlInBatch(Object ret, EsqlSub subSql) throws SQLException {
        return batch.processBatchUpdate(subSql);
    }

    private Object execDmlNoBatch(Object ret, EsqlSub subSql) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = prepareSql(subSql, realSql(subSql));
            new EsqlParamsBinder().bindParams(ps, subSql, params, logger);

            if (subSql.getSqlType() == EsqlType.SELECT) {
                rs = ps.executeQuery();
                return convert(rs, subSql);
            }

            if (EsqlUtils.isProcedure(subSql.getSqlType())) {
                CallableStatement cs = (CallableStatement) ps;
                return execAndRetrieveProcedureRet(subSql, cs);
            }

            return ps.executeUpdate();

        } finally {
            RClose.closeQuietly(rs, ps);
        }
    }

    private Object execAndRetrieveProcedureRet(EsqlSub subSql, CallableStatement cs) throws SQLException {
        cs.execute();

        if (subSql.getOutCount() == 0) return null;

        if (subSql.getOutCount() == 1)
            for (int i = 0, ii = subSql.getPlaceHolders().length; i < ii; ++i)
                if (subSql.getPlaceHolders()[i].getInOut() != InOut.IN) return cs.getObject(i + 1);

        switch (subSql.getPlaceHolderOutType()) {
        case AUTO_SEQ:
            return retrieveAutoSeqOuts(subSql, cs);
        case VAR_NAME:
            return getCallableReturnMapper().mapResult(subSql, cs);
        default:
            break;
        }

        return null;
    }

    private Object retrieveAutoSeqOuts(EsqlSub subSql, CallableStatement cs) throws SQLException {
        List<Object> objects = Lists.newArrayList();
        for (int i = 0, ii = subSql.getPlaceHolders().length; i < ii; ++i)
            if (subSql.getPlaceHolders()[i].getInOut() != InOut.IN) objects.add(cs.getObject(i + 1));

        return objects;
    }

    public PreparedStatement prepareSql(EsqlSub subSql, String realSql) throws SQLException {
        logger.info("sql: {} ", realSql);
        return EsqlUtils.isProcedure(subSql.getSqlType())
                ? connection.prepareCall(realSql) : connection.prepareStatement(realSql);
    }

    public String realSql(EsqlSub subSql) {
        return new EsqlDynamicReplacer().repaceDynamics(subSql, dynamics);
    }

    private Object convert(ResultSet rs, EsqlSub subSql) throws SQLException {
        return maxRows <= 1 || subSql.isWillReturnOnlyOneRow() ? firstRow(rs) : selectList(rs);
    }

    private Object selectList(ResultSet rs) throws SQLException {
        List<Object> result = new ArrayList<Object>();

        for (int rownum = 1; rs.next() && rownum <= maxRows; ++rownum) {
            Object rowObject = rowBeanCreate(rs, rownum);
            if (rowObject != null) result.add(rowObject);
        }

        return result;
    }

    private Object rowBeanCreate(ResultSet rs, int rowNum) throws SQLException {
        Object rowBean = getRowMapper().mapRow(rs, rowNum);
        if (rowBean instanceof AfterProperitesSet)
            ((AfterProperitesSet) rowBean).afterPropertiesSet();

        return rowBean;
    }

    private EsqlRowMapper getRowMapper() {
        if (returnType == null && esqlItem != null) returnType = esqlItem.getReturnType();

        if (returnType != null && EsqlRowMapper.class.isAssignableFrom(returnType))
            return Reflect.on(returnType).create().get();

        if (returnType != null) return new EsqlBeanRowMapper(returnType);

        return new EsqlMapMapper();
    }

    private EsqlCallableReturnMapper getCallableReturnMapper() {
        if (returnType == null && esqlItem != null) returnType = esqlItem.getReturnType();

        if (returnType != null && EsqlCallableReturnMapper.class.isAssignableFrom(returnType))
            return Reflect.on(returnType).create().get();

        if (returnType != null) return new EsqlCallableResultBeanMapper(returnType);

        return new EsqlCallableReturnMapMapper();
    }

    public Esql returnType(Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }

    private Object firstRow(ResultSet rs) throws SQLException {
        if (!rs.next()) return null;

        if (rs.getMetaData().getColumnCount() == 1) {
            Object object = rs.getObject(1);
            if (object instanceof BigDecimal)
                return ((BigDecimal) object).intValue();

            return object;
        }

        return rowBeanCreate(rs, 1);
    }

    public Esql(String connectionName) {
        this.connectionName = connectionName;
    }

    public Esql() {}

    protected void initSqlId(String sqlId, String sqlClassPath) {
        this.sqlClassPath = isEmpty(sqlClassPath) ? getSqlClassPath(4) : sqlClassPath;

        esqlItem = EsqlSqlParser.getEsqlItem(this.sqlClassPath, sqlId);
        if (esqlItem == null)
            esqlItem = EsqlSqlParser.getEsqlItemFromSqlTable(connectionName, sqlId);

        if (esqlItem == null) throwError(sqlId);
    }

    private void throwError(String sqlId) {
        StringBuilder errMsg = new StringBuilder(sqlId + " not found ");
        boolean useSqlFile = isNotEmpty(sqlClassPath);
        if (useSqlFile) errMsg.append("in sql file ").append(sqlClassPath);

        throw new EsqlIdNotFoundException(errMsg.toString());
    }

    protected List<EsqlSub> createSqlSubs(String[] directSqls) {
        return directSqls.length == 0 ? esqlItem.createSqlSubs(RArray.first(params))
                : createSqlSubsByDirectSqls(directSqls);
    }

    private List<EsqlSub> createSqlSubsByDirectSqls(String[] sqls) {
        List<EsqlSub> sqlSubs = Lists.newArrayList();
        EsqlSub lastSelectSql = null;
        for (String sql : sqls) {
            EsqlSub subSql = new EsqlParamsParser().parseRawSql(sql, null);
            sqlSubs.add(subSql);

            if (subSql.getSqlType() == EsqlType.SELECT) lastSelectSql = subSql;
        }

        if (lastSelectSql != null) lastSelectSql.setLastSelectSql(true);

        return sqlSubs;
    }

    public Esql useSqlFile(Class<?> sqlBoundClass) {
        sqlClassPath = "/" + sqlBoundClass.getName().replace('.', '/') + ".esql";
        return this;
    }

    public Esql useSqlFile(String sqlClassPath) {
        this.sqlClassPath = sqlClassPath;
        return this;
    }

    public Esql id(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public Esql merge(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public Esql update(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public Esql insert(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public Esql delete(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public Esql select(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public Esql selectFirst(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        limit(1);
        return this;
    }

    public Esql procedure(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        return this;
    }

    public String getSqlId() {
        return esqlItem.getSqlId();
    }

    private void tranStart() {
        if (externalTrans) return;

        transaction = getConfigTran(connectionName);
        connection = transaction.getConnection();
        transaction.start();
    }

    private void tranCommit() {
        if (externalTrans) return;

        transaction.commit();
    }

    private void tranClose() {
        RClose.closeQuietly(transaction);
        transaction = null;
    }

    public EsqlTransaction newTransaction() {
        externalTrans = true;
        EsqlTransaction configTran = getConfigTran(connectionName);
        connection = configTran.getConnection();

        return configTran;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getSqlPath() {
        return sqlClassPath;
    }

    public Esql params(Object... params) {
        this.params = params;
        return this;
    }

    public Esql limit(EsqlPage page) {
        this.page = page;

        return this;
    }

    public Esql startBatch(int maxBatches) {
        batch.startBatch(maxBatches);
        return this;
    }

    public Esql startBatch() {
        batch = new EsqlBatch(this);
        batch.startBatch();

        if (!externalTrans) tranStart();
        return this;
    }

    public int executeBatch() {
        int totalRows = 0;
        try {
            totalRows = batch.processBatchExecution();
            tranCommit();
        } catch (SQLException e) {
            throw new EsqlExecuteException("executeBatch failed:" + e.getMessage());
        } finally {
            tranClose();
        }

        batch = null;
        return totalRows;
    }

    public Esql dynamics(Object... dynamics) {
        this.dynamics = dynamics;

        return this;
    }

    public Esql limit(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public Object[] getParams() {
        return params;
    }

    public Logger getLogger() {
        return logger;
    }

}
