package org.n3r.core.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.n3r.core.lang.RStr;

import com.google.common.base.Throwables;

/**
 * 3DES 加密解密类.
 * @author Bingoo Huang
 *
 */
public class TDesCryptor extends BaseCryptor {
    // 加密用
    private Cipher encryptCipher;
    // 解密用
    private Cipher decryptCipher;

    /**
     * 默认构造函数.
     */
    public TDesCryptor() {
        super();
        initCipher();
    }

    /**
     * 带密钥的构造函数.
     * @param key 密钥
     */
    public TDesCryptor(String key) {
        super(key);
        initCipher();
    }

    /**
     * 取得Cipher对象.
     * @param isEncrypt 是否加密
     * @return Cipher对象
     */
    @Override
    protected Cipher getCipher(boolean isEncrypt) {
        return isEncrypt ? encryptCipher : decryptCipher;
    }

    private void initCipher() {
        byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 }; // 加解密向量
        IvParameterSpec paramSpec = new IvParameterSpec(iv);

        try {
            // convert key to byte array and get it into a key object
            final byte[] rawkey = RStr.alignRight(getKey(), 24, 'L').getBytes("UTF-8");
            DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key = keyfactory.generateSecret(keyspec);

            encryptCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            decryptCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }
        catch (Throwable e) {
            Throwables.propagate(e);
        }
    }

}
