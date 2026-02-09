package com.nexdropratecalculator;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import lombok.Getter;
import lombok.Setter;

public class NexDroprateCalculatorOverlay extends OverlayPanel {
    private final NexDroprateCalculatorPlugin plugin;
    private final NexDroprateCalculatorConfig config;
    @Getter @Setter
    private boolean shouldRender = false;

    @Inject
    public NexDroprateCalculatorOverlay(NexDroprateCalculatorPlugin plugin, NexDroprateCalculatorConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        // Add an option to Shift+Right click's context menu which can take the user to plugin settings
        addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Configure Nex Droprate Calculator");
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showOverlay() || !shouldRender) {
            return null;
        }

        NexDroprateCalculatorRun run = plugin.panel.currentRun;

        // Title
        panelComponent.getChildren().add(TitleComponent.builder().text("Nex Calculator").color(Color.ORANGE).build());

        // Drop rate
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Drop Rate:").right(Utils.fraction(run.getUniqueChanceRoll()))
                .leftColor(Color.WHITE).rightColor(Color.WHITE).build());
        // Contribution
        double contributionPercent = run.getContributionPercent();
        double threshold = 100.0 / run.calculateAvgPlayers(); // Threshold determines qualifying for equal damage / mvp
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Contribution:").right(String.format("%.1f%%", contributionPercent))
                .leftColor(Color.WHITE).rightColor(contributionPercent > threshold ? Color.GREEN : Color.RED).build());
        // Players
        final int playerCount = run.getPlayersCurrent();
        String playersValue = String.format("%d", playerCount);
        // Someone might have left after the fight started, account for that and display original player count as well
        if (playerCount < run.getPlayersMax()) {
            playersValue = String.format("%d out of %d", playerCount, run.getPlayersMax());
        }
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Players:").right(playersValue)
                .leftColor(Color.WHITE).rightColor(Color.WHITE).build());

        return super.render(graphics);
    }
}