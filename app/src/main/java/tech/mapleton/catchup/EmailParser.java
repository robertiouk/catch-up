package tech.mapleton.catchup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A static utility class to provide email verification and parsing helper methods.
 */
public class EmailParser {
    private static final String REGEX = "(\\w*@\\w*)\\..+";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE);

    private EmailParser() {}

    /**
     * Run given string through regex to check whether email is valid
     * @param email the given email address
     * @return true if valid, false if not
     */
    public static boolean isValid(final String email) {
        final Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}
