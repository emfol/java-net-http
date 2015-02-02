package com.duckwriter.net.http;

public final class HttpProtocol extends Object {

    /**
     * Private Constants
     */

    private static final char[] ASCII_SEPARATORS = {
        0x22 /* " */, 0x28 /* ( */, 0x29 /* ) */, 0x2C /* , */, 0x2F /* / */,
        0x3A /* : */, 0x3B /* ; */, 0x3C /* < */, 0x3D /* = */, 0x3E /* > */,
        0x3F /* ? */, 0x40 /* @ */, 0x5B /* [ */, 0x5C /* \ */, 0x5D /* ] */,
        0x7B /* { */, 0x7D /* } */
    }; // + HT (0x09) + SP (0x20)

    private static final char ASCII_HT  = 0x09;
    private static final char ASCII_LF  = 0x0A;
    private static final char ASCII_CR  = 0x0D;
    private static final char ASCII_SP  = 0x20;
    private static final char ASCII_DEL = 0x7F;

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

    public static boolean isLWS( char c ) {
        return ( c == ASCII_SP || c == ASCII_HT || c == ASCII_CR );
    }

    public static int findEndOfLine( byte[] buf, int off, int len ) {

        int i, stat = 0;
        char prev, curr;

        for ( i = 0; i < len; i++ ) {
            curr = (char)( 255 & buf[off + i] );
            if ( stat != 0 ) {
                if ( prev == ASCII_CR && curr == ASCII_LF ) {
                    stat--;
                }
                i--;
                break;
            } else if ( curr == ASCII_CR || curr == ASCII_LF ) {
                prev = curr;
                stat++;
            }
        }

        return ( stat != 0 ) ? (-i) - 1 : i;

    }

    public static int parseToken( byte[] src, int offsrc, char[] dst, int offdst, int len ) {

        final char[] sep = ASCII_SEPARATORS;
        final int seplen = sep.length;
        int i, k;
        char c;

        scan:
        for ( i = 0; i < len; i++ ) {
            c = (char)( 255 & src[offsrc + i] );
            if ( c <= ASCII_SP || c >= ASCII_DEL ) {
                break scan;
            }
            for ( k = 0; k < seplen; k++ ) {
                if ( c == sep[k] ) {
                    break scan;
                }
            }
            dst[offdst + i] = c;
        }

        return i;

    }

    public static int parseFieldValue( byte[] src, int offsrc, char[] dst, int offdst, int len ) {

        int i, j, stat = 0;
        char c;

        for ( i = 0, j = 0; i < len; i++ ) {

            c = (char)( 255 & src[offsrc + i] );

            if ( c == ASCII_SP || c == ASCII_HT ) {
                if ( (stat & 1) != 0 ) {
                    stat = (stat | 2) ^ 1;
                }
                continue;
            }

            if ( c < ASCII_SP || c == ASCII_DEL ) {
                stat |= 4;
                break;
            }

            if ( (stat & 2) != 0 ) {
                dst[offdst + j++] = ASCII_SP;
                stat ^= 2;
            }

            dst[offdst + j++] = c;
            stat |= 1;

        }

        if ( (stat & 4) != 0 ) {
            j = (-j) - 1;
        }

        return j;

    }

    /**
     * Constructors (No Instance Needed)
     */

    private HttpProtocol() {
        super();
    }

}
