package org.n3r.prizedraw.drawer;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RBaseBean;
import org.n3r.esql.map.AfterProperitesSet;
import org.n3r.prizedraw.base.PrizeDrawItemChecker;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class PrizeItem extends RBaseBean implements AfterProperitesSet {
    private String activityId;
    private String itemId;
    private String itemName;
    private boolean itemJoin;
    private int itemTotal;
    private int itemOut;
    private int itemIn;
    private int itemRandbase;
    private int itemLucknum;
    private String checkers;
    private String itemSpec;
    private String itemMd5;
    private Date itemCheckpoints;
    private List<PrizeDrawItemChecker> itemCheckers = Lists.newArrayList();

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isNotEmpty(checkers)) {
            Splitter splitter = Splitter.onPattern("[,\\s]").omitEmptyStrings().trimResults();
            for (String functorStr : splitter.split(checkers)) {
                PrizeDrawItemChecker functor = Reflect.on(functorStr).create().get();
                itemCheckers.add(functor);
            }
        }
    }

    public int getItemOut() {
        return itemOut;
    }

    public void setItemOut(int itemOut) {
        this.itemOut = itemOut;
    }

    public int getItemIn() {
        return itemIn;
    }

    public void setItemIn(int itemIn) {
        this.itemIn = itemIn;
    }

    public String getItemMd5() {
        return itemMd5;
    }

    public void setItemMd5(String itemMd5) {
        this.itemMd5 = itemMd5;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(int itemTotal) {
        this.itemTotal = itemTotal;
    }

    public String getItemSpec() {
        return itemSpec;
    }

    public void setItemSpec(String itemSpec) {
        this.itemSpec = itemSpec;
    }

    public int getItemRandbase() {
        return itemRandbase;
    }

    public void setItemRandbase(int itemRandbase) {
        this.itemRandbase = itemRandbase;
    }

    public int getItemLucknum() {
        return itemLucknum;
    }

    public void setItemLucknum(int itemLucknum) {
        this.itemLucknum = itemLucknum;
    }

    public boolean isItemJoin() {
        return itemJoin;
    }

    public void setItemJoin(boolean itemJoin) {
        this.itemJoin = itemJoin;
    }

    public String getCheckers() {
        return checkers;
    }

    public void setCheckers(String checkers) {
        this.checkers = checkers;
    }

    public List<PrizeDrawItemChecker> getItemCheckers() {
        return itemCheckers;
    }

    public Date getItemCheckpoints() {
        return itemCheckpoints;
    }

    public void setItemCheckpoints(Date itemCheckpoints) {
        this.itemCheckpoints = itemCheckpoints;
    }

}
