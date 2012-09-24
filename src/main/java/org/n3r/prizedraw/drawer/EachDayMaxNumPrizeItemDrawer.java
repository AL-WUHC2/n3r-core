package org.n3r.prizedraw.drawer;

import org.n3r.core.lang.RDate;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.util.PrizeUtils;

public class EachDayMaxNumPrizeItemDrawer extends SimplePrizeItemDrawer {

    @Override
    public PrizeItem drawPrize(PrizeItem prizeItem, PrizeActivity prizeActivity, Object userInfo) {
        if (prizeItem == null) return null;

        // 使用随机数判断是否通过幸运抽选关
        if (!PrizeUtils.hasLucky(prizeItem)) return null;

        String propertyName = prizeItem.getItemId() + "-MaxDay-" + RDate.toDateStr();
        Number num = new Esql().selectFirst("selectEachDayMaxNum")
                .params(prizeActivity.getActivityId(), propertyName)
                .execute();

        if (num != null && num.intValue() > PrizeUtils.getDayMax(prizeItem, 100)) return null;

        return decreaseItemNum(prizeItem);
    }

}
