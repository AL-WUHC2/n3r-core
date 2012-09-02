package org.n3r.prizedraw.checker;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RBean;
import org.n3r.core.lang.RDate;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawChecker;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.impl.PrizeDrawCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrequencyPrizeDrawChecker implements PrizeDrawChecker {
    private long timeUnitBySeconds;
    private int maxTimes;
    private int maxExceedTimes;
    private String[] byPropertyNames;
    private PrizeActivity prizeActivity;
    private Logger log = LoggerFactory.getLogger(getClass());
    private Object userInfo;

    public FrequencyPrizeDrawChecker(long timeUnitBySeconds, int maxTimes, int maxExceedTimes,
            String... byPropertyNames) {
        this.timeUnitBySeconds = timeUnitBySeconds;
        this.maxTimes = maxTimes;
        this.maxExceedTimes = maxExceedTimes;
        this.byPropertyNames = byPropertyNames;
    }

    @Override
    public void check(PrizeActivity prizeActivity, Object userInfo) {
        this.prizeActivity = prizeActivity;

        String frequenceTag = computeFrequenceTag(0);
        for (String byPropertyName : byPropertyNames)
            checkProperty(prizeActivity, userInfo, frequenceTag, byPropertyName);
    }

    private void checkProperty(PrizeActivity prizeActivity, Object userInfo, String frequenceTag, String byPropertyName) {
        this.userInfo = userInfo;

        String property = RBean.getProperty(userInfo, byPropertyName);
        if (StringUtils.isEmpty(property)) throw new PrizeDrawCheckException(byPropertyName + "属性取值为空");

        int times = new Esql().selectFirst("checkFrequency")
                .params(prizeActivity.getActivityId(), frequenceTag, byPropertyName, property)
                .execute();
        if (times > maxTimes) {
            checkMaxExceedTimes(frequenceTag, property, byPropertyName);
            throw new PrizeDrawCheckException("超过频率限制");
        }
    }

    private void checkMaxExceedTimes(String frequenceTag, String property, String byPropertyName) {
        String fromFrequenceTag = computeFrequenceTag(maxExceedTimes);
        int count = new Esql().selectFirst("checkMaxExceedTimes")
                .params(prizeActivity.getActivityId(), fromFrequenceTag, frequenceTag,
                        byPropertyName, property, maxTimes)
                        .execute();
        if (count == maxExceedTimes) {
            log.info("抽奖活动[{}]中，用户[{}={}连续超越抽奖频率{}，被加入黑名单]",
                    new Object[] { prizeActivity.getActivityId(), byPropertyName, property, maxTimes });
            String userId = RBean.getProperty(userInfo, "userId");
            new Esql().update("addToBlacklist")
            .params(prizeActivity.getActivityId(), byPropertyName, property,
                    "auto black at " + RDate.toDateTimeStr(), userId)
                    .execute();
        }
    }

    private String computeFrequenceTag(int offset) {
        int secondsFromMidnight = RDate.getSecondsFromMidnight();

        long truncPoint = secondsFromMidnight - secondsFromMidnight % timeUnitBySeconds;
        for (int i = 0; i < offset; ++i)
            if (truncPoint > 0) truncPoint -= timeUnitBySeconds;

        return RDate.toDateStr() + "." + truncPoint;
    }
}
