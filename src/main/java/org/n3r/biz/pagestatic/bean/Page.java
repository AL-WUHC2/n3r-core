package org.n3r.biz.pagestatic.bean;

/**
 * 一个需要静态化并上传的页面.
 * @author Bingoo
 *
 */
public class Page {
    // 页面所在的URL
    private String url;
    // 使用HttpClient访问页面URL返回的响应体
    private String responseBody;
    // 响应体所需要存储的本地文件名称
    private String localFileName;

    public Page(String url, String responseBody, String localFileName) {
        this.url = url;
        this.responseBody = responseBody;
        this.localFileName = localFileName;
    }

    public String getUrl() {
        return url;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getLocalFileName() {
        return localFileName;
    }

}
