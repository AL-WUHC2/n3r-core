package org.n3r.prizedraw.drawer;

import org.n3r.core.lang.RBaseBean;
import org.n3r.esql.map.AfterProperitesSet;

public class PrizeItem extends RBaseBean implements AfterProperitesSet {
    private String activityId;
    private String itemId;
    private String itemName;
    private int itemTotal;
    private int itemOut;
    private int itemIn;
    private int itemRandbase;
    private int itemLucknum;
    private String itemSpec;
    private String itemMd5;

    private String dupSpec;

    @Override
    public void afterPropertiesSet() {

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

    public String getDupSpec() {
        return dupSpec;
    }

    public void setDupSpec(String dupSpec) {
        this.dupSpec = dupSpec;
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

}
