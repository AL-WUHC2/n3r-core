package org.n3r.prizedraw.resulter;

import org.n3r.core.lang.RBean;
import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTran;
import org.n3r.prizedraw.base.PrizeCommitter;
import org.n3r.prizedraw.base.PrizeDrawResulter;
import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;

public class LogPrizeDrawResulter implements PrizeDrawResulter {

    @Override
    public PrizeItem result(PrizeItem drawResult, PrizeActivity prizeActivity, Object userInfo) {
        String userId = RBean.getProperty(userInfo, "userId");

        if (drawResult != null) {
            String orderNo = RRand.randNum(10);
            Esql esql = new Esql();
            final EsqlTran tran = PrizeCommitter.getTran(Esql.DEFAULT_CONN_NAME);
            esql.useTran(tran).insert("insertPrizeBingoo")
                    .params(orderNo, prizeActivity.getActivityId(),
                            drawResult.getItemId(), userId, drawResult.isItemJoin())
                    .execute();
        }

        PrizeRecord prizeRecord = new PrizeRecord();
        prizeRecord.setActivityId(prizeActivity.getActivityId());
        prizeRecord.setUserId(userId);
        prizeRecord.setBingooTimes(drawResult != null ? 1 : 0);

        new Esql().update("logPrizeRecord").params(prizeRecord).execute();

        return drawResult;
    }
}
