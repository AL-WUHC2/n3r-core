package org.n3r.esql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RClassPath;
import org.n3r.core.lang.RClose;
import org.n3r.core.lang.RJavaScript;
import org.n3r.esql.config.EsqlConfigManager;
import org.n3r.esql.config.EsqlConfigable;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.ex.EsqlIdNotFoundException;
import org.n3r.esql.impl.EsqlExecInfo;
import org.n3r.esql.impl.SelectType;
import org.n3r.esql.map.AfterProperitesSet;
import org.n3r.esql.map.EsqlBeanRowMapper;
import org.n3r.esql.map.EsqlRowMapper;
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
import com.google.common.collect.Maps;

import static org.n3r.esql.util.EsqlUtils.*;

import static org.apache.commons.lang3.StringUtils.*;

public class Esql {
    private Class<?> returnType;
    private SelectType selectType;

    public static final String DEFAULT_CONNECTION_NAME = "DEFAULT";
    protected Logger logger = LoggerFactory.getLogger(Esql.class);
    protected String connectionName = DEFAULT_CONNECTION_NAME;
    protected EsqlItem esqlItem;
    protected Object[] params;
    protected String sqlClassPath;
    private EsqlPage page;
    private boolean inBatchMode;
    private int maxBatches;
    private int currentBatches;
    private List<PreparedStatement> batchedPs;
    private Map<String, PreparedStatement> batchedMap;
    private EsqlTransaction transaction;
    private Object[] dynamics;

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

    @SuppressWarnings("unchecked")
    public <T> T execute(String... directSqls) {
        checkPreconditions(directSqls);
        newExecContext();

        Object ret = null;
        try {
            this.transaction = tranStart();

            for (EsqlSub subSql : createSqlSubs(directSqls))
                ret = executeSub(transaction.getConnection(), ret, subSql);

            if (!inBatchMode) tranCommit(transaction);
        }
        catch (SQLException e) {
            if (inBatchMode) cleanupBatch();
            throw new EsqlExecuteException("exec sql failed[" + Esql.getExecContextInfo().getSql() + "]"
                    + e.getMessage());
        }
        finally {
            if (!inBatchMode) tranClose(transaction);
        }

        return (T) ret;
    }

    private void checkPreconditions(String... directSqls) {
        if (this.esqlItem == null && directSqls.length == 0) throw new EsqlExecuteException("No sqlid defined!");
    }

    private Object executeSub(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        Esql.getExecContextInfo().setSql(subSql.getSql());

        try {
            return isDdl(subSql.getSql()) ? executeImmedate(conn, getRealSql(subSql))
                    : pageExecute(conn, ret, subSql);
        }
        catch (EsqlExecuteException ex) {
            boolean onerrResume = subSql.getEsqlItem().isOnerrResume();
            if (!onerrResume) throw ex;
        }

        return 0;
    }

    private Object pageExecute(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        if (this.page == null || !subSql.isLastSelectSql())
            return executeNoneDdl(conn, ret, subSql);

        if (this.page.getTotalRows() == 0) this.page.setTotalRows(executeTotalRowsSql(conn, ret, subSql));

        return executePageSql(conn, ret, subSql);
    }

    private Object executePageSql(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        // oracle专用物理分页。
        EsqlSub pageSql = createOraclePageSql(subSql);

        return executeNoneDdl(conn, ret, pageSql);
    }

    private int executeTotalRowsSql(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        EsqlSub totalSqlSub = subSql.clone();
        createTotalSql(totalSqlSub);

        return (Integer) executeNoneDdl(conn, ret, totalSqlSub);
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
        int fromPos2 = sql.indexOf("FROM", fromPos1 + 4);
        subSql.setSql(fromPos2 > 0 ? "SELECT COUNT(*) CNT__ FROM (" + sql + ")"
                : "SELECT COUNT(*) AS CNT " + sql.substring(fromPos1));
        subSql.setWillReturnOnlyOneRow(true);
    }

    private Object executeNoneDdl(Connection conn, Object ret, EsqlSub subSql) throws SQLException {
        PreparedStatement ps = null;
        Object execRet = null;
        ResultSet rs = null;
        try {
            if (!this.inBatchMode) {
                String realSql = getRealSql(subSql);
                logger.info("sql:{}", realSql);
                ps = conn.prepareStatement(realSql);
                logger.info("bind parameters:{}", new EsqlParamsBinder().bindParams(ps, subSql, params));
            }
            switch (subSql.getSqlType()) {
            case SELECT:
                rs = ps.executeQuery();
                execRet = convert(rs, subSql);
                break;
            default:
                if (!this.inBatchMode) execRet = ps.executeUpdate();
                else execRet = processBatchUpdate(conn, subSql);
            }
            logger.info("execute result:{}", execRet);
            getExecContextInfo().addReturn(execRet);

            return execRet;
        }
        finally {
            RClose.closeQuiety(rs, ps);
        }
    }

    private int processBatchUpdate(Connection conn, EsqlSub subSql) throws SQLException {
        String realSql = getRealSql(subSql);
        PreparedStatement ps = batchedMap.get(realSql);
        if (ps == null) {
            logger.info("sql:{}", realSql);
            ps = conn.prepareStatement(realSql);
            batchedMap.put(realSql, ps);
            batchedPs.add(ps);
        }
        logger.info("bind batch parameters:{}", new EsqlParamsBinder().bindParams(ps, subSql, params));
        ps.addBatch();
        ++currentBatches;
        return maxBatches > 0 && currentBatches >= maxBatches ? executeBatch() : 0;
    }

