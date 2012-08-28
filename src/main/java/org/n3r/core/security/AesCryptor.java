package org.n3r.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.n3r.core.lang.RStr;

import com.google.common.base.Throwables;

/**
 * Advanced Encryption Standard.
 * @author Bingoo Huang
 *
 */
public class AesCryptor extends BaseCryptor {

    // 加密用
    private Cipher encryptCipher;
    // 解密用
    private Cipher decryptCipher;

    /**
     * 默认构造函数.
     */
    public AesCryptor() {
        super();
        initCipher();
    }

    /**
     * 使用密钥构造.
     * @param key 密钥
     */
    public AesCryptor(String key) {
        super(key);

        initCipher();
    }

    private void initCipher() {
        try {
            final byte[] rawkey = RStr.alignRight(getKey(), 16, 'L').getBytes("UTF-8");
            SecretKeySpec key1 = new SecretKeySpec(rawkey, "AES");
            encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key1);
            decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key1);
        }
        catch (Throwable e) {
            Throwables.propagate(e);
        }
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
}
