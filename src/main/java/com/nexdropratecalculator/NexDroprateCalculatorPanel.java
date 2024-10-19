package com.nexdropratecalculator;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class NexDroprateCalculatorPanel extends PluginPanel {
  public NexDroprateCalculatorRun currentRun = new NexDroprateCalculatorRun();
  public NexDroprateCalculatorRun lastRun = new NexDroprateCalculatorRun();
  public NexDroprateCalculatorRun averageRun = new NexDroprateCalculatorRun();

  public JPanel runPanel;

  void init() {
    final Font font = FontManager.getDefaultFont();

    setLayout(new BorderLayout());
    setBackground(ColorScheme.DARK_GRAY_COLOR);
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // Run panel
    runPanel = new JPanel();
    runPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
    runPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    runPanel.setLayout(new GridLayout(0, 1));

    // Current run labels
    runPanel.add(new JLabel(labelTitle("- Current Run -")));
    ((JLabel) runPanel.getComponent(0)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Chance", "0.0000%")));
    ((JLabel) runPanel.getComponent(1)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Roll", "1/0")));
    ((JLabel) runPanel.getComponent(2)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Per.", "0.0000%")));
    ((JLabel) runPanel.getComponent(3)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Flat", "0/0")));
    ((JLabel) runPanel.getComponent(4)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Min.", "no")));
    ((JLabel) runPanel.getComponent(5)).setFont(font);
    runPanel.add(new JLabel(labelValue("Players", "0.00")));
    ((JLabel) runPanel.getComponent(6)).setFont(font);
    runPanel.add(new JLabel(labelValue("Is MVP", "no")));
    ((JLabel) runPanel.getComponent(7)).setFont(font);
    runPanel.add(new JLabel(labelValue("Time", "00h 00m 00s")));
    ((JLabel) runPanel.getComponent(8)).setFont(font);
    runPanel.add(new JLabel(labelState(0)));
    ((JLabel) runPanel.getComponent(9)).setFont(font);
    runPanel.add(new JLabel(labelEmpty()));
    ((JLabel) runPanel.getComponent(10)).setFont(font);

    // Last run labels
    runPanel.add(new JLabel(labelTitle("- Last Run -")));
    ((JLabel) runPanel.getComponent(11)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Chance", "0.0000%")));
    ((JLabel) runPanel.getComponent(12)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Roll", "1/0")));
    ((JLabel) runPanel.getComponent(13)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Per.", "0.0000%")));
    ((JLabel) runPanel.getComponent(14)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Flat", "0/0")));
    ((JLabel) runPanel.getComponent(15)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Min.", "no")));
    ((JLabel) runPanel.getComponent(16)).setFont(font);
    runPanel.add(new JLabel(labelValue("Players", "0.00")));
    ((JLabel) runPanel.getComponent(17)).setFont(font);
    runPanel.add(new JLabel(labelValue("Is MVP", "no")));
    ((JLabel) runPanel.getComponent(18)).setFont(font);
    runPanel.add(new JLabel(labelValue("Time", "00h 00m 00s")));
    ((JLabel) runPanel.getComponent(19)).setFont(font);
    runPanel.add(new JLabel(labelEmpty()));
    ((JLabel) runPanel.getComponent(20)).setFont(font);

    // Average run labels
    runPanel.add(new JLabel(labelTitle("- Average Runs -")));
    ((JLabel) runPanel.getComponent(21)).setFont(font);
    runPanel.add(new JLabel(labelValue("Total Runs", "0")));
    ((JLabel) runPanel.getComponent(22)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Chance", "0.0000%")));
    ((JLabel) runPanel.getComponent(23)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Roll", "1/0")));
    ((JLabel) runPanel.getComponent(24)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Per.", "0.0000%")));
    ((JLabel) runPanel.getComponent(25)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Flat", "0/0")));
    ((JLabel) runPanel.getComponent(26)).setFont(font);
    runPanel.add(new JLabel(labelValue("Contribution Min.", "0")));
    ((JLabel) runPanel.getComponent(27)).setFont(font);
    runPanel.add(new JLabel(labelValue("Players", "0.00")));
    ((JLabel) runPanel.getComponent(28)).setFont(font);
    runPanel.add(new JLabel(labelValue("MVP Per.", "0.0000%")));
    ((JLabel) runPanel.getComponent(29)).setFont(font);
    runPanel.add(new JLabel(labelValue("MVP Total", "0")));
    ((JLabel) runPanel.getComponent(30)).setFont(font);
    runPanel.add(new JLabel(labelValue("Time", "00h 00m 00s")));
    ((JLabel) runPanel.getComponent(31)).setFont(font);
    runPanel.add(new JLabel(labelValue("Runs / h", "0.00")));
    ((JLabel) runPanel.getComponent(32)).setFont(font);
    runPanel.add(new JLabel(labelValue("Unique Chance / h", "0.0000%")));
    ((JLabel) runPanel.getComponent(33)).setFont(font);
    runPanel.add(new JLabel(labelEmpty()));
    ((JLabel) runPanel.getComponent(34)).setFont(font);
    runPanel.add(new JButton(labelButton("Reset averages")));
    ((JButton) runPanel.getComponent(35)).setFont(font);
    ((JButton) runPanel.getComponent(35)).setFocusable(false);
    ((JButton) runPanel.getComponent(35))
        .addActionListener(
            e -> {
              averageRun.reset();
            });

    add(runPanel);
  }

  void deinit() {}

  void updateValues(
      int ownContribution,
      int totalContribution,
      int players,
      boolean isMVP,
      boolean minContribution,
      int state) {
    if (state == 1 && currentRun.getRunState() == 1) {
      // Collecting current run information
      currentRun.updateRun(
          ownContribution, totalContribution, players, isMVP, minContribution, false, false);
      currentRun.setRunState(state);
    } else if (state == 1 && currentRun.getRunState() != 1) {
      // New run starts
      if (currentRun.getRunState() == 0) lastRun.setFromRun(currentRun);
      currentRun.reset();
      currentRun.updateRun(
          ownContribution, totalContribution, players, isMVP, minContribution, true, false);
      currentRun.setRunState(state);
    } else if (state == 0 && currentRun.getRunState() == 1) {
      // Current run finished
      currentRun.updateRun(
          ownContribution, totalContribution, players, isMVP, minContribution, false, true);
      averageRun.addRun(currentRun);
      currentRun.setRunState(state);
    } else if (state == -1 && currentRun.getRunState() == 1) {
      // Current run canceled
      currentRun.setRunState(state);
    }

    ((JLabel) runPanel.getComponent(1))
        .setText(
            labelValue(
                "Unique Chance",
                String.format("%.4f", currentRun.getRunUniqueChancePercent()) + "%"));
    ((JLabel) runPanel.getComponent(2))
        .setText(
            labelValue("Unique Roll", "1/" + String.valueOf(currentRun.getRunUniqueChanceRoll())));
    ((JLabel) runPanel.getComponent(3))
        .setText(
            labelValue(
                "Contribution Per.",
                String.format("%.4f", currentRun.getRunContributionPercent()) + "%"));
    ((JLabel) runPanel.getComponent(4))
        .setText(
            labelValue(
                "Contribution Flat",
                String.valueOf(currentRun.getRunContributionFlatOwn())
                    + "/"
                    + String.valueOf(currentRun.getRunContributionFlatTotal())));
    ((JLabel) runPanel.getComponent(5))
        .setText(
            labelValue("Contribution Min.", currentRun.isRunMinimumContribution() ? "Yes" : "No"));
    ((JLabel) runPanel.getComponent(6))
        .setText(labelValue("Players", String.format("%.2f", currentRun.getRunPlayers())));
    ((JLabel) runPanel.getComponent(7))
        .setText(labelValue("Is MVP", currentRun.isRunIsMVP() ? "Yes" : "No"));
    ((JLabel) runPanel.getComponent(8)).setText(labelValue("Time", currentRun.getTime()));
    ((JLabel) runPanel.getComponent(9)).setText(labelState(currentRun.getRunState()));

    // Last run labels
    ((JLabel) runPanel.getComponent(12))
        .setText(
            labelValue(
                "Unique Chance", String.format("%.4f", lastRun.getRunUniqueChancePercent()) + "%"));
    ((JLabel) runPanel.getComponent(13))
        .setText(
            labelValue("Unique Roll", "1/" + String.valueOf(lastRun.getRunUniqueChanceRoll())));
    ((JLabel) runPanel.getComponent(14))
        .setText(
            labelValue(
                "Contribution Per.",
                String.format("%.4f", lastRun.getRunContributionPercent()) + "%"));
    ((JLabel) runPanel.getComponent(15))
        .setText(
            labelValue(
                "Contribution Flat",
                String.valueOf(lastRun.getRunContributionFlatOwn())
                    + "/"
                    + String.valueOf(lastRun.getRunContributionFlatTotal())));
    ((JLabel) runPanel.getComponent(16))
        .setText(
            labelValue("Contribution Min.", lastRun.isRunMinimumContribution() ? "Yes" : "No"));
    ((JLabel) runPanel.getComponent(17))
        .setText(labelValue("Players", String.format("%.2f", lastRun.getRunPlayers())));
    ((JLabel) runPanel.getComponent(18))
        .setText(labelValue("Is MVP", lastRun.isRunIsMVP() ? "Yes" : "No"));
    ((JLabel) runPanel.getComponent(19)).setText(labelValue("Time", lastRun.getTime()));

    // Average run labels
    ((JLabel) runPanel.getComponent(22))
        .setText(labelValue("Total Runs", String.valueOf(averageRun.getRunAverageTotal())));
    ((JLabel) runPanel.getComponent(23))
        .setText(
            labelValue(
                "Unique Chance",
                String.format("%.4f", averageRun.getRunAverageUniqueChancePercent()) + "%"));
    ((JLabel) runPanel.getComponent(24))
        .setText(
            labelValue(
                "Unique Roll", "1/" + String.valueOf(averageRun.getRunAverageUniqueChanceRoll())));
    ((JLabel) runPanel.getComponent(25))
        .setText(
            labelValue(
                "Contribution Per.",
                String.format("%.4f", averageRun.getRunAverageContributionPercent()) + "%"));
    ((JLabel) runPanel.getComponent(26))
        .setText(
            labelValue(
                "Contribution Flat",
                String.valueOf(averageRun.getRunAverageContributionFlatOwn())
                    + "/"
                    + String.valueOf(averageRun.getRunAverageContributionFlatTotal())));
    ((JLabel) runPanel.getComponent(27))
        .setText(
            labelValue(
                "Contribution Min.",
                String.valueOf(averageRun.getRunAverageContributionMinimumDamageTotal())));
    ((JLabel) runPanel.getComponent(28))
        .setText(labelValue("Players", String.format("%.2f", averageRun.getRunAveragePlayers())));
    ((JLabel) runPanel.getComponent(29))
        .setText(
            labelValue(
                "MVP Per.", String.format("%.4f", averageRun.getRunAverageIsMVPPercent()) + "%"));
    ((JLabel) runPanel.getComponent(30))
        .setText(labelValue("MVP Total", String.valueOf(averageRun.getRunAverageIsMVPTotal())));
    ((JLabel) runPanel.getComponent(31))
        .setText(labelValue("Time", averageRun.getRunAverageTime()));
    ((JLabel) runPanel.getComponent(32))
        .setText(
            labelValue("Runs / h", String.format("%.2f", averageRun.getRunAverageRunsPerHour())));
    ((JLabel) runPanel.getComponent(33))
        .setText(
            labelValue(
                "Unique Chance / h",
                String.format("%.4f", averageRun.getRunAverageUniqueChancePercentPerHour()) + "%"));
  }

  private static String labelTitle(String text) {
    return "<html><body><span style='color: #7d7d7d'>" + text + "</span></body></html>";
  }

  private static String labelValue(String name, String value) {
    return "<html><body><span style='color: #b3b3b3'>"
        + name
        + ": "
        + "</span><span style='color: #ffffff'>"
        + value
        + "</span></body></html>";
  }

  private static String labelButton(String name) {
    return "<html><body><span style='color: #ffffff'>" + name + "</span></body></html>";
  }

  private static String labelEmpty() {
    return "<html><body><span></span></body></html>";
  }

  private static String labelState(int state) {
    if (state < 0) {
      return "<html><body><span style='color: #b3b3b3'>Run: </span><span style='color:yellow'>Canceled</span></body></html>";
    } else if (state > 0) {
      return "<html><body><span style='color: #b3b3b3'>Run: </span><span style='color:green'>Active</span></body></html>";
    } else {
      return "<html><body><span style='color: #b3b3b3'>Run: </span><span style='color:red'>Not Active</span></body></html>";
    }
  }
}
