package org.n3r.biz.pagestatic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordStreamGobbler extends Thread {
    private Logger log = LoggerFactory.getLogger(StreamGobbler.class);

    final static String separator = System.getProperty("line.separator");
    private InputStream is;
    private String commandLine;
    private String password;

    private OutputStream stdIn;

    public PasswordStreamGobbler(String commandLine, InputStream is, OutputStream stdIn, String password) {
        this.commandLine = commandLine;
        this.is = is;
        this.stdIn = stdIn;
        this.password = password + "\n";
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            StringBuilder buffer = new StringBuilder();

            String passwordTips = detectPaswordRequired(br);
            if (passwordTips != null) {
                stdIn.write(password.getBytes());
                stdIn.flush();
            }

            buffer.append(passwordTips);
            readOutput(br, buffer);

            String msg = buffer.toString();
            if (msg.trim().length() > 0)
                log.info("{} info {}", commandLine, msg);
        }
        catch (IOException e) {
            log.error("{} ioexception {}", commandLine, e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(stdIn);
        }
    }

    private String detectPaswordRequired(BufferedReader br) throws IOException {
        // Choose it big enough for rsync password request and all that goes before it
        char[] passRequest = new char[128];

        int len = 0;
        while (true) {
            int n = br.read (passRequest, len, passRequest.length - len);
            if (n == -1 ) break;

            len += n;
            String passwordTips = new String (passRequest, 0, len);
            if (passwordTips.contains ("password:"))  return passwordTips;
        }

        return null;
    }

    private void readOutput(BufferedReader br, StringBuilder buffer) throws IOException {
        char[] passRequest = new char[128];
        while (true) {
            int n = br.read (passRequest, 0, passRequest.length);
            if (n == -1 ) break;

            buffer.append(new String (passRequest, 0, n));
        }
    }
}
