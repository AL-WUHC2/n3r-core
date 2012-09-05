package org.n3r.prizedraw.drawer;

import java.util.List;

import org.junit.Test;
import org.n3r.prizedraw.drawer.ItemSpecParser.DayItem;

public class ItemSpecParserTest {

    @Test
    public void testParseItemSpec() {
        new ItemSpecParser();

        // 正常情况
        List<DayItem> dayItems = ItemSpecParser.parseItemSpec("鬼扯蛋一行没用\r\n每天开5\r\n"
                + "2012-08-10开20 10:10~12:30开10 12:30~15:00 5"
                + "2012-08-11开20 10:10~12:30开10 12:30~15:00 5"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);

        // 日期重复，重复部分被忽略
        dayItems = ItemSpecParser.parseItemSpec("每天开5\r\n"
                + "2012-09-10开20 10:10~12:30开10 12:30~15:00 5\r\n"
                + "2012-09-10开20 10:10~12:30开10 12:30~15:00 5"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);

        // 小时超过24，分钟超过60
        dayItems = ItemSpecParser.parseItemSpec("每天开5\r\n"
                + "2012-08-10开20 10:10~12:30开10 12:30~15:00 5\r\n"
                + "2012-09-11开20 10:10~12:30开10 12:20~15:00 5, 15:00~25:00 6, 15:98~16:00 1"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);

        // 时间范围累积数量超过指定数量
        dayItems = ItemSpecParser.parseItemSpec("每天开5\r\n"
                + "2012-08-10开20 10:10~12:30开10 12:30~15:00 25\r\n"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);

        // 不指定当天数量，由时间范围数量累加
        dayItems = ItemSpecParser.parseItemSpec("每天开5\r\n"
                + "2012-09-10 10:10~12:30开10 12:30~15:00 25\r\n"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);

        // 不指定每天平均数量
        dayItems = ItemSpecParser.parseItemSpec("鬼扯蛋一行没用\r\n"
                + "2012-09-10 10:10~12:30开10 12:30~15:00 25\r\n"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);

        // 时间范围不按照顺序排列（自动调整）
        dayItems = ItemSpecParser.parseItemSpec("鬼扯蛋一行没用\r\n"
                + "2012-09-10 10:10~12:30开10 12:30~15:00 25 09:00~10:10 32\r\n"
                );
        for (DayItem dayItem : dayItems)
            System.out.println(dayItem);
    }
}
