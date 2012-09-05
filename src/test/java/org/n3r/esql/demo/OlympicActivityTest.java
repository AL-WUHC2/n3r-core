package org.n3r.esql.demo;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.n3r.esql.Esql;

import static org.junit.Assert.*;

public class OlympicActivityTest {
    @Test
    public void test10() {
        RandomAward randomAwared =new OlympicActivity().getRandomAwardType();
        assertNotNull(randomAwared);
    }

    @Test
    public void test11() {
        Map<String, String> randomAwared = new Esql().select("GetRandomAwardType")
                .execute();
        assertNotNull(randomAwared);
    }

    @Test
    public void test2() {
        int effectedRows = new Esql().update("DecreaseAwardRemain")
                .params("myAwardId")
                .execute();
        assertEquals(1, effectedRows);
    }

    @Test
    public void test3() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("StatisticDimens", "CustIp");
        params.put("CUST_IP", "127.0.0.1");

        int times = new Esql().select("StatisticAwardOrder")
                .params(params).execute();
        assertEquals(3, times);
    }

    /**
     * 这个类最好有个工具能生成。
     * @author Bingoo
     *
     */
    public static interface RandomAward {
        String getRandom();

        String getAwardId();

        String getAwardType();

        String getAwardProb();

        String getAwardLuckNum();

        String getTotalNum();

        String getRemainNum();

        boolean isDefaultAward();

        String getAwardDoodsDesc();

        String getAawardDoodsDode();
    }
}
