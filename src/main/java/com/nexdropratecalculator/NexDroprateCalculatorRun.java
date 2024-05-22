package com.nexdropratecalculator;

import lombok.Data;

@Data
public class NexDroprateCalculatorRun {
  public int runState = 0;

  public double runUniqueChancePercent = 0;
  public int runUniqueChanceRoll = 0;
  public double runContributionPercent = 0;
  public int runContributionFlatOwn = 0;
  public int runContributionFlatTotal = 0;
  public boolean runMinimumContribution = false;
  public double runPlayers = 0;
  public int runPlayersMin = 0;
  public int runPlayersMax = 0;
  public boolean runIsMVP = false;
  public int ticks = 0;
  public String time = "00h 00m 00s";

  public int runAverageTotal = 0;
  public double runAverageUniqueChancePercent = 0;
  public int runAverageUniqueChanceRoll = 0;
  public double runAverageContributionPercent = 0;
  public int runAverageContributionFlatOwn = 0;
  public int runAverageContributionFlatTotal = 0;
  public int runAverageContributionMinimumDamageTotal = 0;
  public double runAveragePlayers = 0;
  public int runAverageIsMVPTotal = 0;
  public double runAverageIsMVPPercent = 0;
  public int runAverageTicks = 0;
  public String runAverageTime = "00h 00m 00s";
  public double runAverageRunsPerHour = 0;
  public double runAverageUniqueChancePercentPerHour = 0;

  public NexDroprateCalculatorRun() {}
  ;

  public void updateRun(
      int ownContribution,
      int totalContribution,
      int players,
      boolean isMVP,
      boolean minContribution,
      boolean runStarted,
      boolean runFinished) {
    if (runFinished) {
      runIsMVP = isMVP;
      if (runIsMVP) {
        double mvpBoost = runContributionPercent + 10.0;
        if (mvpBoost > 100.0) mvpBoost = 100.0;
        runUniqueChanceRoll = (int) Math.ceil(43.0 * (100.0 / mvpBoost));
        runUniqueChancePercent = (1.0 / (double) runUniqueChanceRoll) * 100.0;
      }
      runMinimumContribution = minContribution;
      return;
    }

    ticks++;
    int secondsTotal = (int) Math.ceil((double) ticks * 0.6);
    int seconds = secondsTotal % 60;
    int minutes = (int) Math.floor((double) secondsTotal / 60.0);
    int hours = (int) Math.floor((double) secondsTotal / 3600.0);
    time = String.format("%02dh %02dm %02ds", hours, minutes % 60, seconds % 60);

    if (runStarted) {
      runPlayersMin = players;
      runPlayersMax = players;
    }
    if (players < runPlayersMin) runPlayersMin = players;
    if (players > runPlayersMax) runPlayersMax = players;

    runPlayers = (runPlayersMax + runPlayersMin) / 2.0;

    runContributionFlatOwn += ownContribution;
    runContributionFlatTotal += totalContribution;

    if (runContributionFlatOwn <= 0 || runContributionFlatTotal <= 0) {
      runUniqueChanceRoll = 0;
      runUniqueChancePercent = 0;
      runContributionPercent = 0;
    } else {
      runUniqueChanceRoll = (int) Math.ceil(43.0 * (100.0 / runContributionPercent));
      runUniqueChancePercent = (1.0 / (double) runUniqueChanceRoll) * 100.0;
      runContributionPercent =
          ((double) runContributionFlatOwn / (double) runContributionFlatTotal) * 100.0;
    }
  }

