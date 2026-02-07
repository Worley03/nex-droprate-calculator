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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NexDroprateCalculatorOverlay extends OverlayPanel {
    public NexDroprateCalculatorRun currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.NOT_STARTED);
    private final NexDroprateCalculatorPlugin plugin;

    @Inject
    public NexDroprateCalculatorOverlay(NexDroprateCalculatorPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Nex Droprate Calculator Overlay");
    }

    // TODO: This is temporary until I figure out to deduplicate currentRun and use the same data between panel and overlay
    public void startCurrentRun(int playerCount) {
        this.currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.STARTED);
        this.currentRun.runPlayersMin = playerCount;
        this.currentRun.runPlayersMax = playerCount;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (currentRun.getRunState() != Utils.RUN_STATES.CANCELLED || true) { // TODO: For now ignore the if statement, probably drop it later
            // TODO: Implement better way of displaying players (account for players leaving mid-kill)
            String playersValue = String.valueOf(currentRun.runPlayersMin);
            double threshold = 100.0 / currentRun.runPlayersMin; // Calculate the threshold that determines qualifying for equal damage / mvp
            double contributionPercent = currentRun.getRunContributionPercent(); // Get the contribution percent

            // Convert necessary values to String
            String[] dropRate = {Utils.fraction(currentRun.getRunUniqueChanceRoll()), Utils.percent(contributionPercent), playersValue};
            String[] type = {"Drop Rate:", "Contribution:", "Players:"};

            // Determine the color for the contribution text
            Color contributionColor = contributionPercent > threshold ? Color.GREEN : Color.RED;

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Nex Calculator")
                    .color(Color.ORANGE)
                    .build());

            for (int i = 0; i < dropRate.length; i++) {
                Color leftColor = Color.WHITE;  // Default left color
                Color rightColor = (i == 1) ? contributionColor : Color.WHITE;  // Apply the color logic to "Contribution" text only

                panelComponent.getChildren().add(LineComponent.builder()
                        .left(type[i])
                        .right(dropRate[i])
                        .leftColor(leftColor)
                        .rightColor(rightColor)
                        .build());
            }

            return super.render(graphics);
        }
        return null; // Return null if the overlay should not be shown
    }
}
