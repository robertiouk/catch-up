package tech.mapleton.catchup;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmailParserTest {

    @Test
    public void isValid() {
        assertTrue(EmailParser.isValid("asd@fmail.com"));
        assertTrue(EmailParser.isValid("asdfd@fmail.co.uk"));
        assertTrue(EmailParser.isValid("ssdf23@f322.com"));
        assertTrue(EmailParser.isValid("ssdf23@f322.net"));
        assertTrue(EmailParser.isValid("ssdf23@f322.org"));
        assertTrue(EmailParser.isValid("ssdf23@f322.tech"));

        assertFalse(EmailParser.isValid("bademail"));
    }
}