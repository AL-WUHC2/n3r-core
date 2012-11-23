package org.n3r.biz.pagestatic.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.n3r.biz.pagestatic.bean.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageStaticUtils {
    private static Logger log = LoggerFactory.getLogger(PageStaticUtils.class);

    public static boolean createFile(Page page) {
        File pageFile = new File(page.getLocalFileName());
        File parentPath = pageFile.getParentFile();
        if (!parentPath.exists() && !parentPath.mkdirs()) {
            log.error("mkdir fail {}", parentPath);
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pageFile);
            IOUtils.write(page.getResponseBody(), fos);
            IOUtils.write(createTimestamp(page), fos);
            log.info("file {} was created from url {}", page.getLocalFileName(), page.getUrl());
        } catch (IOException ex) {
            log.error("write content of {} to file {} failed {}",
                    new Object[] { page.getUrl(), page.getLocalFileName(), ex });
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }

        return true;
    }

    public static final String DATEFMT = "yyyy-MM-dd HH:mm:ss";

    private static String createTimestamp(Page page) {
        return new StringBuilder()
                .append("<!-- staticized at ")
                .append(new SimpleDateFormat(DATEFMT).format(new Date()))
                .append(" by PageStatic program -->")
                .toString();
    }

    public static boolean deleteDirRecursively(File path) {
        if (path.exists() && path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory())
                    deleteDirRecursively(file);
                else
                    file.delete();
            }
        }

        return (path.delete());
    }

    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public static void sleepSeconds(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
    }

    public static void sleepMilis(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
        }
    }

    public static void startStreamGlobber(Process p, String commandLine) {
        new StreamGobbler(commandLine, p.getInputStream(), StreamGobbler.TYPE.STDOUT).start();
        new StreamGobbler(commandLine, p.getErrorStream(), StreamGobbler.TYPE.STDERR).start();
    }

    private static final String os = System.getProperty("os.name").toLowerCase();
    public static boolean isWindowsOS() {
        return os.indexOf("windows") != -1 || os.indexOf("nt") != -1;
    }
}
