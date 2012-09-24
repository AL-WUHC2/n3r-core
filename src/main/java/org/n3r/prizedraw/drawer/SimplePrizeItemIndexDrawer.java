package org.n3r.prizedraw.drawer;

import java.util.List;

import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawItemChecker;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;

public class SimplePrizeItemIndexDrawer implements PrizeDrawer {

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        List<PrizeItem> items = getPrizeItems(prizeActivity);

        PrizeItem prizeItem = items.get(items.size() == 1 ? 0 : RRand.randInt(items.size()));

        if (prizeItem.getItemTotal() > 0 /* 有总数限制 */
                && prizeItem.getItemIn() <= 0 /* 该奖项已经没有剩余奖品 */) return null;

        for (PrizeDrawItemChecker checker : prizeItem.getItemCheckers())
            checker.check(prizeActivity, userInfo, prizeItem);

        return prizeItem;
    }

    protected List<PrizeItem> getPrizeItems(PrizeActivity prizeActivity) {
        return new Esql().select("selectPrizeItems").params(prizeActivity.getActivityId()).execute();
    }

}
