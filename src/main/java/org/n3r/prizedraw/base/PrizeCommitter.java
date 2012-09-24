package org.n3r.prizedraw.base;

import java.util.HashMap;
import java.util.Map;

import org.n3r.core.lang.RClose;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTran;

public class PrizeCommitter {
    private static ThreadLocal<Map<String, EsqlTran>> transLocal = new ThreadLocal<Map<String, EsqlTran>>() {
        @Override
        protected Map<String, EsqlTran> initialValue() {
            return new HashMap<String, EsqlTran>();
        }
    };

    private static ThreadLocal<Boolean> commitOrRollbackFlag = new ThreadLocal<Boolean>();

    public static EsqlTran getTran(String connName) {
        EsqlTran esqlTran = transLocal.get().get(connName);
        if (esqlTran != null) return esqlTran;

        esqlTran = Esql.newTran(connName);
        esqlTran.start();
        transLocal.get().put(connName, esqlTran);

        return esqlTran;
    }

    public static void commit() {
        if (commitOrRollbackFlag.get() != null) return;

        for (EsqlTran tran : transLocal.get().values())
            tran.commit();

        commitOrRollbackFlag.set(true);
    }

    public static void rollback() {
        if (commitOrRollbackFlag.get() != null) return;

        for (EsqlTran tran : transLocal.get().values())
            tran.rollback();

        commitOrRollbackFlag.set(true);
    }

    public static void close() {
        for (EsqlTran tran : transLocal.get().values())
            RClose.closeQuietly(tran);

        transLocal.get().clear();

        commitOrRollbackFlag.set(null);
    }

}
