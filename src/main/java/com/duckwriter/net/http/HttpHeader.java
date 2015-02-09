package com.duckwriter.net.http;

class HttpHeader extends Object {

    private static final char[] HTTP_IDENTIFIER = { 'H', 'T', 'T', 'P', '/' };

    private int statusCode;
    private int majorVersion;
    private int minorVersion;

    HttpHeader( byte[] buf, int off, int len ) throws HttpException {

        super();

        // check arguments
        if ( buf == null || off < 0 || len < 1 ) {
            throw new IllegalArgumentException(
                "Bad Constructor Arguments For HTTP Header"
            );
        }

        this.parse( buf, off, len );

    }

    private void parse( byte[] buf, int off, int len ) throws HttpException {

        final char[] identifier = HTTP_IDENTIFIER;
        int i, stat, minVer, majVer;
        char c;

        lim = identifier.length;
        if ( len < lim + 7 ) {
            throw new HttpException(
                "Bad Status Line Size For HTTP Header"
            );
        }

        for ( i = 0; i < lim; i++ ) {
            c = (char)( 255 & buf[off + i] );
            if ( c != identifier[i] ) {
                throw new HttpException(
                    "Bad Status Line Identifier For HTTP Header"
                );
            }
        }

        for ( i = 0, stat = 0; i < len; i++ ) {
            c = (char)( 255 & buf[off + i] );
            switch ( stat ) {
            case 0: // identifier
                if ( c != identifier[i] ) {
                    throw new HttpException(
                        "Bad Status Line Identifier For HTTP Header"
                    );
                }
                if ( i + 1 == identifier.length )
                    stat++;
                break;
            case 1: // major version
                break;
            }
        }

    }

}
