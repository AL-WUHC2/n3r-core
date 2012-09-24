package org.n3r.prizedraw.resulter;

import org.n3r.core.lang.RDate;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTran;
import org.n3r.prizedraw.base.PrizeCommitter;
import org.n3r.prizedraw.base.PrizeDrawResulter;
import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.util.PrizeUtils;

public class EachDayMaxNumResulter implements PrizeDrawResulter {

    @Override
    public PrizeItem result(PrizeItem drawResult, PrizeActivity prizeActivity, Object userInfo) {
        if (drawResult == null) return null;

        final EsqlTran tran = PrizeCommitter.getTran(Esql.DEFAULT_CONN_NAME);
        String propertyName = drawResult.getItemId() + "-MaxDay-" + RDate.toDateStr();
        int rows = new Esql().useTran(tran).update("increaseDayMax")
                .params(prizeActivity.getActivityId(), propertyName, PrizeUtils.getDayMax(drawResult, 100))
                .execute();

        return rows == 1 ? drawResult : null;
    }
}
