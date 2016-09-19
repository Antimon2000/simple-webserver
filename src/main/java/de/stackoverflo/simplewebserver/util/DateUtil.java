package de.stackoverflo.simplewebserver.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    /** Date format pattern used to generate the header in RFC 1123 format. */
    public static final
        String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static synchronized String getHttpDateFromTimestamp(long time) {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date(time));
    }

    public static synchronized Date parseFromHttpDate(String httpDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
        Date d = dateFormat.parse(httpDate);
        return d;
    }
}
