package org.n3r.prizedraw.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RBaseBean;
import org.n3r.core.lang.RDuration;
import org.n3r.esql.map.AfterProperitesSet;
import org.n3r.prizedraw.base.PrizeDrawChecker;
import org.n3r.prizedraw.base.PrizeDrawResulter;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.checker.FrequencyPrizeDrawChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class PrizeActivity extends RBaseBean implements AfterProperitesSet {
    private String activityId;
    private Date activityEff;
    private Date activityExp;
    private String dupSpec;
    private String frequencySpec;
    private String checkers;
    private String drawers;
    private String resulters;

    private List<PrizeDrawChecker> prizeDrawCheckers = Lists.newArrayList();
    private List<PrizeDrawer> prizeDrawers = Lists.newArrayList();
    private List<PrizeDrawResulter> prizeDrawResulters = Lists.newArrayList();
    private PrizeDrawChecker frequencyPrizeDrawChecker;
    private static Pattern frequencySpecPattern = Pattern
            .compile("(\\d+)r?\\s*/(\\d+\\s*\\w)\\s*(\\d+)?.*?by\\s+([\\w_\\-.,]+)");
    private Logger log = LoggerFactory.getLogger(PrizeActivity.class);

    public void afterPropertiesSet() {
        Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
        splitFunctors(splitter, checkers, prizeDrawCheckers);
        splitFunctors(splitter, drawers, prizeDrawers);
        splitFunctors(splitter, resulters, prizeDrawResulters);
        parseFrequencySpec(); // 频率控制描述解析
    }

    private void parseFrequencySpec() {
        if (StringUtils.isEmpty(frequencySpec)) return;

        Matcher matcher = frequencySpecPattern.matcher(frequencySpec);
        if (!matcher.matches()) {
            log.warn("抽奖活动[" + activityId + "]中配置的频率控制不符合格式要求");
            return;
        }

        int maxTimes = Integer.valueOf(matcher.group(1));
        String timeUnit = matcher.group(2);
        String maxExceeds = matcher.group(3);
        String byPropertyNames = matcher.group(4);

        long durationSeconds = RDuration.parseDuration(timeUnit, TimeUnit.SECONDS);
        int maxExceedTimes = maxExceeds == null ? 0 : Integer.valueOf(maxExceeds);

        Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
        String[] byPropertyNamesArr = Iterables.toArray(splitter.split(byPropertyNames), String.class);

        frequencyPrizeDrawChecker = new FrequencyPrizeDrawChecker(durationSeconds, maxTimes, maxExceedTimes,
                byPropertyNamesArr);
    }

    private <T> void splitFunctors(Splitter splitter, String functorsStr, List<T> result) {
        for (String functorStr : splitter.split(functorsStr)) {
            T functor = Reflect.on(functorStr).create().get();
            result.add(functor);
        }
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Date getActivityEff() {
        return activityEff;
    }

    public void setActivityEff(Date activityEff) {
        this.activityEff = activityEff;
    }

    public Date getActivityExp() {
        return activityExp;
    }

    public void setActivityExp(Date activityExp) {
        this.activityExp = activityExp;
    }

    public String getDupSpec() {
        return dupSpec;
    }

    public void setDupSpec(String dupSpec) {
        this.dupSpec = dupSpec;
    }

    public String getFrequencySpec() {
        return frequencySpec;
    }

    public void setFrequencySpec(String frequencySpec) {
        this.frequencySpec = frequencySpec;
    }

    public String getCheckers() {
        return checkers;
    }

    public void setCheckers(String checkers) {
        this.checkers = checkers;
    }

    public String getDrawers() {
        return drawers;
    }

    public void setDrawers(String drawers) {
        this.drawers = drawers;
    }

    public String getResulters() {
        return resulters;
    }

    public void setResulters(String resulters) {
        this.resulters = resulters;
    }

    public List<PrizeDrawChecker> getPrizeDrawCheckers() {
        return prizeDrawCheckers;
    }

    public List<PrizeDrawer> getPrizeDrawers() {
        return prizeDrawers;
    }

    public List<PrizeDrawResulter> getPrizeDrawResulters() {
        return prizeDrawResulters;
    }

    public PrizeDrawChecker getFrequencyPrizeDrawChecker() {
        return frequencyPrizeDrawChecker;
    }

}
