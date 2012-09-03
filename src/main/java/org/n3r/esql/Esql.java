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
import org.n3r.core.lang.RClassPath;
import org.n3r.core.lang.RClose;
import org.n3r.core.lang.RJavaScript;
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

    public static ThreadLocal<EsqlExecInfo> execContext = new ThreadLocal<EsqlExecInfo>() {
        @Override
        protected EsqlExecInfo initialValue() {
            RJavaScript.eval(RClassPath.toStr("/org/n3r/esql/initializeScript.js"));

            return new EsqlExecInfo();
        }
    };

    public static EsqlExecInfo getExecContextInfo() {
        return execContext.get();
    }

    public static void setExecContextInfo(EsqlExecInfo execContextInfo) {
        execContext.set(execContextInfo);

    }

    public static void newExecContext() {
        EsqlExecInfo esqlExecInfo = execContext.get();
        EsqlExecInfo newEsqlExecInfo = new EsqlExecInfo();
        if (esqlExecInfo != null) newEsqlExecInfo.setTransaction(esqlExecInfo.getTransaction());
        execContext.set(newEsqlExecInfo);
    }

    public Connection getConnection() {
        return getConfigTran(connectionName).getConnection();
    }

    @SuppressWarnings("unchecked")
    public <T> T execute(String... directSqls) {
        checkPreconditions(directSqls);
        newExecContext();

        Object ret = null;
        try {
            transaction = tranStart();

            for (EsqlSub subSql : createSqlSubs(directSqls))
                ret = execSub(transaction.getConnection(), ret, subSql);

            if (batch == null) tranCommit(transaction);
        } catch (SQLException e) {
            if (batch != null) batch.cleanupBatch();
            throw new EsqlExecuteException("exec sql failed[" + Esql.getExecContextInfo().getSql() + "]"
                    + e.getMessage());
        } finally {
            if (batch == null) tranClose(transaction);
        }

        return (T) ret;
    }

    private void checkPreconditions(String... directSqls) {
        if (esqlItem != null || directSqls.length > 0) return;

        throw new EsqlExecuteException("No sqlid defined!");
    }

    private Object execSub(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        Esql.getExecContextInfo().setSql(subSql.getSql());

        try {
            return isDdl(subSql.getSql()) ? execDdl(conn, realSql(subSql)) : pageExecute(conn, ret, subSql);
        } catch (EsqlExecuteException ex) {
            if (!subSql.getEsqlItem().isOnerrResume()) throw ex;
        }

        return 0;
    }

    private boolean execDdl(Connection conn, String sql) {
        Statement stmt = null;
        logger.info("sql:{}", sql);
        try {
            stmt = conn.createStatement();
            return stmt.execute(sql);
        } catch (SQLException ex) {
            throw new EsqlExecuteException(ex);
        } finally {
            RClose.closeQuiety(stmt);
        }
    }

    private Object pageExecute(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        if (page == null || !subSql.isLastSelectSql())
            return execDml(conn, ret, subSql);

        if (page.getTotalRows() == 0) page.setTotalRows(executeTotalRowsSql(conn, ret, subSql));

        return executePageSql(conn, ret, subSql);
    }

    private Object executePageSql(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        // oracle专用物理分页。
        EsqlSub pageSql = createOraclePageSql(subSql);

        return execDml(conn, ret, pageSql);
    }

    private int executeTotalRowsSql(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        EsqlSub totalSqlSub = subSql.clone();
        createTotalSql(totalSqlSub);

        return (Integer) execDml(conn, ret, totalSqlSub);
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

    private Object execDml(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        Object execRet = batch != null ? execDmlInBatch(conn, ret, subSql) : execDmlNoBatch(conn, ret, subSql);
        logger.info("result: {}", execRet);
        getExecContextInfo().addReturn(execRet);

        return execRet;
    }

    private Object execDmlInBatch(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        return batch.processBatchUpdate(conn, subSql);
    }

    private Object execDmlNoBatch(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        ResultSet rs = null;
        try {
            PreparedStatement ps = prepareSql(conn, subSql, realSql(subSql));
            new EsqlParamsBinder().bindParams(ps, subSql, params, logger);

            Object execRet = null;
            switch (subSql.getSqlType()) {
            case SELECT:
                rs = ps.executeQuery();
                execRet = convert(rs, subSql);
                break;
            case CALL:
                execRet = execAndRetrieveProcedureRet(subSql, (CallableStatement) ps);
                break;
            default:
                execRet = ps.executeUpdate();
            }

            return execRet;
        } finally {
            RClose.closeQuiety(rs, rs != null ? rs.getStatement() : null);
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

    public PreparedStatement prepareSql(Connection conn, EsqlSub subSql, String realSql) throws SQLException {
        logger.info("sql: {} ", realSql);
        return subSql.getSqlType() == EsqlType.CALL ? conn.prepareCall(realSql) : conn.prepareStatement(realSql);
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
        getExecContextInfo();

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

    protected EsqlTransaction tranStart() {
        EsqlTransaction outerTran = getOuterTran();
        if (outerTran != null) return outerTran;

        if (batch != null && transaction != null) return transaction;

        outerTran = getConfigTran(connectionName);
        outerTran.start();

        return outerTran;
    }

    protected void tranCommit(EsqlTransaction tran) {
        if (getOuterTran() != null) return;

        tran.commit();
    }

    protected void tranClose(EsqlTransaction tran) {
        if (getOuterTran() != null) return;

        RClose.closeQuiety(tran);
    }

    public EsqlTransaction newTransaction() {
        EsqlTransaction tran = getConfigTran(connectionName);
        getExecContextInfo().setTransaction(tran);

        return tran;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public static void startTran(Esql esql) {
        EsqlTransaction transaction = esql.newTransaction();
        transaction.start();
    }

    public static void rollbackTran() {
        EsqlTransaction tran = getOuterTran();
        if (tran != null) tran.rollback();
    }

    public static void commit() {
        EsqlTransaction tran = getOuterTran();
        if (tran != null) tran.commit();
    }

    public static void closeTran() {
        EsqlTransaction tran = getOuterTran();
        if (tran != null) RClose.closeQuiety(tran);
    }

    public String getSqlPath() {
        return sqlClassPath;
    }

    private static EsqlTransaction getOuterTran() {
        return getExecContextInfo().getTransaction();
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
        return this;
    }

    public int executeBatch() {
        int totalRows = 0;
        try {
            totalRows = batch.processBatchExecution();
            tranCommit(transaction);
        } catch (SQLException e) {
            throw new EsqlExecuteException("executeBatch failed:" + e.getMessage());
        } finally {
            tranClose(transaction);
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
