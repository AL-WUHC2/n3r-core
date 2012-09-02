package org.n3r.prizedraw.drawer;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n3r.core.lang.RDate;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class ItemSpecParser {
    private final static Pattern dayPattern = Pattern.compile("\\d{4}-\\d\\d-\\d\\d");
    private final static Pattern hourPattern = Pattern.compile("(\\d\\d:\\d\\d)~(\\d\\d:\\d\\d).*?(\\d+)");

    public static class DayItem {
        private Date day;
        private int total;
        private List<ItemTimeRange> configedTimeRanges = Lists.newArrayList();

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

        public List<ItemTimeRange> getConfigedTimeRanges() {
            return configedTimeRanges;
        }

        public void setConfigedTimeRanges(List<ItemTimeRange> configedTimeRanges) {
            this.configedTimeRanges = configedTimeRanges;
        }
    }

    /**
     * 解析奖项分配说明的字符串。
     * @param itemSpec
     * @return
     */
    public static List<DayItem> parseItemSpec(String itemSpec) {
        Splitter splitter = Splitter.on('\n').omitEmptyStrings().trimResults();

        List<DayItem> dayItems = Lists.newArrayList();
        for (String line : splitter.split(itemSpec)) {
            Matcher yearMatcher = dayPattern.matcher(line);
            Date day = yearMatcher.find() ? RDate.parse(yearMatcher.group()) : null;
            DayItem dayItem = new DayItem();

            dayItem.setDay(day);

            Matcher hourMatcher = hourPattern.matcher(line);
            while (hourMatcher.find()) {
                String hourFrom = hourMatcher.group(1);
                String hourTo = hourMatcher.group(2);
                String num = hourMatcher.group(3);
                ItemTimeRange itemTimeRange = new ItemTimeRange();
                itemTimeRange.setFromSeconds(hourFrom);
                itemTimeRange.setToSeconds(hourTo);
                itemTimeRange.setItems(Integer.valueOf(num));
                dayItem.getConfigedTimeRanges().add(itemTimeRange);
            }

            if (dayItem.getConfigedTimeRanges().size() > 0) dayItems.add(dayItem);
        }

        return dayItems;
    }

    public static class ItemTimeRange {
        private String fromSeconds; // 开始秒数(从午夜开始),包含
        private String toSeconds; // 结束秒数(从午夜开始),不包含
        private int items;

        public String getFromSeconds() {
            return fromSeconds;
        }

        public void setFromSeconds(String fromSeconds) {
            this.fromSeconds = fromSeconds;
        }

        public String getToSeconds() {
            return toSeconds;
        }

        public void setToSeconds(String toSeconds) {
            this.toSeconds = toSeconds;
        }

        public int getItems() {
            return items;
        }

        public void setItems(int items) {
            this.items = items;
        }

    }

}
