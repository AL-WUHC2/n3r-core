package org.n3r.prizedraw;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawChecker;
import org.n3r.prizedraw.base.PrizeDrawResulter;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.drawer.PrizeItem;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.impl.PrizeDrawCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrizeDraw {
    private Logger log = LoggerFactory.getLogger(PrizeDraw.class);
    private PrizeActivity prizeActivity;

    public PrizeDraw(String activityId) {
        prizeActivity = findPrizeActivity(activityId);
    }

    /**
     * 根据用户信息进行抽奖。
     * @param userInfo 用户信息，POJO或者Map
     * @return 奖项ID。如果返回null,表名没有中奖
     */
    public PrizeItem draw(Object userInfo) {
        try {
            prizeDrawCheck(userInfo);
            PrizeItem drawResult = prizeDraw(userInfo);
            prizeResultProcess(userInfo, drawResult);

            return drawResult;
        } catch (PrizeDrawCheckException ex) {
            log.info("抽奖校验不通过:{}", ex.getMessage());
            return null;
        }
    }

    private void prizeResultProcess(Object userInfo, PrizeItem drawResult) {
        for (PrizeDrawResulter prizeDrawResulter : prizeActivity.getPrizeDrawResulters())
            prizeDrawResulter.result(drawResult, prizeActivity, userInfo);
    }

    private PrizeItem prizeDraw(Object userInfo) {
        PrizeItem drawResult = null;
        for (PrizeDrawer prizeDrawer : prizeActivity.getPrizeDrawers())
            drawResult = prizeDrawer.drawPrize(drawResult, prizeActivity, userInfo);

        return drawResult;
    }

    private void prizeDrawCheck(Object userInfo) {
        for (PrizeDrawChecker checker : prizeActivity.getPrizeDrawCheckers())
            checker.check(prizeActivity, userInfo);

        if (prizeActivity.getFrequencyPrizeDrawChecker() == null) return;

        prizeActivity.getFrequencyPrizeDrawChecker().check(prizeActivity, userInfo);
    }

    private PrizeActivity findPrizeActivity(String activityId) {
        List<PrizeActivity> prizeActivities = new Esql().select("GetAllActivities").execute();

        for (PrizeActivity activity : prizeActivities)
            if (StringUtils.equals(activityId, activity.getActivityId())) return activity;

        throw new RuntimeException(activityId + " not defined properly!");
    }
}
