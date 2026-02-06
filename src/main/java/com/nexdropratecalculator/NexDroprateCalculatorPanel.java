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
  private NexDroprateCalculatorRun currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.NOT_STARTED);
  private NexDroprateCalculatorRun lastRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.NOT_STARTED);

  // TODO: Consider moving the logic from here to a separate class for run averages
  private int runAverageTotal = 0;
  private double runAverageUniqueChancePercent = 0;
  private int runAverageUniqueChanceRoll = 0;
  private double runAverageContributionPercent = 0;
  private int runAverageContributionFlatOwn = 0;
  private int runAverageContributionFlatTotal = 0;
  private int runAverageContributionMinimumDamageTotal = 0;
  private double runAveragePlayers = 0;
  private int runAverageIsMVPTotal = 0;
  private double runAverageIsMVPPercent = 0;
  private int runAverageTicks = 0;
  private double runAverageRunsPerHour = 0;
  private double runAverageUniqueChancePercentPerHour = 0;

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
    JPanel runPanel = new JPanel();
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

    stateLabel = new JLabel(labelState(this.currentRun.runState));
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
      resetButtonUpdate();
    });
    runPanel.add(resetButton);

    add(runPanel);
  }

  void deinit() {}

  private void updatePanelValues() {
    // Current run
    uniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", currentRun.getRunUniqueChancePercent()) + "%"));
    uniqueRollLabel.setText(labelValue("Unique Roll", Utils.fraction(currentRun.getRunUniqueChanceRoll())));
    contributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", currentRun.getRunContributionPercent()) + "%"));
    contributionFlatLabel.setText(labelValue("Damage", currentRun.getRunContributionFlatOwn() + "/" + currentRun.getRunContributionFlatTotal()));
    playersLabel.setText(labelValue("Players", String.format("%.2f", currentRun.getRunPlayers())));
    isMVPLabel.setText(labelValue("MVP", (currentRun.isRunIsMVP() ? "yes" : "no")));
    timeLabel.setText(labelValue("Time", Utils.formatDuration(currentRun.getTicks())));
    stateLabel.setText(labelState(currentRun.getRunState()));

    // Last run
    lastUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", lastRun.getRunUniqueChancePercent()) + "%"));
    lastUniqueRollLabel.setText(labelValue("Unique Roll", Utils.fraction(lastRun.getRunUniqueChanceRoll())));
    lastContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", lastRun.getRunContributionPercent()) + "%"));
    lastContributionFlatLabel.setText(labelValue("Damage", lastRun.getRunContributionFlatOwn() + "/" + lastRun.getRunContributionFlatTotal()));
    lastPlayersLabel.setText(labelValue("Players", String.format("%.2f", lastRun.getRunPlayers())));
    lastIsMVPLabel.setText(labelValue("MVP", (lastRun.isRunIsMVP() ? "yes" : "no")));
    lastTimeLabel.setText(labelValue("Time", Utils.formatDuration(lastRun.getTicks())));

    // Average run
    this.updatePanelValuesAverage();
  }

  // Separate method for updating averages' values is needed since this is duplicated for the reset button
  private void updatePanelValuesAverage() {
    totalKillsLabel.setText(labelValue("Total Kills", String.valueOf(this.runAverageTotal)));
    averageUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", this.runAverageUniqueChancePercent) + "%"));
    averageUniqueRollLabel.setText(labelValue("Unique Roll", Utils.fraction(this.runAverageUniqueChanceRoll)));
    averageContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", this.runAverageContributionPercent) + "%"));
    averageContributionFlatLabel.setText(labelValue("Damage", this.runAverageContributionFlatOwn + "/" + this.runAverageContributionFlatTotal));
    averagePlayersLabel.setText(labelValue("Players", String.format("%.2f", this.runAveragePlayers)));
    averageMVPPercentLabel.setText(labelValue("MVP", String.format("%.2f", (this.runAverageIsMVPPercent) * 100) + "%"));
    averageMVPTotalLabel.setText(labelValue("MVP Total", String.valueOf(this.runAverageIsMVPTotal)));
    averageTimeLabel.setText(labelValue("Time", Utils.formatDuration(this.runAverageTicks)));
    killsPerHourLabel.setText(labelValue("Kills / hr", String.format("%.2f", this.runAverageRunsPerHour)));
    uniqueChancePerHourLabel.setText(labelValue("Unique Chance / hr", String.format("%.4f", this.runAverageUniqueChancePercentPerHour) + "%"));

    int total = this.runAverageTotal;
    uniqueRollCombined.setText(labelValue("Combined Unique Roll", Utils.fraction(total != 0 ? this.runAverageUniqueChanceRoll / total : 0)));
  }

  private void resetButtonUpdate() {
    this.runAverageTotal = 0;
    this.runAverageUniqueChancePercent = 0;
    this.runAverageUniqueChanceRoll = 0;
    this.runAverageContributionPercent = 0;
    this.runAverageContributionFlatOwn = 0;
    this.runAverageContributionFlatTotal = 0;
    this.runAverageContributionMinimumDamageTotal = 0;
    this.runAveragePlayers = 0;
    this.runAverageIsMVPTotal = 0;
    this.runAverageIsMVPPercent = 0;
    this.runAverageTicks = 0;
    this.runAverageRunsPerHour = 0;
    this.runAverageUniqueChancePercentPerHour = 0;
    // Trigger values print manually
    this.updatePanelValues();
  }

  // A new run started, initialize values
  public void startCurrentRun(int playerCount) {
    this.currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.STARTING);
    this.currentRun.runPlayersMin = playerCount;
    this.currentRun.runPlayersMax = playerCount;
    this.updatePanelValues();
  }

  // Current run is ongoing, updating run data with recorded values
  public void updateCurrentRun(int ownContribution, int totalContribution, int players) {
    this.currentRun.updateRun(ownContribution, totalContribution, players);
    this.updatePanelValues();
  }

  // Current run either just finished or was cancelled (due to player leaving/dying)
  public void finishCurrentRun(Utils.RUN_STATES finishReason) {
    this.currentRun.runState = finishReason;

    if (finishReason == Utils.RUN_STATES.FINISHED) {
      // Current run finished successfully
      if (this.currentRun.runIsMVP) {
        // Set the mvp bonus
        double mvpBoost = this.currentRun.runContributionPercent * 1.1;
        if (mvpBoost > 100.0) mvpBoost = 100.0;
        this.currentRun.runUniqueChanceRoll = (int) Math.ceil(43.0 * (100.0 / mvpBoost));
        this.currentRun.runUniqueChancePercent = (1.0 / (double) this.currentRun.runUniqueChanceRoll) * 100.0;
      }
      // update average run values from currentRun
      this.addCurrentRunToAverages();
      this.lastRun.setFromRun(this.currentRun); // TODO: Is this method necessary? It's only used upon a run finish anyway (consider just replacing lastRun with currentRun)
      this.currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.NOT_STARTED);
    } else if (finishReason == Utils.RUN_STATES.CANCELLED) {
      // Player has left before the fight has finished
      // TODO: What do we even do on a cancelled run?
    }
    this.updatePanelValues();
  }

  public void setCurrentRunMvp(boolean MvpStatus) {
    this.currentRun.runIsMVP = MvpStatus;
  }

  public void setCurrentRunMinimumContribution(boolean MinContrib) {
    this.currentRun.runMinimumContribution = MinContrib;
  }

  private void addCurrentRunToAverages() {
    NexDroprateCalculatorRun run = this.currentRun;
    runAverageTicks = (int)
            Math.round(
                    (((double) runAverageTicks * runAverageTotal) + (double) run.getTicks())
                            / (double) (runAverageTotal + 1));

    runAverageUniqueChancePercent =
            ((runAverageUniqueChancePercent * (double) runAverageTotal)
                    + run.getRunUniqueChancePercent())
                    / (double) (runAverageTotal + 1);
    runAverageUniqueChanceRoll = (int) Math.ceil(100.0 / runAverageUniqueChancePercent);

    runAverageContributionPercent =
            ((runAverageContributionPercent * (double) runAverageTotal)
                    + run.getRunContributionPercent())
                    / (double) (runAverageTotal + 1);
    runAverageContributionFlatOwn = (int)
            Math.round(
                    (((double) runAverageContributionFlatOwn * runAverageTotal)
                            + (double) run.getRunContributionFlatOwn())
                            / (double) (runAverageTotal + 1));
    runAverageContributionFlatTotal = (int)
            Math.round(
              (((double) runAverageContributionFlatTotal * runAverageTotal)
                      + (double) run.getRunContributionFlatTotal())
                      / (double) (runAverageTotal + 1));
    runAverageContributionMinimumDamageTotal += run.isRunMinimumContribution() ? 1 : 0;

    runAveragePlayers =
            ((runAveragePlayers * runAverageTotal) + run.getRunPlayers())
                    / (double) (runAverageTotal + 1);

    runAverageIsMVPTotal += run.isRunIsMVP() ? 1 : 0;
    runAverageIsMVPPercent = (double) runAverageIsMVPTotal / (double) (runAverageTotal + 1);

    runAverageRunsPerHour = 6000.0 / ((double) runAverageTicks + 30.0);
    runAverageUniqueChancePercentPerHour =
            (1 - Math.pow(1.0 - (runAverageUniqueChancePercent / 100.0), runAverageRunsPerHour)) * 100.0;

    runAverageTotal++;
  }

  private String labelTitle(String title) {
    return "<html><div style='color:white;font-size:10px;'>" + title + "</div></html>";
  }

  private String labelValue(String name, String value) {
    return "<html><div style='color:white;font-size:10px;'><b>" + name + ": </b>" + value + "</div></html>";
  }

  private String labelState(Utils.RUN_STATES state) {
    return "<html><div style='color:white;font-size:10px;'><b>State: </b>" + state + "</div></html>";
  }

  private String labelEmpty() {
    return "<html><div style='color:white;font-size:10px;'><b></b></div></html>";
  }

  private String labelButton(String text) {
    return "<html><div style='color:white;font-size:10px;'>" + text + "</div></html>";
  }
}