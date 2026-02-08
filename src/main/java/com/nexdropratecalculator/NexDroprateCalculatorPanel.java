package com.nexdropratecalculator;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

class AverageValues {
  protected int totalKills;
  protected int totalTicks;
  protected double uniqueChancePercent;
  protected int uniqueChanceRoll;
  protected double contributionPercent;
  protected int contributionFlatOwn;
  protected int contributionFlatTotal;
  protected int minimumContributionTotal;
  protected double playersAverage;
  protected int mvpTotal;
  protected double mvpPercent;

  public double getRunsPerHour() {
    if (totalTicks == 0) {
      return 0;
    }
    return 6000.0 / (totalTicks + 30.0);
  }

  public double getUniqueChancePercentPerHour() {
    return (1 - Math.pow(1.0 - (uniqueChancePercent / 100.0), getRunsPerHour())) * 100.0;
  }

  public void addRun(NexDroprateCalculatorRun run) {
    int newTotal = totalKills + 1;

    totalTicks = (int) Math.round((totalTicks * totalKills + run.getTicks()) / (double) newTotal);

    uniqueChancePercent = (uniqueChancePercent * totalKills + run.getUniqueChancePercent()) / newTotal;
    uniqueChanceRoll = (int) Math.ceil(100.0 / uniqueChancePercent);

    contributionPercent = (contributionPercent * totalKills + run.getContributionPercent()) / newTotal;

    contributionFlatOwn = (int) Math.round((contributionFlatOwn * totalKills + run.getContributionFlatOwn()) / (double) newTotal);
    contributionFlatTotal = (int) Math.round((contributionFlatTotal * totalKills + run.getContributionFlatTotal()) / (double) newTotal);
    if (run.isMinimumContribution()) minimumContributionTotal++;

    playersAverage = (playersAverage * totalKills + run.calculateAvgPlayers()) / newTotal;

    if (run.isMVP()) mvpTotal++;
    mvpPercent = mvpTotal / (double) newTotal;

    totalKills++;
  }
}

