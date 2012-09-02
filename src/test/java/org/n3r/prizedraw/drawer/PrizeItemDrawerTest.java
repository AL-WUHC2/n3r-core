package org.n3r.prizedraw.drawer;

import org.junit.Test;

public class PrizeItemDrawerTest {

    @Test
    public void testDrawPrize() {
        SimplePrizeItemDrawer prizeItemDrawer = new SimplePrizeItemDrawer();
        PrizeItem lastDrawResult = new PrizeItem();
        lastDrawResult.setActivityId("Olympic");
        lastDrawResult.setItemId("1");
        lastDrawResult.setItemTotal(5);
        lastDrawResult.setItemRandbase(10000);
        lastDrawResult.setItemLucknum(787);
        lastDrawResult.setItemOut(0);
        lastDrawResult.setItemIn(5);
        lastDrawResult.setItemSpec("每天开1");

        PrizeItem drawPrize = prizeItemDrawer.drawPrize(lastDrawResult, null, null);
        System.out.println(drawPrize);
    }

}
