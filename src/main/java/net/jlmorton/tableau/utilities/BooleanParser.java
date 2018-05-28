package net.jlmorton.tableau.utilities;

public class BooleanParser {
    private BooleanParser() {

    }

    public static boolean parse(String value) {
        return "t".equalsIgnoreCase(value) ||
                "1".equalsIgnoreCase(value) ||
                "yes".equalsIgnoreCase(value) ||
                "true".equalsIgnoreCase(value) ||
                "on".equalsIgnoreCase(value);
    }
}
