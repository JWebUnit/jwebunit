/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.util;

import javax.servlet.http.Cookie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Establish context for tests (things such as locale, base url for the application, cookies, authorization). The
 * context can be accessed through the {@link net.sourceforge.jwebunit.WebTestCase}or
 * {@link net.sourceforge.jwebunit.junit.WebTester}.
 * 
 * @author Wilkes Joiner
 * @author Jim Weaver
 * @author Julien Henry
 */
public class TestContext {
    private String user;

    private String passwd;

    private String domain;

    private List cookies;

    private boolean hasAuth = false;

    private boolean hasNTLMAuth = false;

    private Locale locale = Locale.getDefault();

    private String resourceBundleName;

    private URL baseUrl;

    private String userAgent;

    private Map requestHeaders = new HashMap();

    private String proxyUser = null;

    private String proxyPasswd = null;

    private String proxyHost = null;

    private int proxyPort = -1;

    private boolean hasProxyAuth = false;

    /**
     * Construct a test client context.
     */
    public TestContext() {
        cookies = new ArrayList();
        try {
            baseUrl = new URL("http://localhost:8080");
        } catch (MalformedURLException e) {
            // Should not be invalid
            e.printStackTrace();
        }
    }

    /**
     * Clear all authorizations (basic, digest, ntlm, proxy).
     * 
     */
    public void clearAuthorizations() {
        hasAuth = false;
        hasNTLMAuth = false;
        hasProxyAuth = false;
    }

    /**
     * Set basic authentication information for the test context.
     * 
     * @param user user name
     * @param passwd password
     */
    public void setAuthorization(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
        hasAuth = true;
    }

    /**
     * Set NTLM authentication information for the test context.
     * 
     * @param user user name
     * @param passwd password
     */
    public void setNTLMAuthorization(String user, String passwd, String domain) {
        this.user = user;
        this.passwd = passwd;
        this.domain = domain;
        hasNTLMAuth = true;
    }

    /**
     * Set proxy authentication information for the test context.
     * 
     * @param user user name (null if none)
     * @param passwd password (null if none)
     * @param host proxy host name (null if applicable to any host).
     * @param port proxy port (negative if applicable to any port).
     */
    public void setProxyAuthorization(String user, String passwd, String host,
            int port) {
        this.proxyUser = user;
        this.proxyPasswd = passwd;
        this.proxyHost = host;
        this.proxyPort = port;
        hasProxyAuth = !(null == user && null == passwd);
    }

    /**
     * Add a cookie to the test context. These cookies are set on the conversation when you use a {WebTester#beginAt}.
     * 
     * @param name cookie name.
     * @param value cookie value.
     * @param domain cookie domain (ie localhost or www.foo.bar).
     */
    public void addCookie(String name, String value, String domain) {
        Cookie c = new Cookie(name, value);
        c.setDomain(domain);
        cookies.add(c);
    }

    /**
     * Add a cookie to the test context. These cookies are set on the conversation when you use a {WebTester#beginAt}.
     * 
     * @param cookie a cookie.
     */
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    /**
     * Return true if a basic authentication has been set on the context via {@link #setAuthorization}.
     */
    public boolean hasAuthorization() {
        return hasAuth;
    }

    /**
     * Return true if a NTLM authentication has been set on the context via {@link #setNTLMAuthorization}.
     */
    public boolean hasNTLMAuthorization() {
        return hasNTLMAuth;
    }

    /**
     * Return true if a proxy authentication has been set on the context via {@link #setProxyAuthorization}.
     */
    public boolean hasProxyAuthorization() {
        return hasProxyAuth;
    }

    /**
     * Return true if one or more cookies have been added to the test context.
     */
    public boolean hasCookies() {
        return cookies.size() > 0;
    }

    /**
     * Return the authorized user for the test context.
     */
    public String getUser() {
        return user;
    }

    /**
     * Return the user password.
     */
    public String getPassword() {
        return passwd;
    }

    /**
     * Return the user domain.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Return the cookies which have been added to the test context.
     */
    public List getCookies() {
        return cookies;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean hasUserAgent() {
        return userAgent != null;
    }

    /**
     * Return the locale established for the test context. If the locale has not been explicitly set,
     * Locale.getDefault() will be returned.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the locale for the test context.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Set a resource bundle to use for the test context (will be used to lookup expected values by key in WebTester).
     * 
     * @param name path name of the resource bundle.
     */
    public void setResourceBundleName(String name) {
        resourceBundleName = name;
    }

    /**
     * Return the test context resource bundle for expected value lookups.
     */
    public String getResourceBundleName() {
        return resourceBundleName;
    }

    /**
     * Return the proxy server name
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Return the proxy server port
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Return the proxy user name
     */
    public String getProxyUser() {
        return proxyUser;
    }

    /**
     * Return the proxy password
     */
    public String getProxyPasswd() {
        return proxyPasswd;
    }

    /**
     * Return the base URL for the test context. The default base URL is port 8080 on localhost.
     */
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * Set the base url for the test context.
     * 
     * @param url Base url value - A trailing "/" is appended if not provided.
     */
    public void setBaseUrl(String url) {
        try {
            baseUrl = new URL(url.endsWith("/") ? url : url + "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the base url for the test context.
     * 
     * @param url Base url value. Anything after trailing "/" will be skipped.
     */
    public void setBaseUrl(URL url) {
        baseUrl = url;
    }

    /**
     * Add a custom request header.
     * @param name header name.
     * @param value header value.
     */
    public void addRequestHeader(final String name, final String value) {
        requestHeaders.put(name, value);
    }

    /**
     * Remove a custom request header.
     * @param name header name.
     */
    public void removeRequestHeader(final String name) {
        requestHeaders.remove(name);
    }

    /**
     * Get custom request headers.
     * @param name header name.
     * @param value header value.
     */
    public Map getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * Clear custom request headers.
     *
     */
    public void clearRequestHeaders() {
        requestHeaders = new HashMap();
    }

}
