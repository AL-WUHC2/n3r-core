package org.n3r.prizedraw.checker;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RBean;
import org.n3r.esql.Esql;
import org.n3r.prizedraw.base.PrizeDrawChecker;
import org.n3r.prizedraw.base.PropertyValueComparator;
import org.n3r.prizedraw.impl.PrizeActivity;
import org.n3r.prizedraw.impl.PrizeDrawCheckException;

public class DefaultPrizeDrawChecker implements PrizeDrawChecker {

    public void check(PrizeActivity prizeActivity, Object userInfo) {
        List<DefaultPrizeCheck> checks = new Esql().select("getPrizeDrawChecker")
                .params(prizeActivity.getActivityId()).execute();
        for (DefaultPrizeCheck defaultPrizeCheck : checks) {
            String property = RBean.getProperty(userInfo, defaultPrizeCheck.getCheckProperty());
            if (property == null && !defaultPrizeCheck.isNotexistsTag()) throw new PrizeDrawCheckException(
                    defaultPrizeCheck.getCheckProperty() + "的值是null");

            PropertyValueComparator comparator = defaultPrizeCheck.getPropertyValueComparator();
            int compareRet = comparator.compare(property, defaultPrizeCheck.getCheckValue());
            if (!passCond(defaultPrizeCheck.getCheckCond(), compareRet)) throw new PrizeDrawCheckException(
                    defaultPrizeCheck.getCheckProperty() + "的值校验通不过");
        }
    }

    private boolean passCond(String cond, int ret) {
        return StringUtils.isBlank(cond) && ret == 0
                || "=".equals(cond) && ret == 0
                || "==".equals(cond) && ret == 0
                || ">=".equals(cond) && ret >= 0
                || ">".equals(cond) && ret > 0
                || "<=".equals(cond) && ret <= 0
                || "<".equals(cond) && ret < 0
                || "<>".equals(cond) && ret != 0
                || "!=".equals(cond) && ret != 0;
    }
}
