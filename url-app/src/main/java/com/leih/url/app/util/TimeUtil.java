package com.leih.url.app.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeUtil {
  /** Default datetime format */
  private static final String DEFAULT_PATTERN = "dd-MM-yyyy HH:mm:ss";

  private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
  private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

  /**
   * Convert LocalDateTime to String using a specific pattern
   *
   * @param localDateTime
   * @param pattern
   * @return
   */
  public static String format(LocalDateTime localDateTime, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    String timeStr = formatter.format(localDateTime.atZone(DEFAULT_ZONE_ID));
    return timeStr;
  }

  /**
   * Convert Date to String using a specific pattern
   *
   * @param time
   * @param pattern
   * @return
   */
  public static String format(Date time, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    String timeStr = formatter.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
    return timeStr;
  }

  /**
   * Convert Date to String(default format)
   *
   * @param time
   * @return
   */
  public static String format(Date time) {
    String timeStr = DEFAULT_DATE_TIME_FORMATTER.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
    return timeStr;
  }

  /**
   * Convert timestamp to String(default format)
   *
   * @param timestamp
   * @return
   */
  public static String format(long timestamp) {
    String timeStr =
        DEFAULT_DATE_TIME_FORMATTER.format(new Date(timestamp).toInstant().atZone(DEFAULT_ZONE_ID));
    return timeStr;
  }
  /**
   * Convert timestamp to String(custom format)
   *
   * @param timestamp
   * @return
   */
  public static String format(long timestamp,DateTimeFormatter pattern) {
    String timeStr =
            pattern.format(new Date(timestamp).toInstant().atZone(DEFAULT_ZONE_ID));
    return timeStr;
  }

  /**
   * Convert String to timestamp using default format
   *
   * @param time
   * @return
   */
  public static Date strToDate(String time) {
    LocalDateTime localDateTime = LocalDateTime.parse(time, DEFAULT_DATE_TIME_FORMATTER);
    return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
  }

  /**
   * Get the number of seconds left in the day. This method is used for configuring the expiration
   * time of data
   *
   * @param currentDate
   * @return
   */
  public static Integer getSecondsLeftInTheDay(Date currentDate) {
    LocalDateTime midnight =
        LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault())
            .plusDays(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
    LocalDateTime currentDateTime =
        LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
    long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
    return (int) seconds;
  }
}