  public void addRun(NexDroprateCalculatorRun run) {
    runAverageTicks =
        (int)
            Math.round(
                (((double) runAverageTicks * runAverageTotal) + (double) run.getTicks())
                    / (double) (runAverageTotal + 1));
    int secondsTotal = (int) Math.ceil((double) runAverageTicks * 0.6);
    int seconds = secondsTotal % 60;
    int minutes = (int) Math.floor((double) secondsTotal / 60.0);
    int hours = (int) Math.floor((double) secondsTotal / 3600.0);
    runAverageTime = String.format("%02dh %02dm %02ds", hours, minutes % 60, seconds % 60);

    runAverageUniqueChancePercent =
        ((runAverageUniqueChancePercent * (double) runAverageTotal)
                + run.getRunUniqueChancePercent())
            / (double) (runAverageTotal + 1);
    runAverageUniqueChanceRoll = (int) Math.ceil(100.0 / runAverageUniqueChancePercent);

    runAverageContributionPercent =
        ((runAverageContributionPercent * (double) runAverageTotal)
                + run.getRunContributionPercent())
            / (double) (runAverageTotal + 1);
    runAverageContributionFlatOwn =
        (int)
            Math.round(
                (((double) runAverageContributionFlatOwn * runAverageTotal)
                        + (double) run.getRunContributionFlatOwn())
                    / (double) (runAverageTotal + 1));
    runAverageContributionFlatTotal =
        (int)
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
        (1 - Math.pow(1.0 - (runAverageUniqueChancePercent / 100.0), runAverageRunsPerHour))
            * 100.0;

    runAverageTotal++;
  }

  public void setFromRun(NexDroprateCalculatorRun run) {
    runState = run.getRunState();

    runUniqueChancePercent = run.getRunUniqueChancePercent();
    runUniqueChanceRoll = run.getRunUniqueChanceRoll();
    runContributionPercent = run.getRunContributionPercent();
    runContributionFlatOwn = run.getRunContributionFlatOwn();
    runContributionFlatTotal = run.getRunContributionFlatTotal();
    runMinimumContribution = run.isRunMinimumContribution();
    runPlayers = run.getRunPlayers();
    runPlayersMin = run.getRunPlayersMin();
    runPlayersMax = run.getRunPlayersMax();
    runIsMVP = run.isRunIsMVP();
    ticks = run.getTicks();
    time = run.getTime();

    runAverageTotal = run.getRunAverageTotal();
    runAverageUniqueChancePercent = run.getRunAverageUniqueChancePercent();
    runAverageUniqueChanceRoll = run.getRunAverageUniqueChanceRoll();
    runAverageContributionPercent = run.getRunAverageContributionPercent();
    runAverageContributionFlatOwn = run.getRunAverageContributionFlatOwn();
    runAverageContributionFlatTotal = run.getRunAverageContributionFlatTotal();
    runAverageContributionMinimumDamageTotal = run.getRunAverageContributionMinimumDamageTotal();
    runAveragePlayers = run.getRunAveragePlayers();
    runAverageIsMVPTotal = run.getRunAverageIsMVPTotal();
    runAverageIsMVPPercent = run.getRunAverageIsMVPPercent();
    runAverageTicks = run.getRunAverageTicks();
    runAverageTime = run.getRunAverageTime();
    runAverageRunsPerHour = run.getRunAverageRunsPerHour();
    runAverageUniqueChancePercentPerHour = run.getRunAverageUniqueChancePercentPerHour();
  }

  public void reset() {
    runState = 0;

    runUniqueChancePercent = 0;
    runUniqueChanceRoll = 0;
    runContributionPercent = 0;
    runContributionFlatOwn = 0;
    runContributionFlatTotal = 0;
    runMinimumContribution = false;
    runPlayers = 0;
    runPlayersMin = 0;
    runPlayersMax = 0;
    runIsMVP = false;
    ticks = 0;
    time = "00h 00m 00s";

    runAverageTotal = 0;
    runAverageUniqueChancePercent = 0;
    runAverageUniqueChanceRoll = 0;
    runAverageContributionPercent = 0;
    runAverageContributionFlatOwn = 0;
    runAverageContributionFlatTotal = 0;
    runAverageContributionMinimumDamageTotal = 0;
    runAveragePlayers = 0;
    runAverageIsMVPTotal = 0;
    runAverageIsMVPPercent = 0;
    runAverageTicks = 0;
    runAverageTime = "00h 00m 00s";
    runAverageRunsPerHour = 0;
    runAverageUniqueChancePercentPerHour = 0;
  }
}
