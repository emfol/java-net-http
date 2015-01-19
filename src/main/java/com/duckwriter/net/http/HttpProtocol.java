package com.duckwriter.net.http;

public final class HttpProtocol extends Object {

    /**
     * Private Constants
     */

    private static final char[] SEPARATORS = {
        0x09, // HT
        0x20, // SP
        0x22, // "
        0x28, // (
        0x29, // )
        0x2C, // ,
        0x2F, // /
        0x3A, // :
        0x3B, // ;
        0x3C, // <
        0x3D, // =
        0x3E, // >
        0x3F, // ?
        0x40, // @
        0x5B, // [
        0x5C, // \
        0x5D, // ]
        0x7B, // {
        0x7D  // }
    };

    /**
     * Public Constants
     */

    public static final char HT = 0x09;
    public static final char LF = 0x0A;
    public static final char CR = 0x0D;
    public static final char SP = 0x20;

    /**
     * Public Class Methods
     */

    public static boolean isASCII( char c ) {
        return ( c < 0x80 );
    }

    public static boolean isAlpha( final char c ) {
        final char a = c | 0x20;
        return ( a > 0x60 && a < 0x7B );
    }

    public static boolean isDigit( char c ) {
        return ( c > 0x2F && c < 0x3A );
    }

    public static boolean isControl( char c ) {
        return ( c < 0x20 || c == 0x7F );
    }

    public static int countBlank( byte[] buf, int off, int cnt ) {

        int i;
        char c;

        for ( i = 0; i < cnt; ++i ) {
            c = (char)(0xFF & buf[i + off]);
            if ( c != HttpProtocol.SP && c != HttpProtocol.HT )
                break;
        }

        return i;

    }

    public static int parseToken( byte[] src, int srcOff, char[] dst, int dstOff, int cnt ) {

        final char[] sep = HttpProtocol.SEPARATORS;
        int i, k, sepCnt = sep.length;
        char c;

        scan:
        for ( i = 0; i < cnt; ++i ) {
            c = (char)(0xFF & src[i + srcOff]);
            if ( c < 0x20 || c > 0x7E )
                break scan;
            for ( k = 0; k < sepCnt; ++k )
                if ( c == sep[k] )
                    break scan;
            dst[i + dstOff] = c;
        }

        return i;

    }

    /**
     * No Instance Needed
     */

    private HttpProtocol {
        super();
    }

}
