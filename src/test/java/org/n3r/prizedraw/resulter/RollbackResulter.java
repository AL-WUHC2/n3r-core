package org.n3r.prizedraw.resulter;

import org.n3r.prizedraw.base.PrizeCommitter;
import org.n3r.prizedraw.base.PrizeDrawResulter;
import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;

public class RollbackResulter implements PrizeDrawResulter {

    @Override
    public PrizeItem result(PrizeItem drawResult, PrizeActivity prizeActivity, Object userInfo) {
        PrizeCommitter.rollback();

        return null;
    }

}
