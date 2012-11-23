package org.n3r.biz.pagestatic.bean;

import org.apache.commons.lang3.StringUtils;

/**
 * Rsync需要同步的目录配置项。
 * @author Bingoo
 *
 */
public class RsyncDir {
    // 本地目录
    private String localDir;
    // 远程主机
    // 可能为null，表示对应到所有RsyncConf中定义的相同远程主机上的remoteDir
    private String remoteHost;
    // 远程主机上的目录
    private String remoteDir;

    public RsyncDir(String localDir, String remoteDir) {
        this.localDir = localDir;

        this.remoteHost = StringUtils.substringBefore(remoteDir, ":");
        this.remoteDir = StringUtils.substringAfter(remoteDir, ":");
    }

    public String getLocalDir() {
        return localDir;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public String getRemoteHost() {
        return remoteHost;
    }
}
