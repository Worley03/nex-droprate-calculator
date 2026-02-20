package com.nexdropratecalculator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NexDroprateCalculatorConfig.GROUP)
public interface NexDroprateCalculatorConfig extends Config {
    String GROUP = "nex-droprate-calculator";

    String SHOW_OVERLAY = "showOverlay";
    String DROP_RATE = "DropRate";
    String LOG_KILLS = "logKills";

    @ConfigItem(
            position = 1,
            keyName = SHOW_OVERLAY,
            name = "Show Overlay",
            description = "Toggle overlay"
    )
    default boolean showOverlay() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = DROP_RATE,
            name = "Increase Drop Rate",
            description = "Only works if you believe!"
    )
    default boolean DropRate() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = LOG_KILLS,
            name = "Log Kills",
            description = "Log kill data to .txt file in .runelite folder"
    )
    default boolean logKills() {
        return false;
    }
}

