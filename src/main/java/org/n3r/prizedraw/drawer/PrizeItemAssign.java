package org.n3r.prizedraw.drawer;

import java.util.Date;

import org.n3r.core.lang.RBaseBean;

public class PrizeItemAssign extends RBaseBean {
    private String activityId;
    private String itemId;
    private Date assignTimeFrom;
    private Date assignTimeTo;
    private Date luckTime;
    private int assignNum;
    private int availableNum;

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

    public Date getAssignTimeFrom() {
        return assignTimeFrom;
    }

    public void setAssignTimeFrom(Date assignTimeFrom) {
        this.assignTimeFrom = assignTimeFrom;
    }

    public Date getAssignTimeTo() {
        return assignTimeTo;
    }

    public void setAssignTimeTo(Date assignTimeTo) {
        this.assignTimeTo = assignTimeTo;
    }

    public Date getLuckTime() {
        return luckTime;
    }

    public void setLuckTime(Date luckTime) {
        this.luckTime = luckTime;
    }

    public int getAssignNum() {
        return assignNum;
    }

    public void setAssignNum(int assignNum) {
        this.assignNum = assignNum;
    }

    public int getAvailableNum() {
        return availableNum;
    }

    public void setAvailableNum(int availableNum) {
        this.availableNum = availableNum;
    }

}
