package org.n3r.prizedraw.util;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.text.RRand;
import org.n3r.prizedraw.drawer.EachDayMaxNumPrizeItemDrawer;
import org.n3r.prizedraw.drawer.PrizeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrizeUtils {
    private static Logger log = LoggerFactory.getLogger(EachDayMaxNumPrizeItemDrawer.class);

    public static boolean hasLucky(PrizeItem prizeItem) {
        int rand = RRand.randInt(prizeItem.getItemRandbase());
        return rand == prizeItem.getItemLucknum() % prizeItem.getItemRandbase();
    }

    public static int getDayMax(PrizeItem prizeItem, int defaultValue) {
        try {
            return Integer.parseInt(StringUtils.defaultString(prizeItem.getItemSpec(), "" + defaultValue));
        } catch (NumberFormatException ex) {
            log.warn("EachDayMaxNumPrizeItemDrawer需要配置item_spec为正数");
            return defaultValue;
        }
    }
}
