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
    public NexDroprateCalculatorRun currentRun = new NexDroprateCalculatorRun();
    private final NexDroprateCalculatorPlugin plugin;
    private int playersCount = 0;

    void updateValues(
            int ownContribution,
            int totalContribution,
            int players,
            boolean isMVP,
            boolean minContribution,
            int state) {

        this.playersCount = players;

        if (state == 1 && currentRun.getRunState() == 1) {
            // Collecting current run information
            currentRun.updateRun(
                    ownContribution, totalContribution, players, isMVP, minContribution, false, false);
            currentRun.setRunState(state);
        } else if (state == 1 && currentRun.getRunState() != 1) {
            // New run starts
            if (currentRun.getRunState() == 0);
            currentRun.reset();
            currentRun.updateRun(
                    ownContribution, totalContribution, players, isMVP, minContribution, true, false);
            currentRun.setRunState(state);
        } else if (state == 0 && currentRun.getRunState() == 1) {
            // Current run finished
            currentRun.updateRun(
                    ownContribution, totalContribution, players, isMVP, minContribution, false, true);
            currentRun.setRunState(state);
        } else if (state == -1 && currentRun.getRunState() == 1) {
            // Current run canceled
            currentRun.setRunState(state);
        }
    }

    @Inject
    public NexDroprateCalculatorOverlay(NexDroprateCalculatorPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Nex Droprate Calculator Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (currentRun.getRunState() != -1) {

            String playersValue = String.valueOf(playersCount); // Convert playersCount to String
            String[] dropRate = {(fraction(currentRun.getRunUniqueChanceRoll())), percent(currentRun.getRunContributionPercent()), playersValue};
            String[] type = {"Drop Rate:", "Contribution:", "Players:"};

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Nex Calculator")
                    .color(Color.ORANGE)
                    .build());

            for (int i = 0; i < dropRate.length; i++) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(type[i])
                        .right(dropRate[i])
                        .leftColor(Color.WHITE)
                        .rightColor(Color.WHITE)
                        .build());
            }
            return super.render(graphics);
        }
        return null; // Return null if the overlay should not be shown
    }

    private String percent(double value) {
        return String.format("%.1f%%", value);
    }

    private String fraction(int value) {
        if (value == 0) {
            return "N/A";
        }
        int denominator = (value);
        return "1/" + denominator;
    }
}
