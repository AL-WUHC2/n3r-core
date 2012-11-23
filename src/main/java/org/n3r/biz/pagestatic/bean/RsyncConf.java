package org.n3r.biz.pagestatic.bean;

public class RsyncConf {
    private String destIp;
    private String destUser;
    private String destPass;

    public RsyncConf(String destIp, String destUser, String destPass) {
        this.destIp = destIp;
        this.destUser = destUser;
        this.destPass = destPass;
    }

    public String getDestIp() {
        return destIp;
    }

    public String getDestUser() {
        return destUser;
    }

    public String getDestPass() {
        return destPass;
    }

}
