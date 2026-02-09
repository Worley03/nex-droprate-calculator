package com.nexdropratecalculator;

public class Utils {
     static String fraction(int value) {
        if (value == 0) {
            return "N/A";
        }
        return "1/" + value;
    }

    static String formatDuration(int ticks) {
        double secondsTotal = ticks * 0.6;
        double seconds = secondsTotal % 60.0;
        int minutes = (int) Math.floor((secondsTotal % 3600.0) / 60.0);
        int hours = (int) Math.floor(secondsTotal / 3600.0);
        return String.format("%02dh %02dm %04.1fs", hours, minutes, seconds);
    }
}