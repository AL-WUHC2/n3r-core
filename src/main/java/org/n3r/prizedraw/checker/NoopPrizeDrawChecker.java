package org.n3r.prizedraw.checker;

import org.n3r.prizedraw.base.PrizeDrawChecker;
import org.n3r.prizedraw.impl.PrizeActivity;

public class NoopPrizeDrawChecker implements PrizeDrawChecker {

    public void check(PrizeActivity prizeActivity, Object userInfo) {
    }

}
