package org.n3r.biz.pagestatic.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据页面URL，抓取页面内容。
 * @author Bingoo
 *
 */
public class PageHttpClient {
    private Logger log = LoggerFactory.getLogger(PageHttpClient.class);

    // 多线程HTTP连接管理器
    private MultiThreadedHttpConnectionManager connectionManager;
    private HttpClient httpClient;
    private String content;
    private int httpSocketTimeoutSeconds;

    public PageHttpClient() {
        HttpClientParams params = new HttpClientParams();
        params.setParameter(HttpMethodParams.SO_TIMEOUT, httpSocketTimeoutSeconds * 1000);

        connectionManager = new MultiThreadedHttpConnectionManager();
        httpClient = new HttpClient(params, connectionManager);
    }

    public void shutdown() {
        connectionManager.shutdown();
    }

    public boolean executeGetMethod(String url) {
        GetMethod getMethod = new GetMethod(url);

        try {
            long startMillis = System.currentTimeMillis();
            log.info("content get begin {} ", url);
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                log.error("{} returned {}", url, statusCode);
                return false;
            }

            content = IOUtils.toString(getMethod.getResponseBodyAsStream(), "UTF-8");
            double costs = (System.currentTimeMillis() - startMillis) / 1000.;
            log.info("content get successful {}, costs {} seconds", url, costs);
            return true;
        } catch (HttpException e) {
            log.error("{} HttpException {}", url, e.getMessage());
        } catch (IOException e) {
            log.error("{} IOException {}", url, e.getMessage());
        } catch (Exception e) {
            log.error("{} unkown exception {}", url, e);
        } finally {
            getMethod.releaseConnection();
        }
        return false;
    }

    public String getContent() {
        return content;
    }

    public void setHttpSocketTimeoutSeconds(int httpSocketTimeoutSeconds) {
        this.httpSocketTimeoutSeconds = httpSocketTimeoutSeconds;
    }
}
