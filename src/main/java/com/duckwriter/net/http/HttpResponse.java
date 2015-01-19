package com.duckwriter.net.http;

import java.io.InputStream;

public interface HttpResponse {

    public abstract int getResponseStatusCode();

    public abstract String getResponseStatusPhrase();

    public abstract String getResponseHeader( String name );

    public abstract InputStream getResponseBody();

}
