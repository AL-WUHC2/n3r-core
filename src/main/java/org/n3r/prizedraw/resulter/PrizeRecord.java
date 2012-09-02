package org.n3r.prizedraw.resulter;

public class PrizeRecord {
    private String activityId;
    private String userId;
    private String robotTag;
    private int bingooTimes;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRobotTag() {
        return robotTag;
    }

    public void setRobotTag(String robotTag) {
        this.robotTag = robotTag;
    }

    public int getBingooTimes() {
        return bingooTimes;
    }

    public void setBingooTimes(int bingooTimes) {
        this.bingooTimes = bingooTimes;
    }

}
