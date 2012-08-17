package org.n3r.core.text;

import java.security.SecureRandom;

import org.n3r.core.joou.ULong;
import org.n3r.core.lang3.RandomStringUtils;

public class RRand {
    /*
     * Thread-safe. It uses synchronization to protect the integrity of its state.
     * See SecureRandom.nextBytes with synchronized keyword.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean randBoolean() {
        return RANDOM.nextBoolean();
    }

    public static double randDouble() {
        return RANDOM.nextDouble();
    }

    public static float randFloat() {
        return RANDOM.nextFloat();
    }

    public static int randInt() {
        return RANDOM.nextInt();
    }
    
    public static int randInt(int n) {
        return RANDOM.nextInt(n);
    }

    public static long randLong() {
        return RANDOM.nextLong();
    }

    public static String randNum(int count) {
        StringBuilder sb = new StringBuilder(count);
        while (sb.length() < count) {
            sb.append(new ULong(randLong()));
        }

        return sb.replace(count, sb.length(), "").toString();
    }

    public static String randAscii(int count) {
        return RandomStringUtils.random(count, 32, 127, false, false, null, RANDOM);
    }

    public static String randLetters(int count) {
        return RandomStringUtils.random(count, 0, 0, true, false, null, RANDOM);
    }

    public static String randAlphanumeric(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, RANDOM);
    }

    private final static char[] CHINESE_LEVELONE = RChinese.levelOne().toCharArray();

    public static String randChinese(int count) {
        return RandomStringUtils.random(count, 0, 0, false, false, CHINESE_LEVELONE, RANDOM);
    }

    public static String rand(int count, String allowedChars) {
        return RandomStringUtils.random(count, 0, 0, false, false, allowedChars.toCharArray(), RANDOM);
    }

}
