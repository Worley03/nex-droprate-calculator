package com.nexdropratecalculator;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

class Formatter {
  private static final String HTML_TEMPLATE = "<html><div style='color:white;font-size:10px;'>%s</div></html>";

  public static String text(String text) { return String.format(HTML_TEMPLATE, text); }
  public static String value(String name, String value) { return String.format(HTML_TEMPLATE, "<b>" + name + ":</b> " + value); }

  // Common formats
  public static String formatUniqueChance(double percent) { return value("Unique Chance", String.format("%.4f%%", percent)); }
  public static String formatUniqueRoll(int roll) { return value("Unique Roll", Utils.fraction(roll)); }
  public static String formatContribution(double percent) { return value("Contribution", String.format("%.2f%%", percent)); }
  public static String formatDamage(int own, int total) { return value("Damage", own + "/" + total); }
  public static String formatPlayers(double avg) { return value("Players", String.format("%.2f", avg)); }
  public static String formatMVP(boolean isMVP) { return value("MVP", isMVP ? "yes" : "no"); }
  public static String formatTime(int ticks) { return value("Time", Utils.formatDuration(ticks)); }
  public static String formatState(RunState state) { return value("State", state.toString()); }

  // Formats for averages
  public static String formatTotalKills(int total) { return value("Total Kills", String.valueOf(total)); }
  public static String formatMVPPercent(double percent) { return value("MVP", String.format("%.2f%%", percent * 100)); }
  public static String formatMVPTotal(int total) { return value("MVP Total", String.valueOf(total)); }
  public static String formatKillsPerHour(double killsPerHour) { return value("Kills / hr", String.format("%.2f", killsPerHour)); }
  public static String formatUniqueChancePerHour(double percent) { return value("Unique Chance / hr", String.format("%.4f%%", percent)); }
  public static String formatCombinedUniqueRoll(int totalKills, int uniqueChanceRoll) {
    int combinedRoll = totalKills != 0 ? uniqueChanceRoll / totalKills : 0;
    return value("Combined Unique Roll", Utils.fraction(combinedRoll));
  }
}

abstract class DisplaySection {
  protected final JPanel parent;
  protected final Font font;

  public DisplaySection(JPanel parent, Font font, String sectionTitle) {
    this.parent = parent;
    this.font = font;

    JLabel titleLabel = new JLabel(Formatter.text(String.format("- %s -", sectionTitle)));
    titleLabel.setFont(font);
    this.parent.add(titleLabel);
  }

  protected JLabel createLabel() {
    JLabel label = new JLabel();
    label.setFont(font);
    parent.add(label);
    return label;
  }
}

class RunDisplaySection extends DisplaySection {
  private final JLabel uniqueChanceLabel = createLabel();
  private final JLabel uniqueRollLabel = createLabel();
  private final JLabel contributionPercentLabel = createLabel();
  private final JLabel contributionFlatLabel = createLabel();
  private final JLabel playersLabel = createLabel();
  private final JLabel isMVPLabel = createLabel();
  private final JLabel timeLabel = createLabel();
  private JLabel stateLabel = null;

  public RunDisplaySection(JPanel parent, Font font, String sectionTitle, boolean includeState) {
    super(parent, font, sectionTitle);

    if (includeState) {
      this.stateLabel = createLabel();
    }
    createLabel(); // TODO: consider getting rid of empty labels and instead implement margin
  }

  public void updateFromRun(NexDroprateCalculatorRun run) {
    uniqueChanceLabel.setText(Formatter.formatUniqueChance(run.getUniqueChancePercent()));
    uniqueRollLabel.setText(Formatter.formatUniqueRoll(run.getUniqueChanceRoll()));
    contributionPercentLabel.setText(Formatter.formatContribution(run.getContributionPercent()));
    contributionFlatLabel.setText(Formatter.formatDamage(run.getContributionFlatOwn(), run.getContributionFlatTotal()));
    playersLabel.setText(Formatter.formatPlayers(run.calculateAvgPlayers()));
    isMVPLabel.setText(Formatter.formatMVP(run.isMVP()));
    timeLabel.setText(Formatter.formatTime(run.getTicks()));
    if (stateLabel != null) {
      stateLabel.setText(Formatter.formatState(run.getState()));
    }
  }
}

class AverageDisplaySection extends DisplaySection {
  private final JLabel totalKillsLabel = createLabel();
  private final JLabel averageUniqueChanceLabel = createLabel();
  private final JLabel averageUniqueRollLabel = createLabel();
  private final JLabel averageContributionPercentLabel = createLabel();
  private final JLabel averageContributionFlatLabel = createLabel();
  private final JLabel averagePlayersLabel = createLabel();
  private final JLabel averageMVPPercentLabel = createLabel();
  private final JLabel averageMVPTotalLabel = createLabel();
  private final JLabel averageTimeLabel = createLabel();
  private final JLabel killsPerHourLabel = createLabel();
  private final JLabel uniqueChancePerHourLabel = createLabel();
  private final JLabel uniqueRollCombinedLabel = createLabel();
  // Button is added later inside the init function

  public AverageDisplaySection(JPanel parent, Font font, String sectionTitle) {
    super(parent, font, sectionTitle);
  }

