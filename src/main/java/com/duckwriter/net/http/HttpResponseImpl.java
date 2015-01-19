package com.duckwriter.net.http.HttpResponse;

public class HttpResponseImpl extends Object
    implements HttpResponse {

    private static final int STATUS_HEADER_PARSED = 0;

    private int parsingState;

    HttpResponseImpl() {
        super();
    }

    int parse(byte[] data) {

    }

    public int getResponseStatusCode() {
        return 0;
    }

    public String getResponseStatusPhrase() {
        return null;
    }

    public String getResponseHeader( String name ) {
        return null;
    }

    public InputStream getResponseBody() {
        return null;
    }

}
