package seedu.addressbook;

import java.util.ArrayList;
import java.util.Arrays;

public final class Utility {

	private Utility() {}
	
    /**
     * Removes sign(p/, d/, etc) from parameter string
     *
     * @param s  Parameter as a string
     * @param sign  Parameter sign to be removed
     *
     * @return  Priority string without p/
     */
    public static String removePrefixSign(String s, String sign) {
        return s.replace(sign, "");
    }

    /**
     * Splits a source string into the list of substrings that were separated by whitespace.
     *
     * @param toSplit source string
     * @return split by whitespace
     */
    public static ArrayList<String> splitByWhitespace(String toSplit) {
        return new ArrayList(Arrays.asList(toSplit.trim().split("\\s+")));
    }
    
}
