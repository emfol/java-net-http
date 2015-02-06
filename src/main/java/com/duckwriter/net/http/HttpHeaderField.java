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

        // check arguments
        if ( buf == null || off < 0 || len < 2 ) {
            throw new IllegalArgumentException(
                "Bad Constructor Arguments For HTTP Header Field"
            );
        }

        // initialize fields
        this.fieldContents = new char[len];
        this.fieldValueOffset = 0;
        this.fieldValueLength = 0;

        // parse buffer contents
        this.parse( buf, off, len );

    }

    private void parse( byte[] buf, int off, int len ) throws HttpException {

        int cnt;
        char sep;

        // read token from input buffer
        cnt = HttpProtocol.parseToken( buf, off, this.fieldContents, 0, len );
        if ( cnt < 1 || cnt >= len ) {
            throw new HttpException("Bad Name For Header Field");
        }

        // check header separator
        sep = (char)(255 & buf[off + cnt]);
        if ( sep != ':' ) {
            throw new HttpException("Bad Separator For Header Field");
        }

        // save field value offset and increment counter to skip colon
        this.fieldValueOffset = cnt++;

        // update input offset and length
        off += cnt;
        len -= cnt;

        // try to parse field value from input buffers
        cnt = HttpProtocol.parseFieldValue(
            buf,
            off,
            this.fieldContents,
            this.fieldValueOffset,
            len
        );
        if ( cnt < 0 ) {
            throw new HttpException("Bad Value For Header Field");
        }

        // save value length
        this.fieldValueLength = cnt;

    }

    public void append( byte[] buf, int off, int len ) {

        final int oldLen = this.fieldValueOffset + this.fieldValueLength;
        final char[] oldBuf = this.fieldContents,
            newBuf = new char[ oldLen + len + 1 ];

        int i, cnt;

        // copy contents
        for ( i = 0; i < oldLen; i++ ) {
            newBuf[i] = oldBuf[i];
        }

        // add a single space character to new buffer
        newBuf[i++] = ' ';

        // try to parse field value from input buffers
        cnt = HttpProtocol.parseFieldValue(
            buf,
            off,
            newBuf,
            i,
            len
        );
        if ( cnt < 0 ) {
            throw new HttpException("Bad Value For Header Field");
        }

        this.fieldContents = newBuf;
        this.fieldValueLength += cnt + 1; // +1 for additional space

    }

}
