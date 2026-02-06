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