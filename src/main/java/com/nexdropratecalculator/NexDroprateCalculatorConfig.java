package com.nexdropratecalculator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NexDroprateCalculatorConfig.GROUP)
public interface NexDroprateCalculatorConfig extends Config {
    String GROUP = "nex-droprate-calculator";

    String SHOW_OVERLAY = "showOverlay";
    String SHOW_DAMAGE = "showDamage";
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
            keyName = SHOW_DAMAGE,
            name = "Show Damage",
            description = "Show your damage / total damage alongside contribution in the overlay"
    )
    default boolean showDamage() {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = DROP_RATE,
            name = "Increase Drop Rate",
            description = "Only works if you believe!"
    )
    default boolean DropRate() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = LOG_KILLS,
            name = "Log Kills",
            description = "Log kills data to the nex-droprate-calculator folder within the .runelite directory"
    )
    default boolean logKills() {
        return false;
    }
}

