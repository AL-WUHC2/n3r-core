package org.n3r.prizedraw.drawer;

import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;

public class NonePrizeDrawer implements PrizeDrawer {

    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        return null;
    }

}
