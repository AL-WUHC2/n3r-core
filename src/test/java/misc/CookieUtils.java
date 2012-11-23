package misc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
    public static void setHttpOnlyCookie(HttpServletResponse response,
            String domain,
            String cookieName,
            String cookieValue, int maxAge) {

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);//随浏览器关闭;
        cookie.setDomain(domain);// domain
        cookie.setPath("/; HttpOnly");

        response.addCookie(cookie);
    }
}
