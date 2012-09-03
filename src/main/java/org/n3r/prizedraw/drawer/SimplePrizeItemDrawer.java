package org.n3r.prizedraw.drawer;

import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;

public class SimplePrizeItemDrawer implements PrizeDrawer {

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        if (lastDrawResult == null) return null;

        // 使用随机数判断是否通过幸运抽选关
        int rand = RRand.randInt(lastDrawResult.getItemRandbase());
        boolean hasLucky = rand == lastDrawResult.getItemLucknum() % lastDrawResult.getItemRandbase();
        if (!hasLucky) return null;

        // 检查是否还有可中奖数量
        int effRows = new Esql().update("decreaseItemNum").params(lastDrawResult).execute();

        return effRows == 1 ? lastDrawResult : null;
    }
}
