/*
---------------------------------------------------------------------------------
File Name : TimeParser

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.api;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)(y|w|d|m|min|sec)");

    /**
     * Parses the given time string and returns the corresponding SQL Timestamp.
     *
     * <p>The method parses a time string in the format of "<number>:<unit>" where the number is an integer
     * representing the duration, and the unit is one of the following:
     * - 'y' for years
     * - 'w' for weeks
     * - 'd' for days
     * - 'm' for months
     * - 'min' for minutes
     * - 'sec' for seconds</p>
     *
     * @param timeArg The time argument in the format of "<number>:<unit>" (e.g., "10:d", "5:sec").
     * @return The {@link Timestamp} calculated by adding the specified duration to the current time.
     * @throws IllegalArgumentException If the time argument is invalid.
     */
    public static Timestamp parseTime(String timeArg) throws ParseException {
        Matcher matcher = TIME_PATTERN.matcher(timeArg.trim());

        if (!matcher.matches()) {
            throw new ParseException("Invalid time format. Expected format is <number>:<unit>", 1);
        }

        int quantity = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        LocalDateTime now = LocalDateTime.now();

        now = switch (unit) {
            case "year", "y" -> // Years
                    now.plusYears(quantity);
            case "week", "w" -> // Weeks
                    now.plusWeeks(quantity);
            case "day", "d" -> // Days
                    now.plusDays(quantity);
            case "month", "m" -> // Months
                    now.plusMonths(quantity);
            case "minute", "min" -> // Minutes
                    now.plusMinutes(quantity);
            case "sec", "s" -> // Seconds
                    now.plusSeconds(quantity);
            case "perm" ->
                now.plusYears(150);
            default -> throw new ParseException("Unknown time unit: " + unit, 1);
        };

        return Timestamp.valueOf(now);
    }
}
