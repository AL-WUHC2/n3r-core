package org.n3r.prizedraw;

import java.util.HashMap;
import java.util.Map;

import org.n3r.core.text.RRand;
import org.n3r.prizedraw.drawer.PrizeItem;

public class PrizeDrawDemo {
    public static void main(String[] args) {

        // 进行奥运抽奖
        PrizeDraw prizeDraw = new PrizeDraw("Olympic");
        for (int i = 0; i < 30; ++i) {
            Map<String, String> user = new HashMap<String, String>();
            user.put("createDate", "20120602");
            user.put("userId", RRand.randLetters(10));
            PrizeItem prizeItemId = prizeDraw.draw(user);
            System.out.println("抽奖返回:" + prizeItemId);
        }
    }
}
