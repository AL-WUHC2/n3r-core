package org.n3r.core.security;

import java.security.MessageDigest;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.n3r.core.lang.RBase64;
import org.n3r.core.lang.RByte;

import com.google.common.base.Throwables;

public class RDigest {
    /**
     * SHA-1 消息摘要（校验和）.
     * 固定输出28个字节长的字符串.
     * @param str
     * @return
     * @throws Exception
     */
    public static String sha1(String str) {
        return checksum(str, "SHA-1");
    }

    /**
     * MD5 消息摘要（校验和）.
     * 固定输出24个字节长的字符串.
     * @param str
     * @return
     * @throws Exception
     */
    public static String md5(String str) {
        return checksum(str, "MD5");
    }

    /**
     * 消息摘要.
     * @param str
     * @param algorithm 算法
     * @return
     * @throws Exception
     */
    public static String checksum(String str, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(RByte.toBytes(str));
            return RBase64.encode(digest);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * MAC算法可选以下多种算法
     *
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    public static final String KEY_MAC = "HmacMD5";

    /**
     * 初始化HMAC密钥
     *
     * @return
     * @throws Exception
     */
    public static String initHmacKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);

            SecretKey secretKey = keyGenerator.generateKey();
            return RBase64.encode(secretKey.getEncoded());
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * HMAC加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String hmac(String data, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(RBase64.decode(key), KEY_MAC);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);

            byte[] ret = mac.doFinal(RByte.toBytes(data));
            return RBase64.encode(ret);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
