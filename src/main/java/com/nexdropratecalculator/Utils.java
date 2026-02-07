package com.nexdropratecalculator;

import lombok.AllArgsConstructor;

public class Utils {
     static String fraction(int value) {
        if (value == 0) {
            return "N/A";
        }
        return "1/" + value;
    }

    static String percent(double value) {
        return String.format("%.1f%%", value);
    }

    static String formatDuration(int ticks) {
        int secondsTotal = (int) Math.ceil((double) ticks * 0.6);
        int seconds = secondsTotal % 60;
        int minutes = (int) Math.floor((double) secondsTotal / 60.0);
        int hours = (int) Math.floor((double) secondsTotal / 3600.0);
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    @AllArgsConstructor
    public enum RUN_STATES {
        // Fight started
        STARTED("Active"),
        // Fight was started earlier and just concluded
        FINISHED("Finished"),
        // Fight was ongoing but player left
        // Cancelled runs will be ignored from adding into the average and being set to lastRun
        CANCELLED("Cancelled"),
        // Fight has not been started yet (default state for object initialization)
        NOT_STARTED("Not Started");

        final String displayText;

        @Override
        public String toString() {
            return this.displayText;
        }
    }
}