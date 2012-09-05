package org.n3r.prizedraw.drawer;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.n3r.core.lang.RDate;

public class IntelligentPrizeItemDrawerTest {

    @Test
    public void test() {
        IntelligentPrizeItemDrawer drawer = new IntelligentPrizeItemDrawer();
        PrizeItem prizeItem = new PrizeItem();
        prizeItem.setActivityId("testaid");
        prizeItem.setItemId("1");
        prizeItem.setItemIn(100);
        Date eff = RDate.parse("2012-08-01");
        Date exp = RDate.parse("2012-09-10");
        List<PrizeItemAssign> assigns = drawer.createAssigns(prizeItem, eff, exp);
        for (PrizeItemAssign assign : assigns) {
            System.out.print(RDate.toDateTimeStr(assign.getAssignTimeFrom()));
            System.out.print(" ");
            System.out.print(RDate.toDateTimeStr(assign.getAssignTimeTo()));
            System.out.print(" ");
            System.out.print(RDate.toDateTimeStr(assign.getLuckTime()));
            System.out.print(" ");
            System.out.print(assign.getAssignNum());

            System.out.println();
        }

    }

}
