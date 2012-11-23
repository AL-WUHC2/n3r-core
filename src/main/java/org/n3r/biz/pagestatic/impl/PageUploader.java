package org.n3r.biz.pagestatic.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.n3r.biz.pagestatic.base.PageService;
import org.n3r.biz.pagestatic.bean.Page;
import org.n3r.biz.pagestatic.util.PageStaticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 页面文件生成与上传类。
 * @author Bingoo
 *
 */
public class PageUploader {
    private Logger log = LoggerFactory.getLogger(PageUploader.class);

    private BlockingQueue<Page> pageQueue;
    private PageService pageService;
    private PageUploadTrigger uploadTrigger = new PageUploadTrigger();

    private PageRsync pageRsync;

    public void startUpload(PageRsync pageRsync) {
        this.pageRsync = pageRsync;
        pageRsync.initialize();

        new Thread(new Runnable() {
            @Override
            public void run() {
                 createFileAndRsyncUpload();
            }
        }).start();
    }

    private void createFileAndRsyncUpload() {
        uploadTrigger.reset();

        while (true) {
            checkUploadTriggered();

            if (pollQueueAndCreateFile()) uploadTrigger.incrFileCounting();
            else if (pageService.isTerminated()) break;
        }

        log.info("page uploader going to shutdown!");

        if(uploadTrigger.getFileCounting() > 0) pageRsync.rsync();
        pageService.shutdown();
        log.info("page uploader shutted down after processed {} files with {} seconds!",
                uploadTrigger.getTotalFileCounting(), uploadTrigger.getTotalCostSeconds());
    }

    private void checkUploadTriggered() {
        if (!uploadTrigger.reachTrigger()) return;

        pageRsync.rsync();
        uploadTrigger.reset();
    }

    private boolean pollQueueAndCreateFile()  {
        Page page = pollQueue();
        if (page == null) return false;

        return PageStaticUtils.createFile(page);
    }

    private Page pollQueue()  {
        try {
            return pageQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("poll queue catched InterruptedException {}", e);
            return null;
        }
    }

    public void setPageQueue(BlockingQueue<Page> pageQueue) {
        this.pageQueue = pageQueue;
    }

    public void setUploadTriggerMaxFiles(int uploadTriggerMaxFiles) {
        uploadTrigger.setUploadTriggerMaxFiles(uploadTriggerMaxFiles);
    }

    public void setUploadTriggerMaxSeconds(int uploadTriggerMaxSeconds) {
        uploadTrigger.setUploadTriggerMaxSeconds(uploadTriggerMaxSeconds);
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }
}
