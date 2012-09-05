package org.n3r.prizedraw.checker;

import org.n3r.core.lang.RBean;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawChecker;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.impl.PrizeDrawCheckException;

/**
 * 每天最多中奖一次。
 */
public class DayOnceBingooPrizeDrawChecker implements PrizeDrawChecker {

    @Override
    public void check(PrizeActivity prizeActivity, Object userInfo) {
        String userId = RBean.getProperty(userInfo, "userId");
        Object exists = new Esql().selectFirst("checkBingooTodayRecord")
                .params(prizeActivity.getActivityId(), userId)
                .execute();

        if (exists != null) throw new PrizeDrawCheckException("用户今天已经中过奖");
    }

}
