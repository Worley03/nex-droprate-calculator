package com.nexdropratecalculator;

import lombok.Data;

@Data
public class NexDroprateCalculatorRun {
  public Utils.RUN_STATES runState;

  public double runUniqueChancePercent;
  public int runUniqueChanceRoll;
  public double runContributionPercent;
  public int runContributionFlatOwn;
  public int runContributionFlatTotal;
  public boolean runMinimumContribution;
  public double runPlayers;
  public int runPlayersMin;
  public int runPlayersMax;
  public boolean runIsMVP;
  public int ticks;

  public NexDroprateCalculatorRun(Utils.RUN_STATES stateToSet) {
    this.runState = stateToSet;

    this.runUniqueChancePercent = 0;
    this.runUniqueChanceRoll = 0;
    this.runContributionPercent = 0;
    this.runContributionFlatOwn = 0;
    this.runContributionFlatTotal = 0;
    this.runMinimumContribution = false;
    this.runPlayers = 0;
    this.runPlayersMin = 0;
    this.runPlayersMax = 0;
    this.runIsMVP = false;
    this.ticks = 0;
  }

  public void updateRun(int ownContribution, int totalContribution, int players) {
    ticks++;

    // Update minimum & maximum players accordingly
    // TODO: Max players value can only increase while players are joining
    //  during Nex's spawn animation, after that they can only decrease
    //  because of this, somehow set PlayersMax until no new players can enter the fight
    if (players < runPlayersMin) runPlayersMin = players;
    if (players > runPlayersMax) runPlayersMax = players;

    runPlayers = (runPlayersMax + runPlayersMin) / 2.0;

    // Add to own and total contribution values
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
  }
}