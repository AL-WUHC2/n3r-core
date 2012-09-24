package org.n3r.prizedraw.drawer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.time.DateUtils;
import org.n3r.core.lang.RClose;
import org.n3r.core.lang.RDate;
import org.n3r.core.security.RDigest;
import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTran;
import org.n3r.prizedraw.base.PrizeCommitter;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.drawer.ItemSpecParser.DayItem;
import org.n3r.prizedraw.drawer.ItemSpecParser.ItemTimeRange;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.util.PrizeUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 通过对发放策略进行解析，随机分配奖项到一些随机时间点上。
 * @author Bingoo
 *
 */
public class IntelligentPrizeItemDrawer implements PrizeDrawer {
    private PrizeActivity prizeActivity;

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        if (lastDrawResult == null) return null;
        this.prizeActivity = prizeActivity;

        tryParseItemSpec(lastDrawResult);

        // 使用随机数判断是否通过幸运抽选关
        boolean hasLucky = PrizeUtils.hasLucky(lastDrawResult);

        Esql esql = new Esql();
        final EsqlTran tran = PrizeCommitter.getTran(Esql.DEFAULT_CONN_NAME);

        int rows = 0;
        if (hasLucky) rows = esql.useTran(tran).update("itemDraw")
                .params(prizeActivity.getActivityId(), lastDrawResult.getItemId())
                .execute();

        // 幸运抽奖开始
        if (rows == 0) rows = esql.update("luckyDraw")
                .params(prizeActivity.getActivityId(), lastDrawResult.getItemId())
                .execute();

        // 抽中，减少可用奖项为1
        if (rows == 1) esql.useSqlFile(SimplePrizeItemDrawer.class)
                .update("decreaseItemNum").params(lastDrawResult).execute();

