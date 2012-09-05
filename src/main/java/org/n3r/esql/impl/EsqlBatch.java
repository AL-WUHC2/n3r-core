package org.n3r.esql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.n3r.core.lang.RClose;
import org.n3r.esql.Esql;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.param.EsqlParamsBinder;
import org.n3r.esql.res.EsqlSub;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EsqlBatch {
    private Esql esql;
    //    private int maxBatches;
    //    private int currentBatches;
    private List<PreparedStatement> batchedPs;
    private Map<String, PreparedStatement> batchedMap;

    public EsqlBatch(Esql esql) {
        this.esql = esql;
    }

    public void startBatch(int maxBatches) {
        //        this.maxBatches = maxBatches;
        startBatch();
    }

    public void startBatch() {
        batchedPs = Lists.newArrayList();
        batchedMap = Maps.newHashMap();
    }

    public int processBatchUpdate(EsqlSub subSql) throws SQLException {
        String realSql = esql.realSql(subSql);
        PreparedStatement ps = batchedMap.get(realSql);
        if (ps == null) {
            ps = esql.prepareSql(subSql, realSql);
            batchedMap.put(realSql, ps);
            batchedPs.add(ps);
        }
        new EsqlParamsBinder().bindParams(ps, subSql, esql.getParams(), esql.getLogger());
        ps.addBatch();
        //        ++currentBatches;
        return /*maxBatches > 0 && currentBatches >= maxBatches ? executeBatch() :*/0;
    }

    public int processBatchExecution() throws SQLException {
        try {
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
        } finally {
            cleanupBatch();
        }
    }

    public void cleanupBatch() {
        for (PreparedStatement ps : batchedPs)
            RClose.closeQuietly(ps);
    }
}
