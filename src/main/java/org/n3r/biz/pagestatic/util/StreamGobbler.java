package org.n3r.biz.pagestatic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamGobbler extends Thread {
    public static enum TYPE { STDOUT, STDERR }

    private Logger log = LoggerFactory.getLogger(StreamGobbler.class);

    final static String separator = System.getProperty("line.separator");
    private InputStream is;
    private TYPE type;
    private String commandLine;

    public StreamGobbler(String commandLine, InputStream is, TYPE type) {
        this.commandLine = commandLine;
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = null;
            while ((line = br.readLine()) != null)
                if (type == TYPE.STDOUT)
                    log.info("{} info {}", commandLine, line);
                else
                    log.warn("{} info {}", commandLine, line);
        }
        catch (IOException e) {
            log.error("{} ioexception {}", commandLine, e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(br);
        }
    }
}
