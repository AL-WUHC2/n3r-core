package org.n3r.core.patchca;

import java.io.FileOutputStream;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.n3r.core.patchca.custom.RCaptchca;
import org.n3r.core.patchca.service.Captcha;

public class BatchDemo {
    public static void main(String[] args) throws Exception {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
                AddrUtil.getAddresses("192.168.32.130:11211"));
        MemcachedClient mcc = builder.build();

        for (int i = 0, ii = 100; i < ii; ++i) {
            Captcha captcha = RCaptchca.createCaptchca();

            String picurl = "batch" + i + ".png";
            FileOutputStream fos = new FileOutputStream(picurl);
            ImageIO.write(captcha.getImage(), "png", fos);
            fos.close();

            mcc.set("picurl." + i, 0, picurl);
            mcc.set("picword." + i, 0, captcha.getChallenge());

        }

        String picword = mcc.get("picword.51" );
        System.out.println(picword);
        mcc.shutdown();

        String encode = URLEncoder.encode(picword, "UTF-8");
        System.out.println(encode);

        encode = URLEncoder.encode(picword, "GBK");
        System.out.println(encode);
    }
}
