package org.n3r.biz.pagestatic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.biz.pagestatic.bean.RsyncRemote;
import org.n3r.biz.pagestatic.bean.RsyncDir;
import org.n3r.biz.pagestatic.util.PageStaticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 页面内容上传管理类。
 * @author Bingoo
 *
 */
public class PageRsync {
    private Logger log = LoggerFactory.getLogger(PageRsync.class);

    private List<RsyncRemote> rsyncRemotes;
    private List<RsyncDir> rsyncDirs;
    private boolean deleteLocalDirAfterRsync;
    private long millisOfCheckRsyncExited = 1000;
    private long rsyncTimeoutMilis;

    private List<String> localDirs;
    private ArrayList<PageRsyncCmd> rsyncCmds;

    public void initialize() {
        buildRsyncCmds(); // 构建Rsync上传命令
        buildUniqueLocalDirs(); // 构建本地目录（排重）
    }

    private void buildUniqueLocalDirs() {
        if (!deleteLocalDirAfterRsync) return;

        localDirs = new ArrayList<String>(rsyncDirs.size());
        for(RsyncDir dir: rsyncDirs)
            if (!localDirs.contains(dir.getLocalDir()))
                localDirs.add(dir.getLocalDir());
    }

    private void buildRsyncCmds() {
        rsyncCmds = new ArrayList<PageRsyncCmd>();
        for(RsyncRemote remote: rsyncRemotes) {
            for(RsyncDir dir: rsyncDirs)
                addRsyncCmd(remote, dir);
        }
        rsyncCmds.trimToSize();
    }

    private void addRsyncCmd(RsyncRemote conf, RsyncDir path) {
        if (StringUtils.isEmpty(path.getRemoteHost())
                || StringUtils.equals(path.getRemoteHost(), conf.getDestHost()))
            rsyncCmds.add(new PageRsyncCmd(conf, path));
    }

    public void rsync() {
        startAndWaitRsync();
        deleteLocalDirsAsRequired();
    }

    private void startAndWaitRsync() {
        log.info("rsync began");
        if (!PageStaticUtils.isWindowsOS()) {
            startExecuteAllRsyncCmds();
            waitAndCheckTerminate();
        }

        log.info("rsync finished");
    }

    private void waitAndCheckTerminate() {
        int totalAlives = rsyncCmds.size();
        while (totalAlives > 0) {
            PageStaticUtils.sleepMilis(millisOfCheckRsyncExited);

            for (PageRsyncCmd cmd : rsyncCmds)
                if (cmd.destroyWhenExpired(millisOfCheckRsyncExited, rsyncTimeoutMilis))
                    --totalAlives;
        }
    }

    private void startExecuteAllRsyncCmds() {
        for(PageRsyncCmd cmd: rsyncCmds)
            cmd.execute();
    }

    private void deleteLocalDirsAsRequired() {
        if (!deleteLocalDirAfterRsync) return;

        log.info("delete src path begin");
        for(String dir: localDirs) {
            log.info("deleting {}", dir);
            PageStaticUtils.deleteDirRecursively(new File(dir));
        }

        log.info("delete src path finish");
    }

    public void setRsyncTimeoutSeconds(int rsyncTimeoutSeconds) {
        rsyncTimeoutMilis = rsyncTimeoutSeconds * 1000;
    }

    public void setRsyncRemotes(List<RsyncRemote> rsyncRemotes) {
        this.rsyncRemotes = rsyncRemotes;
    }

    public void setRsyncDirs(List<RsyncDir> rsyncDirs) {
        this.rsyncDirs = rsyncDirs;
    }

    public void setDeleteLocalDirAfterRsync(boolean deleteLocalDirAfterRsync) {
        this.deleteLocalDirAfterRsync = deleteLocalDirAfterRsync;
    }
}
