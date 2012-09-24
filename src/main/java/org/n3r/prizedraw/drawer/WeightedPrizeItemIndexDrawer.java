package org.n3r.prizedraw.drawer;

import java.util.List;

import org.n3r.core.text.RRand;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.impl.PrizeDrawCheckException;

import com.google.common.collect.Lists;

/**
 * 将各个奖项按数量进行加权，再随机取奖项。
 * @author Bingoo
 *
 */
public class WeightedPrizeItemIndexDrawer extends SimplePrizeItemIndexDrawer {
    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        List<PrizeItem> items = getPrizeItems(prizeActivity);
        int total = 0;

        List<PrizeItem> unlimitedItems = Lists.newArrayList();
        for (PrizeItem prizeItem : items)
            if (prizeItem.getItemTotal() > 0) total += prizeItem.getItemIn();
            else unlimitedItems.add(prizeItem);

        // 已经全部抽取完毕，没有剩余奖项
        if (total == 0 && unlimitedItems.size() == 0) return null;
        if (total == 0 && unlimitedItems.size() > 0) return unlimitedItems.get(RRand.randInt(unlimitedItems.size()));

        int randItemIndex = RRand.randInt(total);
        total = 0;
        for (PrizeItem prizeItem : items)
            if (prizeItem.getItemTotal() > 0) {
                if (randItemIndex >= total
                        && randItemIndex < total + prizeItem.getItemTotal()) return prizeItem;
                total += prizeItem.getItemTotal();
            }

        throw new PrizeDrawCheckException("should not happen!");
    }

}