    private String getRealSql(EsqlSub subSql) {
        return new EsqlDynamicReplacer().repaceDynamics(subSql, dynamics);
    }

    private int processBatchExecution() throws SQLException {
        int totalRowCount = 0;
        for (PreparedStatement ps : batchedPs) {
            int[] rowCounts = ps.executeBatch();
            for (int j = 0; j < rowCounts.length; j++)
                if (rowCounts[j] == Statement.SUCCESS_NO_INFO) ; // NOTHING TO DO
                else if (rowCounts[j] == Statement.EXECUTE_FAILED) throw new EsqlExecuteException(
                        "The batched statement at index " + j + " failed to execute.");
                else totalRowCount += rowCounts[j];
        }

        return totalRowCount;
    }

    public void cleanupBatch() {
        for (PreparedStatement ps : batchedPs)
            RClose.closeQuiety(ps);
    }

    private Object convert(ResultSet rs, EsqlSub subSql) throws SQLException {
        return selectType == SelectType.FIRST || subSql.isWillReturnOnlyOneRow()
                ? firstRow(rs) : selectList(rs);
    }

    private Object selectList(ResultSet rs) throws SQLException {
        List<Object> result = new ArrayList<Object>();

        for (int rownum = 1; rs.next(); ++rownum) {
            Object rowObject = rowBeanCreate(rs, rownum);
            if (rowObject != null) result.add(rowObject);
        }

        return result;
    }

    private Object rowBeanCreate(ResultSet rs, int rowNum) throws SQLException {
        Object rowBean = convertToRow(rs, rowNum);
        if (rowBean instanceof AfterProperitesSet)
            ((AfterProperitesSet) rowBean).afterPropertiesSet();

        return rowBean;
    }

    private Object convertToRow(ResultSet rs, int rowNum) throws SQLException {
        if (returnType == null && esqlItem != null) returnType = esqlItem.getReturnType();

        if (returnType != null && EsqlRowMapper.class.isAssignableFrom(returnType)) {
            EsqlRowMapper mapper = Reflect.on(returnType).create().get();
            return mapper.mapRow(rs, rowNum);
        }

        if (returnType != null) return new EsqlBeanRowMapper(returnType).mapRow(rs, rowNum);

        return result2Map(rs);
    }

    private Map<String, String> result2Map(ResultSet rs) throws SQLException {
        Map<String, String> row = new HashMap<String, String>();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); ++i) {
            String column = lookupColumnName(metaData, i);
            Object value = getResultSetValue(rs, i, String.class);
            row.put(column, toStr(value));
        }

        return row;
    }

    private String toStr(Object object) {
        if (object == null) return null;

        return object.toString().trim();
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

        this.sqlClassPath = sqlClassPath;

        if (isEmpty(this.sqlClassPath)) this.sqlClassPath = getSqlClassPath(4);

        Map<String, EsqlItem> sqlFile = EsqlSqlParser.parseSqlFile(this.sqlClassPath);
        esqlItem = sqlFile.get(sqlId);

        EsqlConfigable config = EsqlConfigManager.getConfig(connectionName);
        if (esqlItem == null && isNotEmpty(config.getSqlfromdb())) {
            sqlFile = EsqlSqlParser.parseSqlTable(config.getSqlfromdb(), connectionName);
            esqlItem = sqlFile.get(sqlId);
        }

        if (esqlItem == null) throwError(sqlId, config);
    }

    private void throwError(String sqlId, EsqlConfigable config) {
        StringBuilder errMsg = new StringBuilder(sqlId + " not found ");
        boolean useSqlFile = isNotEmpty(this.sqlClassPath);
        if (useSqlFile) errMsg.append("in sql file ").append(this.sqlClassPath);
        if (isNotEmpty(config.getSqlfromdb()))
            errMsg.append(useSqlFile ? " or " : "").append("in ").append(config.getSqlfromdb());

        throw new EsqlIdNotFoundException(errMsg.toString());
    }

    protected List<EsqlSub> createSqlSubs(String[] directSqls) {
        if (directSqls.length == 0)
            return esqlItem.createSqlSubs(params != null && params.length > 0 ? params[0] : null);

        return createSqlSubsByDirectSqls(directSqls);
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
        this.sqlClassPath = "/" + sqlBoundClass.getName().replace('.', '/') + ".esql";
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
        this.selectType = SelectType.LIST;
        return this;
    }

    public Esql selectFirst(String sqlId) {
        initSqlId(sqlId, sqlClassPath);
        this.selectType = SelectType.FIRST;
        return this;
    }

    public String getSqlId() {
        return esqlItem.getSqlId();
    }

    protected EsqlTransaction tranStart() {
        EsqlTransaction outerTran = getOuterTran();
        if (outerTran != null) return outerTran;

        if (inBatchMode && this.transaction != null) return transaction;

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
        return this.sqlClassPath;
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
        this.maxBatches = maxBatches;
        return startBatch();
    }

    public Esql startBatch() {
        this.inBatchMode = true;
        batchedPs = Lists.newArrayList();
        batchedMap = Maps.newHashMap();

        return this;
    }

    public int executeBatch() {
        int totalRows = 0;
        try {
            totalRows = processBatchExecution();
            tranCommit(transaction);
        }
        catch (SQLException e) {
            throw new EsqlExecuteException("executeBatch failed:" + e.getMessage());
        }
        finally {
            cleanupBatch();
            tranClose(transaction);
        }

        this.inBatchMode = false;
        batchedPs = null;
        batchedMap = null;
        maxBatches = -1;
        return totalRows;
    }

    public Esql dynamics(Object... dynamics) {
        this.dynamics = dynamics;

        return this;
    }
}
