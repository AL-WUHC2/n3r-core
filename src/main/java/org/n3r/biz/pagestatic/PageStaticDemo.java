package org.n3r.biz.pagestatic;

import java.io.File;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;
import org.n3r.biz.pagestatic.util.PageStaticUtils;

/**
 * 在pom中放开maven-jar-plugin和maven-assembly-plugin两个plugin的注释
 * maven打包：mvn package -Dmaven.test.skip=true
 *
 * 需要连续运行24小时，看http连接释放情况，rsync调用情况
 *    http连接情况  /usr/sbin/lsof -p 16786 |grep TCP|wc -l
 * @author Bingoo
 *
 */
public class PageStaticDemo {

    public static void main(String[] args) {
        // 配置上传相关参数
        /*PageStatic pageStatic1 = */new PageStaticBuilder()
                // 必须有一个addRsyncRemote和一个addRsyncDir
                .addRsyncRemote("10.142.151.86", "mall")
                .addRsyncRemote("10.142.151.87", "mall")
                .addRsyncDir("/home/mall/pagestatic/pagehtml/", "10.142.151.86:/home/mall/pagestatic/")
                .addRsyncDir("/home/mall/pagestatic/pagehtml/", "10.142.151.87:/app/mallci/pagestatic/")
                 // 以下是可选参数
                .httpSocketTimeoutSeconds(60) // 不设置，默认30秒
                .triggerUploadWhenMaxFiles(100) // 不设置，默认100
                .triggerUploadWhenMaxSeconds(60) // 不设置，默认120
                .deleteLocalDirAfterRsync(true)  // 不设置，默认true
                .maxUrlContentGeneratingThreads(10) // 不设置，默认1
                .rsyncTimeoutSeconds(60) // 不设置，默认30秒
                .build();

        PageStatic pageStatic = new PageStaticBuilder().fromSpec("DEFAULT");

        SecureRandom random = new SecureRandom();
        File file = new File("stop");

        while(true) {
            if (file.exists()) {
                file.deleteOnExit();
                break;
            }

            staticAndUpload(pageStatic);

            PageStaticUtils.sleepSeconds(random.nextInt(3) + 1);
        }

        System.out.println("main thread exited!");
    }


    private static void staticAndUpload(PageStatic pageStatic) {
        // 开始上传
        pageStatic.startupBatch();

        // 批量上传
        for (int i = 0, ii = urls.length; i < ii; ++i) {
            String url = urls[i];
            String fileName = StringUtils.substringAfterLast(url, "/");
            String localFile = "/home/mall/pagestatic/pagehtml/p" + i % 2 + "/" + fileName;

            // 静态化指定url，以及对应本地文件名称
            pageStatic.staticAndUpload(url, localFile);
        }

        // 等待上传完成
        pageStatic.finishBatch();
    }



    private static String[] urls = {
        "http://10.142.151.86:8105/goodsdetail/341211200803.html",
        "http://10.142.151.86:8105/goodsdetail/341207022503.html",
        "http://10.142.151.86:8105/goodsdetail/341208074455.html",
        "http://10.142.151.86:8105/goodsdetail/341210259313.html",
        "http://10.142.151.86:8105/goodsdetail/341211210958.html",
        "http://10.142.151.86:8105/goodsdetail/341211200851.html",
        "http://10.142.151.86:8105/goodsdetail/341211190701.html",
        "http://10.142.151.86:8105/goodsdetail/341207022503.html",
        "http://10.142.151.86:8105/goodsdetail/341210259313.html",
        "http://10.142.151.86:8105/goodsdetail/341208074455.html",
        "http://10.142.151.86:8105/goodsdetail/341209106504.html",
        "http://10.142.151.86:8105/goodsdetail/341209106505.html",
        "http://10.142.151.86:8105/goodsdetail/341209076408.html",
        "http://10.142.151.86:8105/goodsdetail/341210228903.html",
        "http://10.142.151.86:8105/goodsdetail/341209187201.html",
        "http://10.142.151.86:8105/goodsdetail/341209187204.html",
        "http://10.142.151.86:8105/goodsdetail/341209187205.html",
        "http://10.142.151.86:8105/goodsdetail/341210098301.html",
        "http://10.142.151.86:8105/goodsdetail/341211190701.html",
        "http://10.142.151.86:8105/goodsdetail/341209298001.html",
        "http://10.142.151.86:8105/goodsdetail/341210299505.html",
        "http://10.142.151.86:8105/goodsdetail/341210299506.html",
        "http://10.142.151.86:8105/goodsdetail/341210299507.html",
        "http://10.142.151.86:8105/goodsdetail/341209257501.html",
        "http://10.142.151.86:8105/goodsdetail/341209257504.html",
        "http://10.142.151.86:8105/goodsdetail/341208305801.html",
        "http://10.142.151.86:8105/goodsdetail/341211150601.html"
    };
}
