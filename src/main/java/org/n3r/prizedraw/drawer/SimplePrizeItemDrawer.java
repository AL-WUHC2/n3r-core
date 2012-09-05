package org.n3r.prizedraw.drawer;

import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTransaction;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.util.PrizeUtils;

public class SimplePrizeItemDrawer implements PrizeDrawer {

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        if (lastDrawResult == null) return null;

        // 使用随机数判断是否通过幸运抽选关
        boolean hasLucky = PrizeUtils.randomize(lastDrawResult);
        if (!hasLucky) return null;

        Esql esql = new Esql();
        final EsqlTransaction transaction = esql.newTransaction();
        PrizeUtils.addCommitter(transaction);

        // 检查是否还有可中奖数量
        int effRows = esql.update("decreaseItemNum").params(lastDrawResult).execute();

        return effRows == 1 ? lastDrawResult : null;
    }

}