  public void updateFromAverage(AverageValues avg) {
    totalKillsLabel.setText(Formatter.formatTotalKills(avg.getTotalKills()));
    averageUniqueChanceLabel.setText(Formatter.formatUniqueChance(avg.getUniqueChancePercent()));
    averageUniqueRollLabel.setText(Formatter.formatUniqueRoll(avg.getUniqueChanceRoll()));
    averageContributionPercentLabel.setText(Formatter.formatContribution(avg.getContributionPercent()));
    averageContributionFlatLabel.setText(Formatter.formatDamage(avg.getContributionFlatOwn(), avg.getContributionFlatTotal()));
    averagePlayersLabel.setText(Formatter.formatPlayers(avg.getPlayersAverage()));
    averageMVPPercentLabel.setText(Formatter.formatMVPPercent(avg.getMvpPercent()));
    averageMVPTotalLabel.setText(Formatter.formatMVPTotal(avg.getMvpTotal()));
    averageTimeLabel.setText(Formatter.formatTime(avg.getAvgTicksPerKill()));
    killsPerHourLabel.setText(Formatter.formatKillsPerHour(avg.getRunsPerHour()));
    uniqueChancePerHourLabel.setText(Formatter.formatUniqueChancePerHour(avg.getUniqueChancePercentPerHour()));
    uniqueRollCombinedLabel.setText(Formatter.formatCombinedUniqueRoll(avg.getTotalKills(), avg.getUniqueChanceRoll()));
  }
}

@Data
class AverageValues {
  private int totalKills;
  private int avgTicksPerKill;
  private double uniqueChancePercent;
  private int uniqueChanceRoll;
  private double contributionPercent;
  private int contributionFlatOwn;
  private int contributionFlatTotal;
  private int minimumContributionTotal;
  private double playersAverage;
  private int mvpTotal;
  private double mvpPercent;

  public double getRunsPerHour() {
    if (avgTicksPerKill == 0) {
      return 0;
    }
    return 6000.0 / (avgTicksPerKill + 61.0); // From Nex dying, there's exactly 61 ticks until she spawns and starts new fight
  }

  public double getUniqueChancePercentPerHour() {
    return (1 - Math.pow(1.0 - (uniqueChancePercent / 100.0), getRunsPerHour())) * 100.0;
  }

  public void addRun(NexDroprateCalculatorRun run) {
    int newTotal = totalKills + 1;

    avgTicksPerKill = (int) Math.round((avgTicksPerKill * totalKills + run.getTicks()) / (double) newTotal);

    uniqueChancePercent = (uniqueChancePercent * totalKills + run.getUniqueChancePercent()) / newTotal;
    uniqueChanceRoll = uniqueChancePercent > 0 ? (int) Math.ceil(100.0 / uniqueChancePercent) : 0;

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

public class NexDroprateCalculatorPanel extends PluginPanel {
  protected NexDroprateCalculatorRun currentRun = new NexDroprateCalculatorRun(RunState.NOT_STARTED);
  private NexDroprateCalculatorRun lastRun = new NexDroprateCalculatorRun(RunState.NOT_STARTED);
  private AverageValues avg = new AverageValues();

  @Getter @Setter
  private boolean currentRunActive = false;

  private RunDisplaySection currentSection;
  private RunDisplaySection lastSection;
  private AverageDisplaySection averageSection;

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

    currentSection = new RunDisplaySection(runPanel, font, "Current Kill", true);
    lastSection = new RunDisplaySection(runPanel, font, "Last Kill", false);
    averageSection = new AverageDisplaySection(runPanel, font, "Averages");

    JButton resetButton = new JButton(Formatter.text("Reset Averages"));
    resetButton.setFont(font);
    resetButton.setFocusable(false);
    resetButton.addActionListener(e -> {
      avg = new AverageValues();
      averageSection.updateFromAverage(avg);
    });
    runPanel.add(resetButton);

    add(runPanel);
    updateAllSections(); // Run once to initialize displays with default values
  }

  private void updateAllSections() {
    currentSection.updateFromRun(currentRun);
    lastSection.updateFromRun(lastRun);
    averageSection.updateFromAverage(avg);
  }

  // A new run started, initialize values
  public void startCurrentRun(int playerCount) {
    // Set what's currently stored at currentRun into lastRun, but only if that run was completed successfully
    if (currentRun.getState() == RunState.FINISHED) {
      lastRun = currentRun;
    }
    currentRun = new NexDroprateCalculatorRun(RunState.STARTED);
    currentRun.setPlayersCurrent(playerCount);
    currentRun.setPlayersMax(playerCount);
    updateAllSections();
  }

  // Current run is ongoing, updating run data with recorded values
  public void updateCurrentRun(int ownContribution, int totalContribution, int players) {
    currentRun.updateRun(ownContribution, totalContribution, players);
    currentSection.updateFromRun(currentRun);
  }

  // Current run either just finished or was cancelled (due to player leaving/dying)
  public void finishCurrentRun(RunState finishReason, int ticksToSubtract) {
    currentRunActive = false;

    if (finishReason == RunState.FINISHED) {
      // Current run finished successfully
      if (currentRun.isMVP()) {
        // Set the mvp bonus - Math.min to ensure contribution doesn't exceed 100% (in case of a solo run)
        currentRun.updateUniqueChance(Math.min(currentRun.getContributionPercent() * 1.1, 100.0));
      }
      // HACK: We've waited for N ticks after fight ended before finishing, correct the time
      currentRun.setTicks(currentRun.getTicks() - ticksToSubtract);
      // update average run values from currentRun
      currentRun.setState(finishReason);
      avg.addRun(currentRun);
    } else if (finishReason == RunState.CANCELLED && currentRun.getState() != RunState.FINISHED) {
      // Player has left before the fight has finished - only consider run as cancelled if previously set state was
      // Ignore cancelled runs from being added into the average and being set to lastRun
      currentRun.setState(finishReason);
    }
    updateAllSections();
  }

  public void setCurrentRunMvp() {
    currentRun.setMVP(true);
  }

  public void setCurrentRunMinimumContribution() {
    currentRun.setMinimumContribution(true);
  }
}