package de.gittimchub.structures.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EMailAddress {
    private final String email;
    private final static String REGEX_PATTERN_EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private final static Pattern PATTERN = Pattern.compile(REGEX_PATTERN_EMAIL, Pattern.CASE_INSENSITIVE);

    public EMailAddress(String email) {
        // source: https://stackoverflow.com/questions/8204680/java-regex-email
        Matcher matcher = PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(email + " does not Match Pattern" + REGEX_PATTERN_EMAIL );
        }
        this.email = email.toLowerCase();
    }

    /**
     * Overrides default to String method to use regexp validation.
     * @return String
     */
    @Override
    public String toString() {
        return this.email;
    }

    /**
     * uses regexp validation.
     * @param mail as String
     * @return Email
     */
    public static EMailAddress valueOf(String mail) {
        return new EMailAddress(mail);
    }

}
