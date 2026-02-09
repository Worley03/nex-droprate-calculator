package com.nexdropratecalculator;

public class Utils {
     static String fraction(int value) {
        if (value == 0) {
            return "N/A";
        }
        return "1/" + value;
    }

    static String formatDuration(int ticks) {
        int secondsTotal = (int) Math.ceil((double) ticks * 0.6);
        int seconds = secondsTotal % 60;
        int minutes = (int) Math.floor((double) secondsTotal / 60.0);
        int hours = (int) Math.floor((double) secondsTotal / 3600.0);
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}