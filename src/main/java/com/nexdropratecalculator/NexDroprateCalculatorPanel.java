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
    JLabel currentKillLabel = new JLabel(labelTitle("- Current Kill -"));
    currentKillLabel.setFont(font);
    runPanel.add(currentKillLabel);

    JLabel uniqueChanceLabel = new JLabel(labelValue("Unique Chance", "0.0000%"));
    uniqueChanceLabel.setFont(font);
    runPanel.add(uniqueChanceLabel);

    JLabel uniqueRollLabel = new JLabel(labelValue("Unique Roll", "N/A"));
    uniqueRollLabel.setFont(font);
    runPanel.add(uniqueRollLabel);

    JLabel contributionPercentLabel = new JLabel(labelValue("Contribution Per.", "0.0%"));
    contributionPercentLabel.setFont(font);
    runPanel.add(contributionPercentLabel);

    JLabel contributionFlatLabel = new JLabel(labelValue("Contribution Flat", "0/0"));
    contributionFlatLabel.setFont(font);
    runPanel.add(contributionFlatLabel);

    JLabel contributionMinLabel = new JLabel(labelValue("Contribution Min.", "no"));
    contributionMinLabel.setFont(font);
    runPanel.add(contributionMinLabel);

    JLabel playersLabel = new JLabel(labelValue("Players", "0.00"));
    playersLabel.setFont(font);
    runPanel.add(playersLabel);

    JLabel isMVPLabel = new JLabel(labelValue("MVP", "no"));
    isMVPLabel.setFont(font);
    runPanel.add(isMVPLabel);

    JLabel timeLabel = new JLabel(labelValue("Time", "00h 00m 00s"));
    timeLabel.setFont(font);
    runPanel.add(timeLabel);

    JLabel stateLabel = new JLabel(labelState(0));
    stateLabel.setFont(font);
    runPanel.add(stateLabel);

    JLabel emptyLabel = new JLabel(labelEmpty());
    emptyLabel.setFont(font);
    runPanel.add(emptyLabel);

    // Last run labels
    JLabel lastKillLabel = new JLabel(labelTitle("- Last Kill -"));
    lastKillLabel.setFont(font);
    runPanel.add(lastKillLabel);

    JLabel lastUniqueChanceLabel = new JLabel(labelValue("Unique Chance", "0.0000%"));
    lastUniqueChanceLabel.setFont(font);
    runPanel.add(lastUniqueChanceLabel);

    JLabel lastUniqueRollLabel = new JLabel(labelValue("Unique Roll", "N/A"));
    lastUniqueRollLabel.setFont(font);
    runPanel.add(lastUniqueRollLabel);

    JLabel lastContributionPercentLabel = new JLabel(labelValue("Contribution Per.", "0.0%"));
    lastContributionPercentLabel.setFont(font);
    runPanel.add(lastContributionPercentLabel);

    JLabel lastContributionFlatLabel = new JLabel(labelValue("Contribution Flat", "0/0"));
    lastContributionFlatLabel.setFont(font);
    runPanel.add(lastContributionFlatLabel);

    JLabel lastContributionMinLabel = new JLabel(labelValue("Contribution Min.", "no"));
    lastContributionMinLabel.setFont(font);
    runPanel.add(lastContributionMinLabel);

    JLabel lastPlayersLabel = new JLabel(labelValue("Players", "0.00"));
    lastPlayersLabel.setFont(font);
    runPanel.add(lastPlayersLabel);

    JLabel lastIsMVPLabel = new JLabel(labelValue("MVP", "no"));
    lastIsMVPLabel.setFont(font);
    runPanel.add(lastIsMVPLabel);

    JLabel lastTimeLabel = new JLabel(labelValue("Time", "00h 00m 00s"));
    lastTimeLabel.setFont(font);
    runPanel.add(lastTimeLabel);

    JLabel lastEmptyLabel = new JLabel(labelEmpty());
    lastEmptyLabel.setFont(font);
    runPanel.add(lastEmptyLabel);

    // Average run labels
    JLabel averageKillLabel = new JLabel(labelTitle("- Averages -"));
    averageKillLabel.setFont(font);
    runPanel.add(averageKillLabel);

    JLabel totalKillsLabel = new JLabel(labelValue("Total Kills", "0"));
    totalKillsLabel.setFont(font);
    runPanel.add(totalKillsLabel);

    JLabel averageUniqueChanceLabel = new JLabel(labelValue("Unique Chance", "0.0000%"));
    averageUniqueChanceLabel.setFont(font);
    runPanel.add(averageUniqueChanceLabel);

    JLabel averageUniqueRollLabel = new JLabel(labelValue("Unique Roll", "N/A"));
    averageUniqueRollLabel.setFont(font);
    runPanel.add(averageUniqueRollLabel);

    JLabel averageContributionPercentLabel = new JLabel(labelValue("Contribution Per.", "0.0%"));
    averageContributionPercentLabel.setFont(font);
    runPanel.add(averageContributionPercentLabel);

    JLabel averageContributionFlatLabel = new JLabel(labelValue("Contribution Flat", "0/0"));
    averageContributionFlatLabel.setFont(font);
    runPanel.add(averageContributionFlatLabel);

    JLabel averageContributionMinLabel = new JLabel(labelValue("Contribution Min.", "0"));
    averageContributionMinLabel.setFont(font);
    runPanel.add(averageContributionMinLabel);

    JLabel averagePlayersLabel = new JLabel(labelValue("Players", "0.00"));
    averagePlayersLabel.setFont(font);
    runPanel.add(averagePlayersLabel);

    JLabel averageMVPPercentLabel = new JLabel(labelValue("MVP Per.", "0.0000%"));
    averageMVPPercentLabel.setFont(font);
    runPanel.add(averageMVPPercentLabel);

    JLabel averageMVPTotalLabel = new JLabel(labelValue("MVP Total", "0"));
    averageMVPTotalLabel.setFont(font);
    runPanel.add(averageMVPTotalLabel);

    JLabel averageTimeLabel = new JLabel(labelValue("Time", "00h 00m 00s"));
    averageTimeLabel.setFont(font);
    runPanel.add(averageTimeLabel);

    JLabel killsPerHourLabel = new JLabel(labelValue("Kills / hr", "0.00"));
    killsPerHourLabel.setFont(font);
    runPanel.add(killsPerHourLabel);

    JLabel uniqueChancePerHourLabel = new JLabel(labelValue("Unique Chance / hr", "0.0000%"));
    uniqueChancePerHourLabel.setFont(font);
    runPanel.add(uniqueChancePerHourLabel);

    JLabel uniqueRollCombined = new JLabel(labelValue("Combined Unique Roll", "N/A"));
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

    JLabel uniqueChanceLabel = (JLabel) runPanel.getComponent(1);
    uniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.2f", currentRun.getRunUniqueChancePercent()) + "%"));

    JLabel uniqueRollLabel = (JLabel) runPanel.getComponent(2);
    uniqueRollLabel.setText(labelValue("Unique Roll", fraction(currentRun.getRunUniqueChanceRoll())));

    JLabel contributionPercentLabel = (JLabel) runPanel.getComponent(3);
    contributionPercentLabel.setText(labelValue("Contribution Per.", String.format("%.2f", currentRun.getRunContributionPercent()) + "%"));

    JLabel contributionFlatLabel = (JLabel) runPanel.getComponent(4);
    contributionFlatLabel.setText(labelValue("Contribution Flat", currentRun.getRunContributionFlatOwn() + "/" + currentRun.getRunContributionFlatTotal()));

    JLabel contributionMinLabel = (JLabel) runPanel.getComponent(5);
    contributionMinLabel.setText(labelValue("Contribution Min.", (currentRun.isRunMinimumContribution() ? "yes" : "no")));

    JLabel playersLabel = (JLabel) runPanel.getComponent(6);
    playersLabel.setText(labelValue("Players", String.format("%.2f", currentRun.getRunPlayers())));

    JLabel isMVPLabel = (JLabel) runPanel.getComponent(7);
    isMVPLabel.setText(labelValue("MVP", (currentRun.isRunIsMVP() ? "yes" : "no")));

    JLabel timeLabel = (JLabel) runPanel.getComponent(8);
    timeLabel.setText(labelValue("Time", currentRun.getTime()));

    JLabel stateLabel = (JLabel) runPanel.getComponent(9);
    stateLabel.setText(labelState(currentRun.getRunState()));

    // Last run
    JLabel lastUniqueChanceLabel = (JLabel) runPanel.getComponent(12);
    lastUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", lastRun.getRunUniqueChancePercent()) + "%"));

    JLabel lastUniqueRollLabel = (JLabel) runPanel.getComponent(13);
    lastUniqueRollLabel.setText(labelValue("Unique Roll", fraction(lastRun.getRunUniqueChanceRoll())));

    JLabel lastContributionPercentLabel = (JLabel) runPanel.getComponent(14);
    lastContributionPercentLabel.setText(labelValue("Contribution Per.", String.format("%.2f", lastRun.getRunContributionPercent()) + "%"));

    JLabel lastContributionFlatLabel = (JLabel) runPanel.getComponent(15);
    lastContributionFlatLabel.setText(labelValue("Contribution Flat", lastRun.getRunContributionFlatOwn() + "/" + lastRun.getRunContributionFlatTotal()));

    JLabel lastContributionMinLabel = (JLabel) runPanel.getComponent(16);
    lastContributionMinLabel.setText(labelValue("Contribution Min.", (lastRun.isRunMinimumContribution() ? "yes" : "no")));

    JLabel lastPlayersLabel = (JLabel) runPanel.getComponent(17);
    lastPlayersLabel.setText(labelValue("Players", String.format("%.2f", lastRun.getRunPlayers())));

    JLabel lastIsMVPLabel = (JLabel) runPanel.getComponent(18);
    lastIsMVPLabel.setText(labelValue("MVP", (lastRun.isRunIsMVP() ? "yes" : "no")));

    JLabel lastTimeLabel = (JLabel) runPanel.getComponent(19);
    lastTimeLabel.setText(labelValue("Time", lastRun.getTime()));

    // Average run
    JLabel totalKillsLabel = (JLabel) runPanel.getComponent(22);
    totalKillsLabel.setText(labelValue("Total Kills", String.valueOf(averageRun.getRunAverageTotal())));

    JLabel averageUniqueChanceLabel = (JLabel) runPanel.getComponent(23);
    averageUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", averageRun.getRunAverageUniqueChancePercent()) + "%"));

    JLabel averageUniqueRollLabel = (JLabel) runPanel.getComponent(24);
    averageUniqueRollLabel.setText(labelValue("Unique Roll", fraction(averageRun.getRunAverageUniqueChanceRoll())));

    JLabel averageContributionPercentLabel = (JLabel) runPanel.getComponent(25);
    averageContributionPercentLabel.setText(labelValue("Contribution Per.", String.format("%.2f", averageRun.getRunAverageContributionPercent()) + "%"));

    JLabel averageContributionFlatLabel = (JLabel) runPanel.getComponent(26);
    averageContributionFlatLabel.setText(labelValue("Contribution Flat", averageRun.getRunAverageContributionFlatOwn() + "/" + averageRun.getRunAverageContributionFlatOwn()));

    JLabel averageContributionMinLabel = (JLabel) runPanel.getComponent(27);
    averageContributionMinLabel.setText(labelValue("Contribution Min.", String.valueOf(averageRun.getRunAverageContributionMinimumDamageTotal())));

    JLabel averagePlayersLabel = (JLabel) runPanel.getComponent(28);
    averagePlayersLabel.setText(labelValue("Players", String.format("%.2f", averageRun.getRunAveragePlayers())));

    JLabel averageMVPPercentLabel = (JLabel) runPanel.getComponent(29);
    averageMVPPercentLabel.setText(labelValue("MVP Per.", String.format("%.4f", averageRun.getRunAverageIsMVPPercent()) + "%"));

    JLabel averageMVPTotalLabel = (JLabel) runPanel.getComponent(30);
    averageMVPTotalLabel.setText(labelValue("MVP Total", String.valueOf(averageRun.getRunAverageIsMVPTotal())));

    JLabel averageTimeLabel = (JLabel) runPanel.getComponent(31);
    averageTimeLabel.setText(labelValue("Time", averageRun.getRunAverageTime()));

    JLabel killsPerHourLabel = (JLabel) runPanel.getComponent(32);
    killsPerHourLabel.setText(labelValue("Kills / hr", String.format("%.2f", averageRun.getRunAverageRunsPerHour())));

    JLabel uniqueChancePerHourLabel = (JLabel) runPanel.getComponent(33);
    uniqueChancePerHourLabel.setText(labelValue("Unique Chance / hr", String.format("%.4f", averageRun.getRunAverageUniqueChancePercentPerHour()) + "%"));


    JLabel uniqueRollCombined = (JLabel) runPanel.getComponent(34);
    int total = averageRun.getRunAverageTotal();
    if (total != 0) {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", String.format(fraction(averageRun.getRunAverageUniqueChanceRoll() / total)) + "%"));
    } else {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", "N/A"));
    }
  }

  void RestButtonUpdate() {
    JLabel totalKillsLabel = (JLabel) runPanel.getComponent(22);
    totalKillsLabel.setText(labelValue("Total Kills", String.valueOf(averageRun.getRunAverageTotal())));

    JLabel averageUniqueChanceLabel = (JLabel) runPanel.getComponent(23);
    averageUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", averageRun.getRunAverageUniqueChancePercent()) + "%"));

    JLabel averageUniqueRollLabel = (JLabel) runPanel.getComponent(24);
    averageUniqueRollLabel.setText(labelValue("Unique Roll", fraction(averageRun.getRunAverageUniqueChanceRoll())));

    JLabel averageContributionPercentLabel = (JLabel) runPanel.getComponent(25);
    averageContributionPercentLabel.setText(labelValue("Contribution Per.", String.format("%.2f", averageRun.getRunAverageContributionPercent()) + "%"));

    JLabel averageContributionFlatLabel = (JLabel) runPanel.getComponent(26);
    averageContributionFlatLabel.setText(labelValue("Contribution Flat", averageRun.getRunAverageContributionFlatOwn() + "/" + averageRun.getRunContributionFlatTotal()));

    JLabel averageContributionMinLabel = (JLabel) runPanel.getComponent(27);
    averageContributionMinLabel.setText(labelValue("Contribution Min.", String.valueOf(averageRun.getRunAverageContributionMinimumDamageTotal())));

    JLabel averagePlayersLabel = (JLabel) runPanel.getComponent(28);
    averagePlayersLabel.setText(labelValue("Players", String.format("%.2f", averageRun.getRunAveragePlayers())));

    JLabel averageMVPPercentLabel = (JLabel) runPanel.getComponent(29);
    averageMVPPercentLabel.setText(labelValue("MVP Per.", String.format("%.4f", averageRun.getRunAverageIsMVPPercent()) + "%"));

    JLabel averageMVPTotalLabel = (JLabel) runPanel.getComponent(30);
    averageMVPTotalLabel.setText(labelValue("MVP Total", String.valueOf(averageRun.getRunAverageIsMVPTotal())));

    JLabel averageTimeLabel = (JLabel) runPanel.getComponent(31);
    averageTimeLabel.setText(labelValue("Time", averageRun.getRunAverageTime()));

    JLabel killsPerHourLabel = (JLabel) runPanel.getComponent(32);
    killsPerHourLabel.setText(labelValue("Kills / hr", String.format("%.2f", averageRun.getRunAverageRunsPerHour())));

    JLabel uniqueChancePerHourLabel = (JLabel) runPanel.getComponent(33);
    uniqueChancePerHourLabel.setText(labelValue("Unique Chance / hr", String.format("%.4f", averageRun.getRunAverageUniqueChancePercentPerHour()) + "%"));

    JLabel uniqueRollCombined = (JLabel) runPanel.getComponent(34);
    int total = averageRun.getRunAverageTotal();
    if (total != 0) {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", String.format(fraction(averageRun.getRunAverageUniqueChanceRoll() / total)) + "%"));
    } else {
      uniqueRollCombined.setText(labelValue("Combined Unique Roll", "N/A"));
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