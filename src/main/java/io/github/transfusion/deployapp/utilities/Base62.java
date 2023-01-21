package io.github.transfusion.deployapp.utilities;

/**
 * @see <a href="https://gist.github.com/jdcrensh/4670128">Here</a> for the original implementation.
 */
public class Base62 {

    public static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final int BASE = ALPHABET.length();

    private Base62() {
    }

    public static String fromBase10(long i) {
        StringBuilder sb = new StringBuilder("");
        if (i == 0) {
            return "a";
        }
        while (i > 0) {
            i = fromBase10(i, sb);
        }
        return sb.reverse().toString();
    }

    private static long fromBase10(long i, final StringBuilder sb) {
        long rem = i % BASE; // base is 62; fits in an int
        sb.append(ALPHABET.charAt((int) rem));
        return i / BASE;
    }

    public static long toBase10(String str) {
        return toBase10(new StringBuilder(str).reverse().toString().toCharArray());
    }

    private static long toBase10(char[] chars) {
        long n = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            n += toBase10(ALPHABET.indexOf(chars[i]), i);
        }
        return n;
    }

    private static long toBase10(long n, int pow) {
        return n * (long) Math.pow(BASE, pow);
    }
}