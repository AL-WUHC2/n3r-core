package org.n3r.prizedraw.base;

import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;

public interface PrizeDrawer {
    PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo);
}
