package org.n3r.core.lang;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get local IP address for Linux / Windows.
 * @author Bingoo
 *
 */
public class RIP {
    private static Logger logger = LoggerFactory.getLogger(RIP.class);
    private static InetAddress inetAddress;
    private static String ip;
    private static String hostname;

    static {
        NetworkInterface ni = null;

        try {
            ni = NetworkInterface.getByName("bond0");
        }
        catch (SocketException e) {
            logger.warn("Get NetworkInterface bond0 fail", e);
        }

        if (null != ni) {
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ia = addresses.nextElement();
                if (ia instanceof Inet6Address) continue;

                inetAddress = ia;
                ip = StringUtils.left(ia.getHostAddress(), 20);

                break;
            }
        }
        else {
            try {
                inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            }
            catch (UnknownHostException e) {
                logger.warn("getHostAddress fail", e);
            }
        }

        hostname = StringUtils.left(inetAddress.getHostName(), 50);
    }

    public static String getIp() {
        return ip;
    }

    public static String getHostName() {
        return hostname;
    }

    public static InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Convert ip address to long.
     * @param str IP ADDRESS
     * @return long
     */
    public static long convert(String str) {
        return bytesToLong(getInetAddress(str).getAddress());
    }

    /**
     * Convert ip address to InetAddress.
     * @param str IP ADDRESS
     * @return InetAddress
     */
    public static InetAddress getInetAddress(String str) {
        try {
            return InetAddress.getByName(str);
        }
        catch (UnknownHostException e) {
            return null;
        }

    }

    /**
     * Returns the long version of an IP address given an InetAddress object.
     *
     * @param address the InetAddress.
     * @return the long form of the IP address.
     */
    public static long bytesToLong(byte[] address) {
        long ipnum = 0;
        for (int i = 0; i < 4; ++i) {
            long y = address[i];
            if (y < 0) {
                y += 256;
            }
            ipnum += y << (3 - i) * 8;
        }
        return ipnum;
    }

    /**
     * 是否是IP地址格式。
     * @param inputString 字符串。
     * @return true/false.
     */
    public static boolean isIpAddress(String inputString) {
        if (inputString.indexOf("..") >= 0) { return false; }

        StringTokenizer tokenizer = new StringTokenizer(inputString, ".");
        if (tokenizer.countTokens() != 4) { return false; }

        try {
            for (int i = 0; i < 4; i++) {
                String t = tokenizer.nextToken();
                int chunk = Integer.parseInt(t);
                if ((chunk & 255) != chunk) { return false; }
            }
        }
        catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
