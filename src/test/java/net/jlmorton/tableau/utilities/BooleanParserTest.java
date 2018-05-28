package net.jlmorton.tableau.utilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanParserTest {
    @Test
    public void testBooleanParser() {
        assertTrue(BooleanParser.parse("on"));
        assertTrue(BooleanParser.parse("true"));
        assertTrue(BooleanParser.parse("t"));
        assertTrue(BooleanParser.parse("1"));
        assertTrue(BooleanParser.parse("yes"));

        assertFalse(BooleanParser.parse("foo"));
        assertFalse(BooleanParser.parse("0"));
        assertFalse(BooleanParser.parse("-"));
    }
}