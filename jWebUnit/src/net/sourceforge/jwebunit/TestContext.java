/*
 * User: DJoiner
 * Date: Aug 16, 2002
 * Time: 8:42:33 AM
 */
package net.sourceforge.jwebunit;

import javax.servlet.http.Cookie;
import java.util.*;
import java.io.UnsupportedEncodingException;

public class TestContext {
    private String user;
    private String passwd;
    private List cookies;
    private boolean hasAuth;
    private Locale locale = Locale.getDefault();
    private String encodingScheme = "ISO-8859-1";
    private String resourceBundleName;
    private String baseUrl = "http://localhost:8080";

    public TestContext() {
        cookies = new ArrayList();
    }


    public void setAuthorization(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
        hasAuth = true;
    }

    public void addCookie(String name, String value) {
        cookies.add(new Cookie(name, value));
    }

    public boolean hasAuthorization() {
        return hasAuth;
    }

    public boolean hasCookies() {
        return cookies.size() > 0;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return passwd;
    }

    public List getCookies() {
        return cookies;
    }

    public Locale getLocale() {
        if (locale == null) return Locale.US;
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getEncodingScheme() {
        return encodingScheme;
    }

    public void setEncodingScheme(String encodingScheme) {
        this.encodingScheme = encodingScheme;
    }

    /**
     * Return the value of a String in the encoding specified by the test context.
     * @param text input text.
     * @return String representing bytes of text converted by context's encoding scheme.
     */
    public String toEncodedString(String text) {
        try {
            return new String(text.getBytes(), encodingScheme);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
    }

    public void setResourceBundleName(String name) {
        resourceBundleName = name;
    }

    public String getResourceBundleName() {
        return resourceBundleName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String url) {
        baseUrl = url.endsWith("/") ? url : url + "/";
    }
}
