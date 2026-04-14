package com.boothlens.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DB timestamps are stored as UTC ({@link Instant}). Use this class to show times in the app display zone (UTC+8).
 */
public final class DateTimeUtil {

    /** Wall-clock zone for user-facing output (China Standard Time, UTC+8). */
    public static final ZoneId DISPLAY_ZONE = ZoneId.of("Asia/Shanghai");

    private static final DateTimeFormatter DEFAULT_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DISPLAY_ZONE);

    private DateTimeUtil() {
    }

    public static ZonedDateTime toDisplayZonedDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(DISPLAY_ZONE);
    }

    public static LocalDateTime toLocalDateTimeInDisplayZone(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, DISPLAY_ZONE);
    }

    public static String formatForDisplay(Instant instant) {
        if (instant == null) {
            return null;
        }
        return DEFAULT_DISPLAY.format(instant);
    }

    public static String formatForDisplay(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) {
            return null;
        }
        return formatter.withZone(DISPLAY_ZONE).format(instant);
    }
}
