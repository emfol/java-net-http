package com.duckwriter.net.http;

final class HttpHeaderField extends Object {

    private static final int CONFIG_INITIAL_LENGTH = 80;
    private static final int CONFIG_LENGTH_INCREMENT = 32;

    private char[] contents;
    private int valueOffset, valueLength;
    private HttpHeaderField next, lower, higher;

    HttpHeaderField( byte[] buf, int off, int len ) throws HttpException {

        // initialize superclass
        super();

        // initialize fields
        this.contents = new char[ len ];
        this.valueOffset = 0;
        this.valueLength = 0;

        // parse buffer contents
        this.parse( buf, off, len );

    }

    private void parse( byte[] buf, int off, int len ) throws HttpException {

        int cnt;

        // read token from buffer
        cnt = HttpProtocol.parseToken( buf, off, this.contents, 0, len );
        if ( cnt < 1 || cnt >= len || buf[cnt + off] != ':' ) {
            throw new HttpException("Bad Header Field Name");
        }

        // save header value offset
        this.valueOffset = cnt;

        // update counters
        cnt++;
        off += cnt;
        len -= cnt;

        // count whitespaces
        cnt = HttpProtocol.countBlank( buf, off, len );
        off += cnt;
        len -= cnt;

        if ( len > 0 ) {
            cnt = HttpProtocol.parseFieldValue( buf, off, this.contents, this.valueOffset, len );
        }

    }

    

}
