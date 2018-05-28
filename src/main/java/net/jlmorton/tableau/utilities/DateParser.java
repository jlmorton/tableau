package net.jlmorton.tableau.utilities;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Objects;

public class DateParser {
    private static final DateTimeFormatter MICROSECOND_DATE_FORMATTER = getMicrosecondDateTimeFormatter();
    private static final ISO8601DateFormat ISO_8601_DATE_FORMAT = new ISO8601DateFormat();

    public static Date parse(String dateString) {
        Date parsedDate = parseIso8601DateFormat(dateString);
        if (!Objects.isNull(parsedDate)) {
            return parsedDate;
        }

        parsedDate = parsePostgresqlStyleDateTime(dateString);
        if (!Objects.isNull(parsedDate)) {
            return parsedDate;
        }

        return Timestamp.valueOf(LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private static Date parseIso8601DateFormat(String dateString) {
        try {
            return ISO_8601_DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            // Ignore
        }

        return null;
    }

    private static Date parsePostgresqlStyleDateTime(String dateString) {
        try {
            return Timestamp.valueOf(LocalDateTime.parse(dateString, MICROSECOND_DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            // Ignore
        }

        return null;
    }

    private static DateTimeFormatter getMicrosecondDateTimeFormatter() {
        String basePattern = "yyyy-MM-dd HH:mm:ss";

        return new DateTimeFormatterBuilder().appendPattern(basePattern)
                .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 6, true)
                .appendOffset("+HH", "Z")
                .toFormatter();
    }
}
