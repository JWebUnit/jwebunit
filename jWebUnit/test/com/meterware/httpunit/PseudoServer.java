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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * A basic simulated web-server for testing the HttpUnit library.
 **/
public class PseudoServer {


    public PseudoServer() {
        Thread t = new Thread() {
            public void run() {
                while (_active) {
                    try {
                        handleConnection();
                        Thread.sleep(50);
                    } catch (InterruptedIOException e) {
                        _active = false;
                    } catch (IOException e) {
                        System.out.println("Error in pseudo server: " + e);
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted. Shutting down");
                        _active = false;
                    }
                }
                try {
                    if (_serverSocket != null) _serverSocket.close();
                    _serverSocket = null;
                } catch (IOException e) {
                }
            }
        };
        t.start();
    }


    public void shutDown() {
        _active = false;
    }


    /**
     * Returns the port on which this server is listening.
     **/
    public int getConnectedPort() throws IOException {
        return getServerSocket().getLocalPort();
    }


    /**
     * Defines the contents of an expected resource.
     **/
    public void setResource(String name, String value) {
        setResource(name, value, "text/html");
    }


    /**
     * Defines the contents of an expected resource.
     **/
    public void setResource(String name, PseudoServlet servlet) {
        _resources.put(asResourceName(name), servlet);
    }


    /**
     * Defines the contents of an expected resource.
     **/
    public void setResource(String name, String value, String contentType) {
        _resources.put(asResourceName(name), new WebResource(value, contentType));
    }


    /**
     * Defines the contents of an expected resource.
     **/
    public void setResource(String name, byte[] value, String contentType) {
        _resources.put(asResourceName(name), new WebResource(value, contentType));
    }


    /**
     * Defines a resource which will result in an error message.
     **/
    public void setErrorResource(String name, int errorCode, String errorMessage) {
        _resources.put(asResourceName(name), new WebResource(errorCode, errorMessage));
    }


    /**
     * Enables the sending of the character set in the content-type header.
     **/
    public void setSendCharacterSet(String name, boolean enabled) {
        WebResource resource = (WebResource) _resources.get(asResourceName(name));
        if (resource == null) throw new IllegalArgumentException("No defined resource " + name);
        resource.setSendCharacterSet(enabled);
    }


    /**
     * Specifies the character set encoding for a resource.
     **/
    public void setCharacterSet(String name, String characterSet) {
        WebResource resource = (WebResource) _resources.get(asResourceName(name));
        if (resource == null) {
            resource = new WebResource("");
            _resources.put(asResourceName(name), resource);
        }
        resource.setCharacterSet(characterSet);
    }


    /**
     * Adds a header to a defined resource.
     **/
    public void addResourceHeader(String name, String header) {
        WebResource resource = (WebResource) _resources.get(asResourceName(name));
        if (resource == null) {
            resource = new WebResource("");
            _resources.put(asResourceName(name), resource);
        }
        resource.addHeader(header);
    }


//------------------------------------- private members ---------------------------------------

    private Hashtable _resources = new Hashtable();

    private boolean _active = true;


    private String asResourceName(String rawName) {
        if (rawName.startsWith("/")) {
            return rawName;
        } else {
            return "/" + rawName;
        }
    }


    private void handleConnection() throws IOException {
        Socket socket = getServerSocket().accept();
        socket.setSoTimeout(1000);
        socket.setTcpNoDelay(true);

        HttpRequestStream request = new HttpRequestStream(socket.getInputStream());
        HttpResponseStream response = new HttpResponseStream(socket.getOutputStream());

        try {
            WebResource resource = getResource(request);
            if (resource == null) {
                response.setResponse(HttpURLConnection.HTTP_NOT_FOUND, "unable to find " + request.getURI());
            } else {
                if (resource.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    response.setResponse(resource.getResponseCode(), "");
                }
                response.addHeader("Content-type: " + resource.getContentType() + resource.getCharacterSetParameter());
                String[] headers = resource.getHeaders();
                for (int i = 0; i < headers.length; i++) {
                    response.addHeader(headers[i]);
                }
                response.write(resource.getContents());
            }
        } catch (UnknownMethodException e) {
            response.setResponse(HttpURLConnection.HTTP_BAD_METHOD, "unsupported method: " + e.getMethod());
        } catch (IOException e) {
            e.fillInStackTrace();
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            response.setResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, t.toString());
        } finally {
            response.close();
            socket.close();
        }
    }


    private WebResource getResource(HttpRequestStream request) throws IOException {
        Object resource = _resources.get(request.getURI());

        if (request.getCommand().equals("GET") && resource instanceof WebResource) {
            return (WebResource) resource;
        } else if (resource instanceof PseudoServlet) {
            return getResource((PseudoServlet) resource, request);
        } else {
            return null;
        }
    }


    private WebResource getResource(PseudoServlet servlet, HttpRequestStream request) throws IOException {
        servlet.init(request);
        return servlet.getResponse(request.getCommand());
    }


    private ServerSocket getServerSocket() throws IOException {
        synchronized (this) {
            if (_serverSocket == null) _serverSocket = new ServerSocket(0);
            _serverSocket.setSoTimeout(1000);
        }
        return _serverSocket;
    }


    private ServerSocket _serverSocket;

}