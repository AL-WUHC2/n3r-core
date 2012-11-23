package org.n3r.biz.pagestatic.bean;

public class RsyncPath {

    private String srcRoot;
    private String destRoot;

    public RsyncPath(String srcRoot, String destRoot) {
        this.srcRoot = srcRoot;
        this.destRoot = destRoot;
    }

    public String getSrcRoot() {
        return srcRoot;
    }

    public String getDestRoot() {
        return destRoot;
    }

}
