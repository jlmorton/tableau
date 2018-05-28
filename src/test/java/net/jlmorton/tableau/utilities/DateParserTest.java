package net.jlmorton.tableau.utilities;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DateParserTest {
    @Test
    public void testSimpleIsoDate() {
        Calendar calendar = getCalendar(DateParser.parse("2018-02-12"));
        assertEquals(2018, calendar.get(Calendar.YEAR));
        assertEquals(1, calendar.get(Calendar.MONTH));
        assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testPostgresqlFormatWithTimezone() {
        Calendar calendar = getCalendar(DateParser.parse("2017-05-29 13:43:05.004879-07"));
        assertEquals(2017, calendar.get(Calendar.YEAR));
        assertEquals(4, calendar.get(Calendar.MONTH));
        assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(43, calendar.get(Calendar.MINUTE));
        assertEquals(5, calendar.get(Calendar.SECOND));
        assertEquals(4, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void testIso8601Timestamp() {
        Calendar calendar = getCalendar(DateParser.parse("2018-05-27T23:25:08+00:00"));
        assertEquals(2018, calendar.get(Calendar.YEAR));
        assertEquals(4, calendar.get(Calendar.MONTH));
        assertEquals(27, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(25, calendar.get(Calendar.MINUTE));
        assertEquals(8, calendar.get(Calendar.SECOND));
    }

    @Test
    public void testParseDateTimeHourMinuteSeconds() {
        Calendar cal = getCalendar(DateParser.parse("2017-09-27 02:01:01"));
        assertEquals(2017, cal.get(Calendar.YEAR));
        assertEquals(8, cal.get(Calendar.MONTH));
        assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2, cal.get(Calendar.HOUR));
        assertEquals(1, cal.get(Calendar.MINUTE));
        assertEquals(1, cal.get(Calendar.SECOND));
    }

    private Calendar getCalendar(Date parsedDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(parsedDate);

        return calendar;
    }
}