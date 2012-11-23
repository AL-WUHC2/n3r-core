package org.n3r.biz.pagestatic.util;

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

public class HttpClientUtil {
    private Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
    private MultiThreadedHttpConnectionManager connectionManager;
    private HttpClient httpClient;
    private String content;
    private int httpSocketTimeoutSeconds;

    public HttpClientUtil() {
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
            log.info("content get begin {} ", url);
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                log.error("{} returned {}", url, statusCode);
                return false;
            }

            content = IOUtils.toString(getMethod.getResponseBodyAsStream(), "UTF-8");
            log.info("content get successful {}", url);
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
