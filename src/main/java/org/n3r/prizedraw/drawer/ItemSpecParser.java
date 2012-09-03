package org.n3r.prizedraw.drawer;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.n3r.core.lang.RBaseBean;
import org.n3r.core.lang.RDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * 解析形如一下内容的字符串
 每天开5
2012-08-10开20 10:10~12:30开10 14:00~15:00 20

 * @author Bingoo
 *
 */
public class ItemSpecParser {
    private final static Logger logger = LoggerFactory.getLogger(ItemSpecParser.class);
    private final static Pattern defaultPattern = Pattern.compile("\\d+");
    private final static Pattern dayPattern = Pattern.compile("(\\d{4}-\\d\\d-\\d\\d)[^\\d]*(\\d+)?\\s+"); // YYYY-MM-DD 开 5
    private final static Pattern hourPattern = Pattern.compile("(\\d\\d:\\d\\d)~(\\d\\d:\\d\\d).*?(\\d+)");

    /**
     * 解析奖项分配说明的字符串。
     * @param itemSpec
     * @return
     */
    public static List<DayItem> parseItemSpec(String itemSpec) {
        Splitter splitter = Splitter.on('\n').omitEmptyStrings().trimResults();
        DayItem defaultDayItem = new DayItem();
        LinkedList<DayItem> dayItems = Lists.newLinkedList();
        Date midnight = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        for (String line : splitter.split(itemSpec)) {
            Matcher yearMatcher = dayPattern.matcher(line);
            if (!yearMatcher.find()) {
                Matcher matcher = defaultPattern.matcher(line);
                if (matcher.find()) {
                    defaultDayItem.setDay(null);
                    defaultDayItem.setTotal(Integer.valueOf(matcher.group()));
                }
                continue;
            }

            DayItem dayItem = new DayItem();
            Date day = RDate.parse(yearMatcher.group(1));
            if (day.compareTo(midnight) < 0) {
                logger.warn("日期已经过去，忽略:{}", line);
                continue;
            }

            dayItem.setDay(day);
            if (yearMatcher.group(2) != null) dayItem.setTotal(Integer.parseInt(yearMatcher.group(2)));

            parseTimeRanges(midnight, dayItem, line);
            checkAndAddDayItem(dayItems, line, dayItem);
        }

        if (defaultDayItem.getTotal() > 0) dayItems.addFirst(defaultDayItem);

        return dayItems;
    }

    private static void checkAndAddDayItem(LinkedList<DayItem> dayItems, String line, DayItem dayItem) {
        for (DayItem item : dayItems)
            if (RDate.sameDay(item.getDay(), dayItem.getDay())) {
                logger.warn("日期重复，忽略:{}", line);
                return;
            }

        dayItems.add(dayItem);
    }

    private static void parseTimeRanges(Date midnight, DayItem dayItem, String line) {
        int total = 0;

        Matcher hourMatcher = hourPattern.matcher(line);
        TAG: while (hourMatcher.find() && (dayItem.getTotal() <= 0 || total < dayItem.getTotal())) {
            int fromMinutes = getMinutes(hourMatcher.group(1));
            int toMinutes = getMinutes(hourMatcher.group(2));
            if (fromMinutes < 0 || toMinutes < 0) {
                logger.warn("小时或者分钟超过范围，忽略:{}", line);
                continue;
            }
            if (fromMinutes >= toMinutes) {
                logger.warn("时间范围起始值大于等于结束值，忽略:{}", line);
                continue;
            }
            if (RDate.sameDay(midnight, dayItem.getDay()) && toMinutes < System.currentTimeMillis() / 60000) {
                logger.warn("时间范围在过去，忽略:{}", line);
                continue;
            }

            int num = Integer.valueOf(hourMatcher.group(3));

            // 检查总数是否超过当天设定值，如果超过，则自动削减到最大值，并且忽略后续时间段设定
            if (dayItem.getTotal() > 0 && total + num > dayItem.getTotal()) num = dayItem.getTotal() - total;

            total += num;

            if (hasRangeConnected(dayItem, line, fromMinutes, toMinutes)) continue TAG;

            addItem(dayItem, toMinutes, new ItemTimeRange(fromMinutes, toMinutes, num));
        }

        if (dayItem.getTotal() <= 0) dayItem.setTotal(total);
    }

    private static boolean connected(int from1, int to1, int from2, int to2) {
        return !(to1 <= from2 || to2 <= from1);
    }

    private static void addItem(DayItem dayItem, int toMinutes, ItemTimeRange itemTimeRange) {
        for (int i = 0, ii = dayItem.getConfigedTimeRanges().size(); i < ii; ++i) {
            ItemTimeRange range = dayItem.getConfigedTimeRanges().get(i);
            if (toMinutes <= range.getFromMinutes()) {
                dayItem.getConfigedTimeRanges().add(i, itemTimeRange);
                return;
            }
        }

        dayItem.getConfigedTimeRanges().add(itemTimeRange);
    }

    private static boolean hasRangeConnected(DayItem dayItem, String line, int fromMinutes, int toMinutes) {
        for (ItemTimeRange range : dayItem.getConfigedTimeRanges())
            if (connected(range.getFromMinutes(), range.getToMinutes(), fromMinutes, toMinutes)) {
                logger.warn("时间范围交叉，忽略:{}", line);
                return true;
            }

        return false;
    }

    private static int getMinutes(String hourFrom) {
        String[] parts = StringUtils.split(hourFrom, ":");
        int hour = Integer.valueOf(parts[0]);
        int minu = Integer.valueOf(parts[1]);
        return hour >= 24 || minu >= 60 ? -1 : hour * 60 + minu;
    }

    public static class DayItem extends RBaseBean {
        private Date day;
        private int total;
        private LinkedList<ItemTimeRange> configedTimeRanges = Lists.newLinkedList();

        public Date getDay() {
            return day;
        }

        public void setDay(Date day) {
            this.day = day;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public LinkedList<ItemTimeRange> getConfigedTimeRanges() {
            return configedTimeRanges;
        }

    }

    public static class ItemTimeRange extends RBaseBean {
        private int fromMinutes; // 开始分钟(从午夜开始) 包含
        private int toMinutes; // 结束分钟(从午夜开始),不包含
        private int num;

        public ItemTimeRange(int fromMinutes, int toMinutes, int num) {
            this.fromMinutes = fromMinutes;
            this.toMinutes = toMinutes;
            this.num = num;
        }

        public int getFromMinutes() {
            return fromMinutes;
        }

        public int getToMinutes() {
            return toMinutes;
        }

        public int getNum() {
            return num;
        }

    }

}
