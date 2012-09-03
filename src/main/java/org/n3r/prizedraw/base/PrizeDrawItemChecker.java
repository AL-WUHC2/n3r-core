package org.n3r.prizedraw.base;

import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;

public interface PrizeDrawItemChecker {
    void check(PrizeActivity prizeActivity, Object userInfo, PrizeItem prizeItem);
}
