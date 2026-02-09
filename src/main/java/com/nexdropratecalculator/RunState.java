package com.nexdropratecalculator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RunState {
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