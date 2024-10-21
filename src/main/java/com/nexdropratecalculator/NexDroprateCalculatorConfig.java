package com.nexdropratecalculator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("nex-droprate-calculator")
public interface NexDroprateCalculatorConfig extends Config {

    @ConfigItem(
            position = 1,
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Toggle overlay"
    )
    default boolean showOverlay() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "DropRate",
            name = "Increase Drop Rate",
            description = "Only works if you believe!"
    )
    default boolean DropRate() {
        return true;
    }
}

