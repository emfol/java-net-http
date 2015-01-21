package com.duckwriter.net.http;

public final class HttpProtocol extends Object {

    /**
     * Private Constants
     */

    private static final char[] CHAR_ASCII_SEPARATORS = {
        0x22 /* " */, 0x28 /* ( */, 0x29 /* ) */, 0x2C /* , */, 0x2F /* / */,
        0x3A /* : */, 0x3B /* ; */, 0x3C /* < */, 0x3D /* = */, 0x3E /* > */,
        0x3F /* ? */, 0x40 /* @ */, 0x5B /* [ */, 0x5C /* \ */, 0x5D /* ] */,
        0x7B /* { */, 0x7D /* } */
    }; // + HT (0x09) + SP (0x20)

    private static final char CHAR_ASCII_HT  = 0x09;
    private static final char CHAR_ASCII_LF  = 0x0A;
    private static final char CHAR_ASCII_CR  = 0x0D;
    private static final char CHAR_ASCII_SP  = 0x20;
    private static final char CHAR_ASCII_DEL = 0x7F;

    private static final int INT_STATE_MASK_LWS = 0x03;
    private static final int INT_STATE_LWS_OFF = 0x00;
    private static final int INT_STATE_LWS_CR = 0x01;
    private static final int INT_STATE_LWS_LF = 0x02;
    private static final int INT_STATE_LWS_ON = 0x03;


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

    public static int countBlank( byte[] buf, int off, int len ) {

        char c;
        int i;

        for ( i = 0; i < len; ++i ) {
            c = (char)( 0xFF & buf[off + i] );
            if ( c != CHAR_ASCII_SP && c != CHAR_ASCII_HT ) {
                break;
            }
        }

        return i;

    }

    public static int parseToken( byte[] src, int offsrc, char[] dst, int offdst, int len ) {

        final char[] sep = CHAR_ASCII_SEPARATORS;
        final int seplen = sep.length;
        int i, k;
        char c;

        scan:
        for ( i = 0; i < len; ++i ) {
            c = (char)( 0xFF & src[offsrc + i] );
            if ( c <= CHAR_ASCII_SP || c >= CHAR_ASCII_DEL ) {
                break scan;
            }
            for ( k = 0; k < seplen; ++k ) {
                if ( c == sep[k] ) {
                    break scan;
                }
            }
            dst[offdst + i] = c;
        }

        return i;

    }

    public static int parseFieldValue( byte[] src, int offsrc, char[] dst, int offdst, int len ) {

        boolean failure = false;
        int i, j, lws, state = 0;
        char c;

        scan:
        for ( i = 0, j = 0; i < len; ++i ) {

            c = (char)( 0xFF & src[offsrc + i] );

            lws = state & INT_STATE_MASK_LWS;

            if ( lws != INT_STATE_LWS_ON && lws != INT_STATE_LWS_OFF ) {
                if ( lws == INT_STATE_LWS_CR && c == CHAR_ASCII_LF ) {
                    lws = INT_STATE_LWS_LF;
                } else if ( lws == INT_STATE_LWS_LF && ( c == CHAR_ASCII_SP || c == CHAR_ASCII_HT ) ) {
                    lws = INT_STATE_LWS_ON;
                } else {
                    failure = true;
                    break scan;
                }
                state = (state & ~INT_STATE_MASK_LWS) | lws;
                continue scan;
            }

            if ( c == CHAR_ASCII_SP || c == CHAR_ASCII_HT || c == CHAR_ASCII_CR ) {
                lws = (c == CHAR_ASCII_CR) ? INT_STATE_LWS_CR : INT_STATE_LWS_ON;
                state = (state & ~INT_STATE_MASK_LWS) | lws;
                continue scan;
            }

            if ( c < CHAR_ASCII_SP && c != CHAR_ASCII_HT ) {
                break;
            }
            if ( c == CHAR_HT || c == CHAR_SP ) {
                state |= 0x01;
                continue;
            }
            if ( state == STATE
        }

        if ( failure ) {
            j = -j;
        }

        return j;

    }

    /**
     * Constructors (No Instance Needed)
     */

    private HttpProtocol {
        super();
    }

}
