package org.n3r.biz.pagestatic.impl;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.n3r.biz.pagestatic.bean.RsyncRemote;
import org.n3r.biz.pagestatic.bean.RsyncDir;
import org.n3r.biz.pagestatic.util.PageStaticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单条rsync命令类。
 * @author Bingoo
 *
 */
public class PageRsyncCmd {
    private Logger log = LoggerFactory.getLogger(PageRsyncCmd.class);
    private RsyncRemote conf;
    private RsyncDir path;
    private ProcessBuilder processBuilder;
    private String commandLine;
    private Process process;
    private long startMillis;
    private boolean aliveFlag;

    public PageRsyncCmd(RsyncRemote conf, RsyncDir path) {
        this.conf = conf;
        this.path = path;

        String[] cmd = new String[] { "rsync", "-az", path.getLocalDir(), createRemotePath() };
        commandLine = StringUtils.join(cmd, ' ');
        processBuilder = new ProcessBuilder(cmd);
    }

    private String createRemotePath() {
        return new StringBuilder()
            .append(conf.getDestUser())
            .append('@')
            .append(conf.getDestHost())
            .append(':')
            .append(path.getRemoteDir())
            .toString();
    }

    public void execute() {
        try {
           log.info("start command line {}", commandLine);
           process = processBuilder.start();
           PageStaticUtils.startStreamGlobber(process, commandLine);

           startMillis = System.currentTimeMillis();
           aliveFlag = true;
        } catch (IOException e) {
            log.error("start up rsync exception", e);
        }
    }

    /**
     * Destroy process if expired.
     * @param rysncTimeoutMilis
     * @return process is tagged as terminated just now.
     */
    public boolean destroyWhenExpired(long millisOfCheckRsyncExited, long rysncTimeoutMilis) {
        if (!aliveFlag) return false;

        log.info("check {} was terminated or not after {} second(s)", commandLine, millisOfCheckRsyncExited/1000.);
        if (PageStaticUtils.isAlive(process)) {
            long cost = System.currentTimeMillis() - startMillis;
            if (cost > rysncTimeoutMilis) {
                log.warn("{} runs {}s exipred, try to destroy it.", commandLine, cost/1000.);
                process.destroy();
            }

            return false;
        }

        aliveFlag = false;
        int exitValue = process.exitValue();
        log.info("{} check terminated with return code {}, cost {} seconds",
                new Object[]{commandLine, exitValue, (System.currentTimeMillis() - startMillis)/1000.});

        return true;
    }
}
