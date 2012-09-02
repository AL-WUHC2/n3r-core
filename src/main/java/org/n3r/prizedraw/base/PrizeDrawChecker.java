package org.n3r.prizedraw.base;

import org.n3r.prizedraw.impl.PrizeActivity;

public interface PrizeDrawChecker {
    void check(PrizeActivity prizeActivity, Object userInfo);
}
