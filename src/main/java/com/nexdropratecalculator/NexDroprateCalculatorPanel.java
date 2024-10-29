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

  // Persist the components in fields
  private JLabel uniqueChanceLabel;
  private JLabel uniqueRollLabel;
  private JLabel contributionPercentLabel;
  private JLabel contributionFlatLabel;
  private JLabel playersLabel;
  private JLabel isMVPLabel;
  private JLabel timeLabel;
  private JLabel stateLabel;

  private JLabel lastUniqueChanceLabel;
  private JLabel lastUniqueRollLabel;
  private JLabel lastContributionPercentLabel;
  private JLabel lastContributionFlatLabel;
  private JLabel lastPlayersLabel;
  private JLabel lastIsMVPLabel;
  private JLabel lastTimeLabel;

  private JLabel totalKillsLabel;
  private JLabel averageUniqueChanceLabel;
  private JLabel averageUniqueRollLabel;
  private JLabel averageContributionPercentLabel;
  private JLabel averageContributionFlatLabel;
  private JLabel averagePlayersLabel;
  private JLabel averageMVPPercentLabel;
  private JLabel averageMVPTotalLabel;
  private JLabel averageTimeLabel;
  private JLabel killsPerHourLabel;
  private JLabel uniqueChancePerHourLabel;
  private JLabel uniqueRollCombined;

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
    JLabel currentKillLabel = new JLabel(labelTitle("- Current Kill -"));
    currentKillLabel.setFont(font);
    runPanel.add(currentKillLabel);

    uniqueChanceLabel = new JLabel(labelValue("Unique Chance", "0.0000%"));
    uniqueChanceLabel.setFont(font);
    runPanel.add(uniqueChanceLabel);

    uniqueRollLabel = new JLabel(labelValue("Unique Roll", "N/A"));
    uniqueRollLabel.setFont(font);
    runPanel.add(uniqueRollLabel);

    contributionPercentLabel = new JLabel(labelValue("Contribution", "0.00%"));
    contributionPercentLabel.setFont(font);
    runPanel.add(contributionPercentLabel);

    contributionFlatLabel = new JLabel(labelValue("Damage", "0/0"));
    contributionFlatLabel.setFont(font);
    runPanel.add(contributionFlatLabel);

    playersLabel = new JLabel(labelValue("Players", "0.00"));
    playersLabel.setFont(font);
    runPanel.add(playersLabel);

    isMVPLabel = new JLabel(labelValue("MVP", "no"));
    isMVPLabel.setFont(font);
    runPanel.add(isMVPLabel);

    timeLabel = new JLabel(labelValue("Time", "00h 00m 00s"));
    timeLabel.setFont(font);
    runPanel.add(timeLabel);

    stateLabel = new JLabel(labelState(0));
    stateLabel.setFont(font);
    runPanel.add(stateLabel);

    JLabel emptyLabel = new JLabel(labelEmpty());
    emptyLabel.setFont(font);
    runPanel.add(emptyLabel);

    // Last run labels
    JLabel lastKillLabel = new JLabel(labelTitle("- Last Kill -"));
    lastKillLabel.setFont(font);
    runPanel.add(lastKillLabel);

    lastUniqueChanceLabel = new JLabel(labelValue("Unique Chance", "0.0000%"));
    lastUniqueChanceLabel.setFont(font);
    runPanel.add(lastUniqueChanceLabel);

    lastUniqueRollLabel = new JLabel(labelValue("Unique Roll", "N/A"));
    lastUniqueRollLabel.setFont(font);
    runPanel.add(lastUniqueRollLabel);

    lastContributionPercentLabel = new JLabel(labelValue("Contribution", "0.00%"));
    lastContributionPercentLabel.setFont(font);
    runPanel.add(lastContributionPercentLabel);

    lastContributionFlatLabel = new JLabel(labelValue("Damage", "0/0"));
    lastContributionFlatLabel.setFont(font);
    runPanel.add(lastContributionFlatLabel);

    lastPlayersLabel = new JLabel(labelValue("Players", "0.00"));
    lastPlayersLabel.setFont(font);
    runPanel.add(lastPlayersLabel);

    lastIsMVPLabel = new JLabel(labelValue("MVP", "no"));
    lastIsMVPLabel.setFont(font);
    runPanel.add(lastIsMVPLabel);

    lastTimeLabel = new JLabel(labelValue("Time", "00h 00m 00s"));
    lastTimeLabel.setFont(font);
    runPanel.add(lastTimeLabel);

    JLabel lastEmptyLabel = new JLabel(labelEmpty());
    lastEmptyLabel.setFont(font);
    runPanel.add(lastEmptyLabel);

    // Average run labels
    JLabel averageKillLabel = new JLabel(labelTitle("- Averages -"));
    averageKillLabel.setFont(font);
    runPanel.add(averageKillLabel);

    totalKillsLabel = new JLabel(labelValue("Total Kills", "0"));
    totalKillsLabel.setFont(font);
    runPanel.add(totalKillsLabel);

    averageUniqueChanceLabel = new JLabel(labelValue("Unique Chance", "0.0000%"));
    averageUniqueChanceLabel.setFont(font);
    runPanel.add(averageUniqueChanceLabel);

    averageUniqueRollLabel = new JLabel(labelValue("Unique Roll", "N/A"));
    averageUniqueRollLabel.setFont(font);
    runPanel.add(averageUniqueRollLabel);

    averageContributionPercentLabel = new JLabel(labelValue("Contribution", "0.00%"));
    averageContributionPercentLabel.setFont(font);
    runPanel.add(averageContributionPercentLabel);

    averageContributionFlatLabel = new JLabel(labelValue("Damage", "0/0"));
    averageContributionFlatLabel.setFont(font);
    runPanel.add(averageContributionFlatLabel);

    averagePlayersLabel = new JLabel(labelValue("Players", "0.00"));
    averagePlayersLabel.setFont(font);
    runPanel.add(averagePlayersLabel);

    averageMVPPercentLabel = new JLabel(labelValue("MVP", "0.00%"));
    averageMVPPercentLabel.setFont(font);
    runPanel.add(averageMVPPercentLabel);

    averageMVPTotalLabel = new JLabel(labelValue("MVP Total", "0"));
    averageMVPTotalLabel.setFont(font);
    runPanel.add(averageMVPTotalLabel);

    averageTimeLabel = new JLabel(labelValue("Time", "00h 00m 00s"));
    averageTimeLabel.setFont(font);
    runPanel.add(averageTimeLabel);

    killsPerHourLabel = new JLabel(labelValue("Kills / hr", "0.00"));
    killsPerHourLabel.setFont(font);
    runPanel.add(killsPerHourLabel);

    uniqueChancePerHourLabel = new JLabel(labelValue("Unique Chance / hr", "0.0000%"));
    uniqueChancePerHourLabel.setFont(font);
    runPanel.add(uniqueChancePerHourLabel);

    uniqueRollCombined = new JLabel(labelValue("Combined Unique Roll", "N/A"));
    uniqueRollCombined.setFont(font);
    runPanel.add(uniqueRollCombined);

    JLabel averageEmptyLabel = new JLabel(labelEmpty());
    averageEmptyLabel.setFont(font);
    runPanel.add(averageEmptyLabel);

    JButton resetButton = new JButton(labelButton("Reset Averages"));
    resetButton.setFont(font);
    resetButton.setFocusable(false);
    resetButton.addActionListener(e -> {
      averageRun.reset();
      RestButtonUpdate();
    });
    runPanel.add(resetButton);

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

    uniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", currentRun.getRunUniqueChancePercent()) + "%"));
    uniqueRollLabel.setText(labelValue("Unique Roll", fraction(currentRun.getRunUniqueChanceRoll())));
    contributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", currentRun.getRunContributionPercent()) + "%"));
    contributionFlatLabel.setText(labelValue("Damage", currentRun.getRunContributionFlatOwn() + "/" + currentRun.getRunContributionFlatTotal()));
    playersLabel.setText(labelValue("Players", String.format("%.2f", currentRun.getRunPlayers())));
    isMVPLabel.setText(labelValue("MVP", (currentRun.isRunIsMVP() ? "yes" : "no")));
    timeLabel.setText(labelValue("Time", currentRun.getTime()));
    stateLabel.setText(labelState(currentRun.getRunState()));

    // Last run
    lastUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", lastRun.getRunUniqueChancePercent()) + "%"));
    lastUniqueRollLabel.setText(labelValue("Unique Roll", fraction(lastRun.getRunUniqueChanceRoll())));
    lastContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", lastRun.getRunContributionPercent()) + "%"));
    lastContributionFlatLabel.setText(labelValue("Damage", lastRun.getRunContributionFlatOwn() + "/" + lastRun.getRunContributionFlatTotal()));
    lastPlayersLabel.setText(labelValue("Players", String.format("%.2f", lastRun.getRunPlayers())));
    lastIsMVPLabel.setText(labelValue("MVP", (lastRun.isRunIsMVP() ? "yes" : "no")));
    lastTimeLabel.setText(labelValue("Time", lastRun.getTime()));

    // Average run
    totalKillsLabel.setText(labelValue("Total Kills", String.valueOf(averageRun.getRunAverageTotal())));
    averageUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", averageRun.getRunAverageUniqueChancePercent()) + "%"));
    averageUniqueRollLabel.setText(labelValue("Unique Roll", fraction(averageRun.getRunAverageUniqueChanceRoll())));
    averageContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", averageRun.getRunAverageContributionPercent()) + "%"));
    averageContributionFlatLabel.setText(labelValue("Damage", averageRun.getRunAverageContributionFlatOwn() + "/" + averageRun.getRunAverageContributionFlatTotal()));
    averagePlayersLabel.setText(labelValue("Players", String.format("%.2f", averageRun.getRunAveragePlayers())));
    averageMVPPercentLabel.setText(labelValue("MVP", String.format("%.2f", (averageRun.getRunAverageIsMVPPercent()) * 100) + "%"));
    averageMVPTotalLabel.setText(labelValue("MVP Total", String.valueOf(averageRun.getRunAverageIsMVPTotal())));
    averageTimeLabel.setText(labelValue("Time", averageRun.getRunAverageTime()));
    killsPerHourLabel.setText(labelValue("Kills / hr", String.format("%.2f", averageRun.getRunAverageRunsPerHour())));
    uniqueChancePerHourLabel.setText(labelValue("Unique Chance / hr", String.format("%.4f", averageRun.getRunAverageUniqueChancePercentPerHour()) + "%"));

    int total = averageRun.getRunAverageTotal();
    if (total != 0) {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", String.format(fraction(averageRun.getRunAverageUniqueChanceRoll() / total))));
    } else {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", "N/A"));  // Or any appropriate fallback message
    }
  }

  void RestButtonUpdate() {
    totalKillsLabel.setText(labelValue("Total Kills", String.valueOf(averageRun.getRunAverageTotal())));
    averageUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", averageRun.getRunAverageUniqueChancePercent()) + "%"));
    averageUniqueRollLabel.setText(labelValue("Unique Roll", fraction(averageRun.getRunAverageUniqueChanceRoll())));
    averageContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", averageRun.getRunAverageContributionPercent()) + "%"));
    averageContributionFlatLabel.setText(labelValue("Damage", averageRun.getRunAverageContributionFlatOwn() + "/" + averageRun.getRunAverageContributionFlatTotal()));
    averagePlayersLabel.setText(labelValue("Players", String.format("%.2f", averageRun.getRunAveragePlayers())));
    averageMVPPercentLabel.setText(labelValue("MVP", String.format("%.2f", (averageRun.getRunAverageIsMVPPercent()) * 100) + "%"));
    averageMVPTotalLabel.setText(labelValue("MVP Total", String.valueOf(averageRun.getRunAverageIsMVPTotal())));
    averageTimeLabel.setText(labelValue("Time", averageRun.getRunAverageTime()));
    killsPerHourLabel.setText(labelValue("Kills / hr", String.format("%.2f", averageRun.getRunAverageRunsPerHour())));
    uniqueChancePerHourLabel.setText(labelValue("Unique Chance / hr", String.format("%.4f", averageRun.getRunAverageUniqueChancePercentPerHour()) + "%"));

    int total = averageRun.getRunAverageTotal();
    if (total != 0) {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", String.format(fraction(averageRun.getRunAverageUniqueChanceRoll() / total))));
    } else {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", "N/A"));  // Or any appropriate fallback message
    }
  }

  private String labelTitle(String title) {
    return "<html><div style='color:white;font-size:10px;'>" + title + "</div></html>";
  }

  private String labelValue(String name, String value) {
    return "<html><div style='color:white;font-size:10px;'><b>" + name + ": </b>" + value + "</div></html>";
  }

  private String labelState(int state) {
    String stateStr;
    switch (state) {
      case 1:
        stateStr = "Active";
        break;
      case -1:
        stateStr = "Cancelled";
        break;
      default:
        stateStr = "Inactive";
    }

    return "<html><div style='color:white;font-size:10px;'><b>State: </b>" + stateStr + "</div></html>";
  }

  private String labelEmpty() {
    return "<html><div style='color:white;font-size:10px;'><b></b></div></html>";
  }

  private String labelButton(String text) {
    return "<html><div style='color:white;font-size:10px;'>" + text + "</div></html>";
  }

  private String fraction(int value) {
    if (value == 0) {
      return "N/A";
    }
    int denominator = (value);
    return "1/" + denominator;
  }
}