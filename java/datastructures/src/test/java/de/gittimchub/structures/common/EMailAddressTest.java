package de.gittimchub.structures.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EMailAddressTest {
    private final String validEmail = "test@abc.de";
    private final String invalidEmail = "test@abc";


    /**
     * Tests EMail Regex (valid EMail).
     * @throws IllegalArgumentException if the email is invalid / not matching the regexp
     */
    @Test
    public void shouldBeAnValidEMail() {
        EMailAddress email = new EMailAddress(validEmail);
        assertEquals(email.toString(), validEmail);
    }

    /**
     * Tests EMail Regex (invalid EMail).
     * @throws IllegalArgumentException if the email is invalid / not matching the regexp
     */
    @Test
    public void shouldBeAnInvalidEMail() throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            new EMailAddress(invalidEmail);
        });

    }


}