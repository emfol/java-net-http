package com.duckwriter.net.http;

final class HttpHeaderField extends Object {

    private static final int CONFIG_INITIAL_LENGTH = 80;
    private static final int CONFIG_LENGTH_INCREMENT = 32;

    private char[] fieldContents;
    private int fieldValueOffset, fieldValueLength;
    private HttpHeaderField next, lower, higher;

    HttpHeaderField( byte[] buf, int off, int len ) throws HttpException {

        // initialize superclass
        super();

        // initialize fields
        this.fieldContents = new char[ len ];
        this.fieldValueOffset = 0;
        this.fieldValueLength = 0;

        // parse buffer contents
        this.parse( buf, off, len );

    }

    private void parse( byte[] buf, int off, int len ) throws HttpException {

        int cnt;

        // read token from buffer
        cnt = HttpProtocol.parseToken( buf, off, this.fieldContents, 0, len );
        if ( cnt < 1 || cnt >= len || buf[off + cnt] != ':' ) {
            throw new HttpException("Bad Header Field Name");
        }

        // save field value offset and increment counter to skip colon
        this.fieldValueOffset = cnt++;

        // count whitespaces
        off += cnt;
        len -= cnt;
        cnt = HttpProtocol.countBlank( buf, off, len );
        if ( (len -= cnt) > 0 ) {
            off += cnt;
            cnt = HttpProtocol.parseFieldValue(
                buf,
                off,
                this.fieldContents,
                this.fieldValueOffset,
                len
            );
        }

    }

}