@Slf4j
public class NexDroprateCalculatorPanel extends PluginPanel {
  protected NexDroprateCalculatorRun currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.NOT_STARTED);
  private NexDroprateCalculatorRun lastRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.NOT_STARTED);
  private AverageValues avg = new AverageValues();

  @Getter @Setter
  private boolean currentRunActive = false;

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

    stateLabel = new JLabel(labelValue("State", this.currentRun.getState().toString()));
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
      this.avg = new AverageValues();
      this.updatePanelValuesAverage();
    });
    runPanel.add(resetButton);

    add(runPanel);
  }

  private void updatePanelValues() {
    // Current run
    uniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f%%", currentRun.getUniqueChancePercent())));
    uniqueRollLabel.setText(labelValue("Unique Roll", Utils.fraction(currentRun.getUniqueChanceRoll())));
    contributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f%%", currentRun.getContributionPercent())));
    contributionFlatLabel.setText(labelValue("Damage", currentRun.getContributionFlatOwn() + "/" + currentRun.getContributionFlatTotal()));
    playersLabel.setText(labelValue("Players", String.format("%.2f", currentRun.calculateAvgPlayers())));
    isMVPLabel.setText(labelValue("MVP", (currentRun.isMVP() ? "yes" : "no")));
    timeLabel.setText(labelValue("Time", Utils.formatDuration(currentRun.getTicks())));
    stateLabel.setText(labelValue("State", currentRun.getState().toString()));

    // Last run
    lastUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f%%", lastRun.getUniqueChancePercent())));
    lastUniqueRollLabel.setText(labelValue("Unique Roll", Utils.fraction(lastRun.getUniqueChanceRoll())));
    lastContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f%%", lastRun.getContributionPercent())));
    lastContributionFlatLabel.setText(labelValue("Damage", lastRun.getContributionFlatOwn() + "/" + lastRun.getContributionFlatTotal()));
    lastPlayersLabel.setText(labelValue("Players", String.format("%.2f", lastRun.calculateAvgPlayers())));
    lastIsMVPLabel.setText(labelValue("MVP", (lastRun.isMVP() ? "yes" : "no")));
    lastTimeLabel.setText(labelValue("Time", Utils.formatDuration(lastRun.getTicks())));

    // Average run
    this.updatePanelValuesAverage();
  }

  // Separate method for updating averages' values is needed since this is duplicated for the reset button
  private void updatePanelValuesAverage() {
    totalKillsLabel.setText(labelValue("Total Kills", String.valueOf(avg.totalKills)));
    averageUniqueChanceLabel.setText(labelValue("Unique Chance", String.format("%.4f", avg.uniqueChancePercent) + "%"));
    averageUniqueRollLabel.setText(labelValue("Unique Roll", Utils.fraction(avg.uniqueChanceRoll)));
    averageContributionPercentLabel.setText(labelValue("Contribution", String.format("%.2f", avg.contributionPercent) + "%"));
    averageContributionFlatLabel.setText(labelValue("Damage", avg.contributionFlatOwn + "/" + avg.contributionFlatTotal));
    averagePlayersLabel.setText(labelValue("Players", String.format("%.2f", avg.playersAverage)));
    averageMVPPercentLabel.setText(labelValue("MVP", String.format("%.2f", (avg.mvpPercent) * 100) + "%"));
    averageMVPTotalLabel.setText(labelValue("MVP Total", String.valueOf(avg.mvpTotal)));
    averageTimeLabel.setText(labelValue("Time", Utils.formatDuration(avg.totalTicks)));
    killsPerHourLabel.setText(labelValue("Kills / hr", String.format("%.2f", avg.getRunsPerHour())));
    uniqueChancePerHourLabel.setText(labelValue("Unique Chance / hr", String.format("%.4f", avg.getUniqueChancePercentPerHour()) + "%"));
    uniqueRollCombined.setText(labelValue("Combined Unique Roll", Utils.fraction(avg.totalKills != 0 ? avg.uniqueChanceRoll / avg.totalKills : 0)));
  }

  // A new run started, initialize values
  public void startCurrentRun(int playerCount) {
    // Set what's currently stored at currentRun into lastRun, but only if that run was completed successfully
    if (this.currentRun.getState() == Utils.RUN_STATES.FINISHED) {
      this.lastRun = this.currentRun;
    }
    this.currentRun = new NexDroprateCalculatorRun(Utils.RUN_STATES.STARTED);
    this.currentRun.setPlayersCurrent(playerCount);
    this.currentRun.setPlayersMax(playerCount);
    this.updatePanelValues();
  }

  // Current run is ongoing, updating run data with recorded values
  public void updateCurrentRun(int ownContribution, int totalContribution, int players) {
    this.currentRun.updateRun(ownContribution, totalContribution, players);
    this.updatePanelValues(); // TODO: Don't update all values while fight is ongoing, only current one
  }

  // Current run either just finished or was cancelled (due to player leaving/dying)
  public void finishCurrentRun(Utils.RUN_STATES finishReason) {
    currentRunActive = false;

    if (finishReason == Utils.RUN_STATES.FINISHED) {
      // Current run finished successfully
      if (this.currentRun.isMVP()) {
        // Set the mvp bonus
        double mvpBoost = this.currentRun.getContributionPercent() * 1.1;
        if (mvpBoost > 100.0) mvpBoost = 100.0;
        this.currentRun.setUniqueChanceRoll((int) Math.ceil(43.0 * (100.0 / mvpBoost)));
        this.currentRun.setUniqueChancePercent((1.0 / this.currentRun.getUniqueChanceRoll()) * 100.0);
      }
      // update average run values from currentRun
      this.currentRun.setState(finishReason);
      this.avg.addRun(this.currentRun);
    } else if (finishReason == Utils.RUN_STATES.CANCELLED && this.currentRun.getState() != Utils.RUN_STATES.FINISHED) {
      // Player has left before the fight has finished - only consider run as cancelled if previously set state was
      // Ignore cancelled runs from being added into the average and being set to lastRun
      this.currentRun.setState(finishReason);
    }
    this.updatePanelValues();
  }

  public void setCurrentRunMvp() {
    this.currentRun.setMVP(true);
  }

  public void setCurrentRunMinimumContribution() {
    this.currentRun.setMinimumContribution(true);
  }

  private String labelTitle(String title) {
    return "<html><div style='color:white;font-size:10px;'>" + title + "</div></html>";
  }

  private String labelValue(String name, String value) {
    return "<html><div style='color:white;font-size:10px;'><b>" + name + ": </b>" + value + "</div></html>";
  }

  private String labelEmpty() {
    return "<html><div style='color:white;font-size:10px;'><b></b></div></html>";
  }

  private String labelButton(String text) {
    return "<html><div style='color:white;font-size:10px;'>" + text + "</div></html>";
  }
}