package com.meterware.httpunit;

/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2000-2001, Russell Gold
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 *******************************************************************************************************************/

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Vector;


/**
 * A resource to be returned from the simulated server.
 **/
public class WebResource {


    final static String DEFAULT_CONTENT_TYPE = "text/html";

    final static String DEFAULT_CHARACTER_SET = "iso-8859-1";

    public WebResource(String contents) {
        this(contents, DEFAULT_CONTENT_TYPE);
    }


    public WebResource(String contents, String contentType) {
        this(contents, contentType, HttpURLConnection.HTTP_OK);
    }


    public WebResource(int responseCode, String contents) {
        this(contents, DEFAULT_CONTENT_TYPE, responseCode);
    }


    public WebResource(String contents, String contentType, int responseCode) {
        _string = contents;
        _contentType = contentType;
        _responseCode = responseCode;
    }


    public WebResource(byte[] contents, String contentType) {
        this(contents, contentType, HttpURLConnection.HTTP_OK);
    }


    public WebResource(byte[] contents, String contentType, int responseCode) {
        _contents = contents;
        _contentType = contentType;
        _responseCode = responseCode;
    }


    public void addHeader(String header) {
        _headers.addElement(header);
    }


    void setCharacterSet(String characterSet) {
        _characterSet = characterSet;
    }


    void setSendCharacterSet(boolean enabled) {
        _sendCharacterSet = enabled;
    }


    String[] getHeaders() {
        String[] headers = new String[_headers.size()];
        _headers.copyInto(headers);
        return headers;
    }


    byte[] getContents() throws UnsupportedEncodingException {
        if (_string != null) {
            return _string.getBytes(getCharacterSet());
        } else {
            return _contents;
        }
    }


    String getContentType() {
        return _contentType;
    }


    String getCharacterSet() {
        return HttpUnitUtils.stripQuotes(_characterSet);
    }


    String getCharacterSetParameter() {
        if (!_sendCharacterSet) {
            return "";
        } else {
            return "; charset=" + _characterSet;
        }
    }


    int getResponseCode() {
        return _responseCode;
    }


    public String toString() {
        return "WebResource [code=" + _responseCode + "; type = " + _contentType
                + "; charset = " + _characterSet + "]\n" + getContentsAsString();
    }

    private String getContentsAsString() {
        if (_string != null) {
            return _string;
        } else {
            return "<< hex bytes >>";
        }
    }


    private byte[] _contents;
    private String _string;

    private int _responseCode;
    private boolean _sendCharacterSet;
    private String _contentType = DEFAULT_CONTENT_TYPE;
    private String _characterSet = DEFAULT_CHARACTER_SET;
    private Vector _headers = new Vector();
}



