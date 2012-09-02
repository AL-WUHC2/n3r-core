package org.n3r.prizedraw.base;

import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;

public interface PrizeDrawResulter {
    void result(PrizeItem drawResult, PrizeActivity prizeActivity, Object userInfo);
}
