/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import javax.servlet.http.Cookie;

import net.sourceforge.jwebunit.plugins.httpunit.HttpUnitDialog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Establish context for tests (things such as locale, base url for the
 * application, cookies, authorization). The context can be accessed through
 * the {@link net.sourceforge.jwebunit.WebTestCase}or
 * {@link net.sourceforge.jwebunit.WebTester}.
 * 
 * @author Wilkes Joiner
 * @author Jim Weaver
 */
public class TestContext {
	private String user;
	private String passwd;
	private List cookies;
	private boolean hasAuth;
	private Locale locale = Locale.getDefault();
	private String encodingScheme = "ISO-8859-1";
	private String resourceBundleName;
	private String baseUrl = "http://localhost:8080";
	private String userAgent;
	private String proxyName;
	private int proxyPort = 80;

	/**
	 * Construct a test client context.
	 */
	public TestContext() {
		cookies = new ArrayList();
	}

	/**
	 * Set authentication information for the test context. This information is
	 * used by {@link HttpUnitDialog}to set authorization on the
	 * WebConversation when the dialog is begun.
	 * 
	 * @param user
	 *            user name
	 * @param passwd
	 *            password
	 */
	public void setAuthorization(String user, String passwd) {
		this.user = user;
		this.passwd = passwd;
		hasAuth = true;
	}

	/**
	 * Add a cookie to the test context. These cookies are set on the
	 * WebConversation when an {@link HttpUnitDialog}is begun.
	 * 
	 * @param name
	 *            cookie name.
	 * @param value
	 *            cookie value.
	 */
	public void addCookie(String name, String value) {
		cookies.add(new Cookie(name, value));
	}

	/**
	 * Return true if a user / password has been set on the context via
	 * {@link #setAuthorization}.
	 */
	public boolean hasAuthorization() {
		return hasAuth;
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
	 * Return the locale established for the test context. If the locale has
	 * not been explicitly set, Locale.getDefault() will be returned.
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
	 * Return the encoding scheme for the test context. The default encoding
	 * scheme is ISO-8859-1.
	 */
	public String getEncodingScheme() {
		return encodingScheme;
	}

	/**
	 * Set the encoding scheme for the test context which is applied to
	 * response text.
	 */
	public void setEncodingScheme(String encodingScheme) {
		this.encodingScheme = encodingScheme;
	}

	/**
	 * Return the value of a String in the encoding specified by the test
	 * context.
	 * 
	 * @param text
	 *            input text.
	 * @return String representing bytes of text converted by context's
	 *         encoding scheme.
	 */
	public String toEncodedString(String text) {
		try {
			return new String(text.getBytes(), encodingScheme);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return text;
		}
	}

	/**
	 * Set a resource bundle to use for the test context (will be used to
	 * lookup expected values by key in WebTester).
	 * 
	 * @param name
	 *            path name of the resource bundle.
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
	 * Return the proxy server name Contributed by Jack Chen
	 */
	public String getProxyName() {
		return proxyName;
	}

	/**
	 * Set the proxy server name for the test context. Contributed by Jack Chen
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	/**
	 * Return the proxy server port Contributed by Jack Chen
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * Set the proxy server port for the test context. Contributed by Jack Chen
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * Return true if a proxy name is set {@link #setProxyName}. Contributed
	 * by Jack Chen
	 */
	public boolean hasProxy() {
		return proxyName != null && proxyName.trim().length() > 0;
	}

	/**
	 * Return the base URL for the test context. The default base URL is port
	 * 8080 on localhost.
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Set the base url for the test context.
	 * 
	 * @param url
	 *            Base url value - A trailing "/" is appended if not provided.
	 */
	public void setBaseUrl(String url) {
		baseUrl = url.endsWith("/") ? url : url + "/";
	}

}
