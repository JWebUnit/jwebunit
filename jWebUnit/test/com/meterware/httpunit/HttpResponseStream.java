package com.meterware.httpunit;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Vector;

public class HttpResponseStream {

    final private static String CRLF = "\r\n";


    void close() throws IOException {
        flushHeaders();
        _pw.close();
    }


    HttpResponseStream(OutputStream stream) {
        _stream = stream;
        try {
            setCharacterSet("us-ascii");
        } catch (UnsupportedEncodingException e) {
            _pw = new PrintWriter(new OutputStreamWriter(_stream));
        }
    }


    void setResponse(int responseCode, String responseText) {
        _responseCode = responseCode;
        _responseText = responseText;
    }


    void addHeader(String header) {
        _headers.addElement(header);
    }


    void write(String contents, String charset) throws IOException {
        flushHeaders();
        setCharacterSet(charset);
        sendText(contents);
    }


    void write(byte[] contents) throws IOException {
        flushHeaders();
        _stream.write(contents, 0, contents.length);
    }


    private void setCharacterSet(String characterSet) throws UnsupportedEncodingException {
        if (_pw != null) _pw.flush();
        _pw = new PrintWriter(new OutputStreamWriter(_stream, characterSet));
    }


    private void flushHeaders() {
        if (!_headersWritten) {
            sendResponse(_responseCode, _responseText);
            for (Enumeration e = _headers.elements(); e.hasMoreElements();) {
                sendLine((String) e.nextElement());
            }
            sendText(CRLF);
            _headersWritten = true;
            _pw.flush();
        }
    }


    private void sendResponse(int responseCode, String responseText) {
        sendLine("HTTP/1.0 " + responseCode + ' ' + responseText);
    }


    private void sendLine(String text) {
        sendText(text);
        sendText(CRLF);
    }


    private void sendText(String text) {
        _pw.write(text);
    }


    private OutputStream _stream;
    private PrintWriter _pw;

    private Vector _headers = new Vector();
    private int _responseCode = HttpURLConnection.HTTP_OK;
    private String _responseText = "OK";

    private boolean _headersWritten;
}