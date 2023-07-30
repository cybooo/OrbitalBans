package dev.cybo.orbitalbans.utils;

import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FormatUtils {

    public static String formatConfigString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String formatRemainingTime(long timeMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long hours = TimeUnit.MINUTES.toHours(minutes);
        long days = TimeUnit.HOURS.toDays(hours);

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append("d ");
            hours %= 24;
        }
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        result.append(minutes % 60).append("m ").append(seconds % 60).append("s");

        return result.toString();
    }

    public static long convertToMillis(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            throw new IllegalArgumentException("Invalid time string: null or empty");
        }

        long multiplier;

        if (timeString.endsWith("d")) {
            multiplier = TimeUnit.DAYS.toMillis(1);
        } else if (timeString.endsWith("h")) {
            multiplier = TimeUnit.HOURS.toMillis(1);
        } else if (timeString.endsWith("m")) {
            multiplier = TimeUnit.MINUTES.toMillis(1);
        } else if (timeString.endsWith("s")) {
            multiplier = TimeUnit.SECONDS.toMillis(1);
        } else {
            throw new IllegalArgumentException("Invalid time unit in the input string");
        }

        try {
            int value = Integer.parseInt(timeString.substring(0, timeString.length() - 1).trim());
            if (value < 0) {
                throw new IllegalArgumentException("Time value must be a non-negative integer");
            }
            return value * multiplier;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time value in the input string");
        }
    }

    public static String formatIntoDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(timeMillis);

        return simpleDateFormat.format(date);
    }


}
