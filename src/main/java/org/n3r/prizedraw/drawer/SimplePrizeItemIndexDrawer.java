package org.n3r.prizedraw.drawer;

import java.util.List;

import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;

public class SimplePrizeItemIndexDrawer implements PrizeDrawer {

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        List<PrizeItem> items = getPrizeItems(prizeActivity);
        int randItemIndex = RRand.randInt(items.size());

        PrizeItem prizeItem = items.get(randItemIndex);

        // 该奖项已经没有剩余奖品
        if (prizeItem.getItemIn() <= 0) return null;

        return prizeItem;
    }

    protected List<PrizeItem> getPrizeItems(PrizeActivity prizeActivity) {
        return new Esql().select("selectPrizeItems").params(prizeActivity.getActivityId()).execute();
    }

}
