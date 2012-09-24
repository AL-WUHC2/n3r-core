package org.n3r.prizedraw.drawer;

import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTran;
import org.n3r.prizedraw.base.PrizeCommitter;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.util.PrizeUtils;

public class SimplePrizeItemDrawer implements PrizeDrawer {

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        if (lastDrawResult == null) return null;

        // 使用随机数判断是否通过幸运抽选关
        if (!PrizeUtils.hasLucky(lastDrawResult)) return null;

        return decreaseItemNum(lastDrawResult);
    }

    protected PrizeItem decreaseItemNum(PrizeItem lastDrawResult) {
        EsqlTran tran = PrizeCommitter.getTran(Esql.DEFAULT_CONN_NAME);

        // 检查是否还有可中奖数量
        int effRows = new Esql().useTran(tran).update("decreaseItemNum").params(lastDrawResult).execute();

        return effRows == 1 ? lastDrawResult : null;
    }

}
