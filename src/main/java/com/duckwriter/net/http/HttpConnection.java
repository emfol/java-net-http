package com.duckwriter.net.http;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import java.util.Map;
import java.util.HashMap;

import java.io.Closeable;
import java.io.IOException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;

public class HttpConnection extends Object implements Closeable {

    /*
     * Constants
     */

    // config
    private static final int CONFIG_DEFAULT_PORT = 80;
    private static final int CONFIG_DEFAULT_CONNECT_TIMEOUT = 60000;
    private static final int CONFIG_DEFAULT_IDLE_TIMEOUT = 60000;

    // options
    private static final String OPTION_CONNECT_TIMEOUT = "connectTimeout";
    private static final String OPTION_IDLE_TIMEOUT = "idleTimeout";

    // http
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    /*
     * Class Members
     */

    private static final Map<String, Reference<InetAddress>> addressCache;

    static {
        addressCache = new HashMap<String, Reference<InetAddress>>();
    }

    private final String host;
    private final int port;
    private final Map<String, Object> options;
    private Socket socket;

    public HttpConnection( String host, int port ) {

        // initialize super class
        super();

        // validate parameters
        if ( port < 0 || port > 65535 ) {
            throw new IllegalArgumentException("Bad Port Number");
        }

        if ( host == null ) {
            throw new IllegalArgumentException("Bad Host Name");
        }

        // initialize instance variables
        this.host = host;
        this.port = port;
        this.options = new HashMap<String, Object>();
        this.socket = null;

    }

    public HttpConnection( String host ) {
        this( host, CONFIG_DEFAULT_PORT );
    }

    /*
     * Static Methods
     */

    private static InetAddress getHostAddress( String host )
        throws UnknownHostException {

        Reference<InetAddress> ref = HttpConnection.addressCache.get( host );
        InetAddress adr = (ref != null) ? ref.get() : null;

        if ( adr == null ) {

            // remove null reference
            if ( ref != null ) {
                HttpConnection.addressCache.remove( host );
            }

            // dns lookup might throw UnknownHostException
            adr = InetAddress.getByName( host );

            // save to cache on success
            ref = new SoftReference<InetAddress>( adr );
            HttpConnection.addressCache.put( host, ref );

        }

        return adr;

    }

    /**
     * Private Instance Methods
     */

    /*
     * Throws:
     * -- java.io.IOException
     * -- -- java.net.SocketException
     * -- -- -- java.net.ConnectException
     * -- -- java.net.UnknownHostException
     * -- -- java.net.SocketTimeoutException
     */
    private void connect() throws IOException {

        SocketAddress address;

        // create and initialize socket
        if ( this.socket == null || this.socket.isClosed() ) {
            this.socket = new Socket();
            this.socket.setSoTimeout( this.getIdleTimeout() );
        }

        // try to connect
        if ( !this.socket.isConnected() ) {
            address = new InetSocketAddress(
                HttpConnection.getHostAddress( this.host ),
                this.port
            );
            this.socket.connect( address, this.getConnectTimeout() );
        }

    }

    private void send( String method, String path, InputStream data ) throws IOException {

    }

    private HttpResponse receive() throws IOException {
        return null;
    }

    private HttpResponse request( String method, String path, InputStream data )
        throws IOException {

        this.connect();
        this.send( method, path, data );
        return this.receive();

    }

    /*
     * Interface
     */

    public void setConnectTimeout( int timeout ) {

        if ( timeout < 0 ) {
            timeout = CONFIG_DEFAULT_CONNECT_TIMEOUT;
        }

        this.options.put(
            OPTION_CONNECT_TIMEOUT,
            new Integer( timeout )
        );

    }

    public int getConnectTimeout() {

        int tVal = CONFIG_DEFAULT_CONNECT_TIMEOUT;
        Integer tObj = (Integer)this.options.get( OPTION_CONNECT_TIMEOUT );
        if ( tObj != null ) {
            tVal = tObj.intValue();
        }
        return tVal;

    }

    public void setIdleTimeout( int timeout ) {

        if ( timeout < 0 ) {
            timeout = CONFIG_DEFAULT_IDLE_TIMEOUT;
        }

        this.options.put(
            OPTION_IDLE_TIMEOUT,
            new Integer( timeout )
        );

    }

    public int getIdleTimeout() {

        int tVal = CONFIG_DEFAULT_IDLE_TIMEOUT;
        Integer tObj = (Integer)this.options.get( OPTION_IDLE_TIMEOUT );
        if ( tObj != null ) {
            tVal = tObj.intValue();
        }
        return tVal;

    }

    public HttpResponse get( String path ) throws IOException {
        return this.request( HTTP_METHOD_GET, path, null );
    }

    public HttpResponse post( String path, InputStream data ) throws IOException {
        return this.request( HTTP_METHOD_POST, path, data );
    }

    /* Closable Implementation */
    public void close() throws IOException {
        if ( this.socket != null ) {
            if ( !this.socket.isClosed() ) {
                this.socket.close();
            }
            this.socket = null;
        }
    }

}
