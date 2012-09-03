package org.n3r.prizedraw.drawer;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RClose;
import org.n3r.core.security.RDigest;
import org.n3r.core.text.RRand;
import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTransaction;
import org.n3r.prizedraw.base.PrizeDrawer;
import org.n3r.prizedraw.impl.PrizeActivity;

public class ComplexPrizeItemDrawer implements PrizeDrawer {
    private PrizeActivity prizeActivity;

    @Override
    public PrizeItem drawPrize(PrizeItem lastDrawResult, PrizeActivity prizeActivity, Object userInfo) {
        if (lastDrawResult == null) return null;
        this.prizeActivity = prizeActivity;

        // 使用随机数判断是否通过幸运抽选关
        int rand = RRand.randInt(lastDrawResult.getItemRandbase());
        boolean hasLucky = rand == lastDrawResult.getItemLucknum() % lastDrawResult.getItemRandbase();
        if (!hasLucky) return null;
        String md5 = RDigest.md5(lastDrawResult.getItemSpec());

        tryParseItemSpec(lastDrawResult, md5);

        return null;
    }

    private void tryParseItemSpec(PrizeItem lastDrawResult, String md5) {
        Esql esql = new Esql();
        EsqlTransaction tran = esql.newTransaction();
        try {
            tran.start();
            boolean shouldUpdatePrizeItemAssign = false;
            if (!StringUtils.equals(lastDrawResult.getItemMd5(), md5)) {
                lastDrawResult.setItemMd5(md5);
                int effectedRows = new Esql().update("updatePrizeItemMd5").params(lastDrawResult).execute();
                shouldUpdatePrizeItemAssign = effectedRows == 1;
            }
            if (shouldUpdatePrizeItemAssign) {
                List<PrizeItemAssign> assigns = createAssigns(lastDrawResult);
                writeAssignsToDb(esql, assigns);
            }
            tran.commit();
        }
        finally {
            RClose.closeQuietly(tran);
        }
    }

    private void writeAssignsToDb(Esql esql, List<PrizeItemAssign> assigns) {

    }

    private List<PrizeItemAssign> createAssigns(PrizeItem lastDrawResult) {
        String itemSpec = lastDrawResult.getItemSpec();
        if (StringUtils.isEmpty(itemSpec)) prizeActivity.getActivityEff();
        return null;
    }
}