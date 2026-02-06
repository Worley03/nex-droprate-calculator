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
    enum RUN_STATES {
        STARTING("Starting"),       // fight started
        STARTED_ONGOING("Active"),  // fight is currently ongoing, started before
        FINISHED("Finished"),       // fight was started earlier and just concluded
        CANCELLED("Cancelled"),     // fight was ongoing but player left
        NOT_STARTED("Not Started"); // fight has not been started yet (default state)

        final String displayText;

        @Override
        public String toString() {
            return this.displayText;
        }
    }
}