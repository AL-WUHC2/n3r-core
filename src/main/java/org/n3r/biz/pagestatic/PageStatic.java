package org.n3r.biz.pagestatic;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.n3r.biz.pagestatic.base.PageService;
import org.n3r.biz.pagestatic.bean.Page;
import org.n3r.biz.pagestatic.impl.PageHttpClient;
import org.n3r.biz.pagestatic.impl.PageRsync;
import org.n3r.biz.pagestatic.impl.PageUploader;
import org.n3r.biz.pagestatic.util.PageStaticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Staicize a page content retrieved from an URL and uploaded by rsync.
 * @author Bingoo
 *
 */
public class PageStatic implements PageService{
    private Logger log = LoggerFactory.getLogger(PageStatic.class);

    private volatile BlockingQueue<Page> pageQueue;
    private volatile ExecutorService threadPool4GetUrlContent;
    private volatile PageHttpClient pageHttpClient;

    private volatile String batchId;

    private volatile PageRsync pageRsync;
    private volatile PageUploader pageUploader;

    private PageStaticBuilder pageStaticBuilder;

    private long startupMillis;

    PageStatic(PageStaticBuilder pageStaticBuilder) {
        this.pageStaticBuilder = pageStaticBuilder;
    }

    public void directContentUpload(String content, String localFileName) {
        putQueue("direct://", content, localFileName);
    }

    public void urlStaticAndUpload(final String url, final String localFileName) {
        checkPageHttpClientCreated(url);
        threadPool4GetUrlContent.submit(new Runnable() {
            @Override
            public void run() {
                if (pageHttpClient.executeGetMethod(url))
                    putQueue(url, pageHttpClient.getContent(), localFileName);
            }
        });
    }

    public String startupBatch() {
        waitLastBatchFinish();

        createThreadPool4GetUrlContent();

        createPageQueue();

        createPageUploader();

        createPageRsync();

        startupUploader();

        return batchId;
    }

    private void createThreadPool4GetUrlContent() {
        threadPool4GetUrlContent =  Executors.newFixedThreadPool(pageStaticBuilder.getMaxGeneratingThreads());
    }

    private void startupUploader() {
        pageUploader.startUpload(pageRsync);

        batchId = UUID.randomUUID().toString();
        startupMillis = System.currentTimeMillis();

        log.info("{} page static started up!", batchId);
    }

    private void createPageRsync() {
        pageRsync = new PageRsync();
        pageRsync.setRsyncRemotes(pageStaticBuilder.getRsyncRemotes());
        pageRsync.setRsyncDirs(pageStaticBuilder.getRsyncDirs());
        pageRsync.setDeleteLocalDirAfterRsync(pageStaticBuilder.isDeleteLocalDirAfterRsync());
        pageRsync.setRsyncTimeoutSeconds(pageStaticBuilder.getRsyncTimeoutSeconds());
    }

    private void createPageUploader() {
        pageUploader = new PageUploader();
        pageUploader.setUploadTriggerMaxFiles(pageStaticBuilder.getUploadTriggerMaxFiles());
        pageUploader.setUploadTriggerMaxSeconds(pageStaticBuilder.getUploadTriggerMaxSeconds());
        pageUploader.setPageService(this);
        pageUploader.setPageQueue(pageQueue);
    }

    private BlockingQueue<Page> createPageQueue() {
        return pageQueue = new LinkedBlockingQueue<Page>();
    }

    private void checkPageHttpClientCreated(String url) {
        if (pageHttpClient != null ) return;

        pageHttpClient = new PageHttpClient();
        pageHttpClient.setHttpSocketTimeoutSeconds(pageStaticBuilder.getHttpSocketTimeoutSeconds());
    }

    private void waitLastBatchFinish() {
        while(batchId != null) PageStaticUtils.sleepMilis(100);
    }

    public String getBatchId() {
        return batchId;
    }

    private void putQueue(String url, String content, String file)  {
        try {
            pageQueue.put(new Page(url, content, file));
        } catch (InterruptedException e) {
            log.error("put queue catched InterruptedException {}", e);
        }
    }

    public void finishBatch() {
        threadPool4GetUrlContent.shutdown();
    }

    @Override
    public boolean isTerminated() {
        return threadPool4GetUrlContent.isTerminated();
    }

    @Override
    public void shutdown() {
        threadPool4GetUrlContent = null;

        if (pageHttpClient != null) {
            pageHttpClient.shutdown();
            pageHttpClient = null;
        }

        pageUploader = null;
        pageQueue = null;
        pageRsync = null;

        log.info("{} page static shutted down!, cost {} seconds", batchId,
                (System.currentTimeMillis() - startupMillis)/1000.);
        batchId = null;
    }


}
