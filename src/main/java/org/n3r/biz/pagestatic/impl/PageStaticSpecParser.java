package org.n3r.biz.pagestatic.impl;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.n3r.biz.pagestatic.PageStaticBuilder;
import org.n3r.config.Config;
import org.n3r.config.Configable;

/**
 * 页面静态化上传框架配置文件解析类。
 * @author Bingoo
 *
 */
public class PageStaticSpecParser {
    private String specName;

    public PageStaticSpecParser(String specName) {
        this.specName = specName;
    }

    public void parse(PageStaticBuilder pageStaticBuilder) {
        Configable config = Config.subset("PageStaticBuilder." + specName);

        parseRsyncRemote(pageStaticBuilder, config);
        parseRsyncDir(pageStaticBuilder, config);

        // 以下是可选参数
        String key  = "httpSocketTimeoutSeconds";
        if (config.exists(key)) {
            pageStaticBuilder.httpSocketTimeoutSeconds(config.getInt(key));
        }

        key = "triggerUploadWhenMaxFiles";
        if (config.exists(key)) {
            pageStaticBuilder.triggerUploadWhenMaxFiles(config.getInt(key));
        }

        key = "triggerUploadWhenMaxSeconds";
        if (config.exists(key)) {
            pageStaticBuilder.triggerUploadWhenMaxSeconds(config.getInt(key));
        }

        key = "deleteLocalDirAfterRsync";
        if (config.exists(key)) {
            pageStaticBuilder.deleteLocalDirAfterRsync(config.getBool(key));
        }

        key =  "maxUrlContentGeneratingThreads";
        if (config.exists(key)) {
            pageStaticBuilder.maxUrlContentGeneratingThreads(config.getInt(key));
        }

        key = "rsyncTimeoutSeconds";
        if (config.exists(key)) {
            pageStaticBuilder.rsyncTimeoutSeconds(config.getInt(key));
        }
    }

    private void parseRsyncRemote(PageStaticBuilder pageStaticBuilder, Configable config) {
        String key = "addRsyncRemote";
        Properties remotes = config.subset(key).getProperties();
        for(Object value: remotes.values()) {
            String rsyncRemote = (String)value;
            addRsyncRemote(pageStaticBuilder, rsyncRemote);
        }

        String rsyncRemote = config.getStr(key);
        if (StringUtils.isNotEmpty(rsyncRemote))
            addRsyncRemote(pageStaticBuilder, rsyncRemote);
    }

    private void parseRsyncDir(PageStaticBuilder pageStaticBuilder, Configable config) {
        String key = "addRsyncDir";
        Properties remotes = config.subset(key).getProperties();
        for(Object value: remotes.values()) {
            String rsyncDir = (String)value;
            addRsyncDir(pageStaticBuilder, rsyncDir);
        }

        String rsyncDir = config.getStr(key);
        if (StringUtils.isNotEmpty(rsyncDir))
            addRsyncDir(pageStaticBuilder, rsyncDir);
    }

    private void addRsyncRemote(PageStaticBuilder pageStaticBuilder, String rsyncRemote) {
        if (StringUtils.isEmpty(rsyncRemote)) return;

        String remoteHost = StringUtils.trim(StringUtils.substringBefore(rsyncRemote, ","));
        String remoteUser = StringUtils.trim(StringUtils.substringAfter(rsyncRemote, ","));
        pageStaticBuilder.addRsyncRemote(remoteHost, remoteUser);
    }

    private void addRsyncDir(PageStaticBuilder pageStaticBuilder, String rsyncDir) {
        if (StringUtils.isEmpty(rsyncDir)) return;

        String localDir = StringUtils.trim(StringUtils.substringBefore(rsyncDir, ","));
        String remoteDir = StringUtils.trim(StringUtils.substringAfter(rsyncDir, ","));
        pageStaticBuilder.addRsyncDir(localDir, remoteDir);
    }
}