        return rows == 1 ? lastDrawResult : null;
    }

    /**
     * 尝试解析并更新发放策略。
     * @param prizeItem
     */
    public void tryParseItemSpec(PrizeItem prizeItem) {
        Esql esql = new Esql();
        EsqlTran tran = esql.newTran();
        try {
            tran.start();

            boolean hasExpiredUnusedItems = checkExpiredUnusedItems(esql, prizeItem);
            boolean specChanged = checkSpecChange(esql, prizeItem);

            if (hasExpiredUnusedItems || specChanged) {
                List<PrizeItemAssign> assigns = createAssigns(prizeItem,
                        prizeActivity.getActivityEff(), prizeActivity.getActivityExp());
                writeAssignsToDb(esql, prizeItem, assigns);
            }
            tran.commit();
        } finally {
            RClose.closeQuietly(tran);
        }
    }

    /**
     * 检查发放策略是否更新过。
     * @param esql
     * @param prizeItem
     * @return
     */
    private boolean checkSpecChange(Esql esql, PrizeItem prizeItem) {
        String md5 = RDigest.md5(prizeItem.getItemSpec());
        if (StringUtils.equals(prizeItem.getItemMd5(), md5)) return false;

        prizeItem.setItemMd5(md5);
        int rows = esql.update("updatePrizeItemMd5")
                .params(prizeItem).execute();
        return rows == 1;
    }

    /*
     * 每小时会重新会看一下是否有过期剩余选项
     * @param esql
     * @param prizeItem
     * @return
     */
    private boolean checkExpiredUnusedItems(Esql esql, PrizeItem prizeItem) {
        if (prizeItem.getItemCheckpoints() != null
                && prizeItem.getItemCheckpoints().compareTo(new Date()) > 0) return false;

        int rows = esql.update("updateItemCheckpoint")
                .params(prizeItem.getActivityId(), prizeItem.getItemId()).execute();
        if (rows == 0) return false;

        rows = esql.selectFirst("sumPastLeftPrizeItemNum")
                .params(prizeItem.getActivityId(), prizeItem.getItemId()).execute();

        return rows > 0;
    }

    private void writeAssignsToDb(Esql esql, PrizeItem prizeItem, List<PrizeItemAssign> assigns) {
        esql.delete("deletePrizeItemAssign")
                .params(prizeItem.getActivityId(), prizeItem.getItemId())
                .execute();
        esql.insert("insertPrizeItemAssign");

        for (PrizeItemAssign assign : assigns)
            esql.params(assign).execute();
    }

    public List<PrizeItemAssign> createAssigns(PrizeItem prizeItem, Date eff, Date exp) {
        List<PrizeItemAssign> assigns = Lists.newArrayList();

        if (!checkEffExp(eff, exp)) return assigns;

        List<DayItem> specDayItems = StringUtils.isNotEmpty(prizeItem.getItemSpec())
                ? ItemSpecParser.parseItemSpec(prizeItem.getItemSpec())
                : new ArrayList<DayItem>(0);

        if (eff.getTime() < System.currentTimeMillis()) eff = new Date();
        boolean effInToday = RDate.sameDay(eff, new Date());

        eff = DateUtils.truncate(eff, Calendar.DAY_OF_MONTH);
        exp = DateUtils.ceiling(exp, Calendar.DAY_OF_MONTH);

        int diffDays = (int) RDate.diffDays(exp, eff);
        int[] prizeNumInDay = computeDayItemNum(specDayItems, prizeItem, eff, diffDays);

        // 将每天的奖项随机分配到一天的随机时刻
        for (int i = 0, ii = diffDays; i < ii; ++i) {
            if (prizeNumInDay[i] == 0) continue;

            DayItem dayItem = findDayItem(specDayItems, RDate.addDays(eff, i));

            Map<Long, MutableInt> dayMap = effInToday && i == 0
                    ? createRandTimePointToday(dayItem, prizeNumInDay[i])
                    : createRandTimePointsFutureDay(dayItem, prizeNumInDay[i]);
            createAssignsInDay(prizeItem, eff, assigns, i, dayMap);
        }

        return assigns;
    }

    private DayItem findDayItem(List<DayItem> specDayItems, Date day) {
        for (DayItem dayItem : specDayItems)
            if (dayItem.getDay() != null && RDate.sameDay(dayItem.getDay(), day)) return dayItem;

        return null;
    }

    private int[] computeDayItemNum(List<DayItem> specDayItems, PrizeItem prizeItem, Date eff, int diffDays) {
        int[] prizeNumInDay = new int[diffDays];

        int items4Rand = preConfigDaysPrizeNum(specDayItems, prizeItem, eff, prizeNumInDay);

        if (items4Rand > 0) {
            int totalZeroNumDays = countZeroPrizeNumDays(diffDays, prizeNumInDay);
            randAssignLeftPrizeNum(diffDays, prizeNumInDay, items4Rand, totalZeroNumDays);
        }

        return prizeNumInDay;
    }

    private void randAssignLeftPrizeNum(int diffDays, int[] prizeNumInDay, int items4Rand, int totalZeroNumDays) {
        // 将剩余随机奖项数量分配到奖项数量为0的每天
        if (totalZeroNumDays > 0) for (int i = 0, ii = items4Rand; i < ii; ++i) {
            int dayRandIndex = RRand.randInt(totalZeroNumDays);

            int offset = 0;
            for (int j = 0; j < diffDays; ++j) {
                if (prizeNumInDay[i] > 0) continue;
                if (offset++ == dayRandIndex) {
                    ++prizeNumInDay[i];
                    --items4Rand;
                    break;
                }
            }
        }
        else for (int i = 0, ii = items4Rand; i < ii; ++i) {
            // 将剩余奖项数量分配到随机天
            int dayRandIndex = RRand.randInt(diffDays);
            ++prizeNumInDay[dayRandIndex];
        }
    }

    private int countZeroPrizeNumDays(int diffDays, int[] prizeNumInDay) {
        int totalZeroNumDays = 0;
        for (int i = 0; i < diffDays; ++i)
            totalZeroNumDays += prizeNumInDay[i] > 0 ? 0 : 1;
        return totalZeroNumDays;
    }

    private int preConfigDaysPrizeNum(List<DayItem> specDayItems, PrizeItem prizeItem, Date eff, int[] prizeNumInDay) {
        int items4Rand = prizeItem.getItemIn();

        int avgDayNum = specDayItems.size() > 0 && specDayItems.get(0).getDay() == null
                ? specDayItems.get(0).getTotal() : 0;
        TAG: for (int i = 0, ii = prizeNumInDay.length; i < ii && items4Rand > 0; ++i) {
            Date day = RDate.addDays(eff, i);
            for (DayItem dayItem : specDayItems)
                if (dayItem.getDay() != null && RDate.sameDay(day, dayItem.getDay())) {
                    if (items4Rand > dayItem.getTotal()) {
                        prizeNumInDay[i] = dayItem.getTotal();
                        items4Rand -= dayItem.getTotal();
                    }
                    else {
                        prizeNumInDay[i] = items4Rand;
                        items4Rand = 0;
                    }
                    continue TAG;
                }

            if (items4Rand - avgDayNum > 0) {
                prizeNumInDay[i] = avgDayNum;
                items4Rand -= avgDayNum;
            }
            else {
                prizeNumInDay[i] = items4Rand;
                items4Rand = 0;
            }
        }
        return items4Rand;
    }

    private boolean checkEffExp(Date eff, Date exp) {
        if (exp.getTime() < System.currentTimeMillis()) return false;
        if (eff.getTime() >= exp.getTime()) return false;

        return true;
    }

    private void createAssignsInDay(PrizeItem prizeItem, Date eff, List<PrizeItemAssign> assigns, int offsetDays,
            Map<Long, MutableInt> dayMap) {
        long lastSeconds = 0;
        for (Map.Entry<Long, MutableInt> entry : dayMap.entrySet()) {
            PrizeItemAssign assign = new PrizeItemAssign();
            assigns.add(assign);

            assign.setActivityId(prizeItem.getActivityId());
            assign.setItemId(prizeItem.getItemId());
            long offsetTime = RDate.addDays(eff, offsetDays).getTime();
            assign.setAssignTimeFrom(new Date(offsetTime + lastSeconds * 1000L));
            lastSeconds = entry.getKey() * 60L + RRand.randInt(60);
            assign.setAssignTimeTo(new Date(offsetTime + lastSeconds * 1000L));
            assign.setAssignNum(entry.getValue().intValue());
            assign.setAvailableNum(entry.getValue().intValue());
            // luckTime定在开始时间和结束之间之间，靠近结束时间的一个随时时间点
            long toTime = assign.getAssignTimeTo().getTime();
            long diffTime = toTime - assign.getAssignTimeFrom().getTime();
            long luckTime = toTime - (diffTime > 60000
                    ? RRand.randInt(50000) + 10000
                    : RRand.randInt((int) (diffTime / 2)));

            assign.setLuckTime(new Date(luckTime));
        }
    }

    private Map<Long, MutableInt> createRandTimePointsFutureDay(DayItem dayItem, int prizeNumInDay) {
        Map<Long, MutableInt> dayMap = createDayMapAndPreAssign(dayItem);

        for (int j = 0, jj = prizeNumInDay; j < jj; ++j) {
            int randHour = RRand.randInt(24);
            int randMini = RRand.randInt(60);
            long randMinutes = randHour * 60 + randMini;
            putMap(dayMap, randMinutes);
        }
        return dayMap;
    }

    private void putMap(Map<Long, MutableInt> dayMap, long randMinutes) {
        MutableInt num = dayMap.get(randMinutes);
        if (num != null) num.increment();
        else dayMap.put(randMinutes, new MutableInt(1));
    }

    private Map<Long, MutableInt> createDayMapAndPreAssign(DayItem dayItem) {
        Map<Long, MutableInt> dayMap = Maps.newTreeMap();
        if (dayItem == null) return dayMap;

        for (ItemTimeRange range : dayItem.getConfigedTimeRanges())
            for (int i = 0, ii = range.getNum(); i < ii; ++i) {
                long randTime = RRand.randInt(range.getToMinutes() - range.getFromMinutes()) + range.getFromMinutes();
                putMap(dayMap, randTime);
            }

        return dayMap;
    }

    private Map<Long, MutableInt> createRandTimePointToday(DayItem dayItem, int prizeNumInDay) {
        Map<Long, MutableInt> dayMap = createDayMapAndPreAssign(dayItem);

        Calendar wholePoint = DateUtils.truncate(Calendar.getInstance(), Calendar.HOUR_OF_DAY);
        Calendar midnight = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
        long currentMinutes = (System.currentTimeMillis() - midnight.getTimeInMillis()) / 60000;
        int hours = wholePoint.get(Calendar.HOUR_OF_DAY);
        for (int j = 0, jj = prizeNumInDay; j < jj; ++j) {
            int randHour = RRand.randInt(24 - hours) + hours;
            int randMini = RRand.randInt(60);
            long randMinutes = randHour * 60 + randMini;

            if (randMinutes < currentMinutes) randMinutes = currentMinutes;

            putMap(dayMap, randMinutes);
        }
        return dayMap;
    }
}
