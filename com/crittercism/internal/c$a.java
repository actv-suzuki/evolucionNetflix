// 
// Decompiled by Procyon v0.5.30
// 

package com.crittercism.internal;

public enum c$a
{
    a("NOT_LOGGED_YET", 0, "Not logged"), 
    b("INPUT_STREAM_READ", 1, "InputStream.read()"), 
    c("INPUT_STREAM_CLOSE", 2, "InputStream.close()"), 
    d("SOCKET_CLOSE", 3, "Socket.close()"), 
    e("LEGACY_JAVANET", 4, "Legacy java.net"), 
    f("HTTP_CONTENT_LENGTH_PARSER", 5, "parse()"), 
    g("INPUT_STREAM_FINISHED", 6, "finishedMessage()"), 
    h("PARSING_INPUT_STREAM_LOG_ERROR", 7, "logError()"), 
    i("SOCKET_IMPL_CONNECT", 8, "MonitoredSocketImpl.connect()"), 
    j("SSL_SOCKET_START_HANDSHAKE", 9, "MonitoredSSLSocketKK.startHandshake"), 
    k("UNIT_TEST", 10, "Unit test"), 
    l("LOG_ENDPOINT", 11, "logEndpoint"), 
    m("WEBVIEW_CLIENT_ON_PAGE_FINISHED", 12, "onPageFinished"), 
    n("WEBVIEW_CLIENT_ON_RECEIVED_ERROR", 13, "onReceivedError");
    
    private String o;
    
    private c$a(final String s, final int n, final String o) {
        this.o = o;
    }
    
    @Override
    public final String toString() {
        return this.o;
    }
}
