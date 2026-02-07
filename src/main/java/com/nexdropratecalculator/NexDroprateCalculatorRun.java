package com.nexdropratecalculator;

import lombok.Data;

@Data
public class NexDroprateCalculatorRun {
  private Utils.RUN_STATES state;

  private int ticks;
  private double uniqueChancePercent;
  private int uniqueChanceRoll;
  private double contributionPercent;
  private int contributionFlatOwn;
  private int contributionFlatTotal;
  private int playersMax;         // amount of players who were present at the start of the kill
  private int playersCurrent;     // amount of players currently in the room (can only be lower than the max)
  private double playersAverage;  // average amount of players (used if someone leaves mid-kill)
  private boolean isMinimumContribution;
  private boolean isMVP;

  public NexDroprateCalculatorRun(Utils.RUN_STATES stateToSet) {
    this.state = stateToSet;

    this.ticks = 0;
    this.uniqueChancePercent = 0;
    this.uniqueChanceRoll = 0;
    this.contributionPercent = 0;
    this.contributionFlatOwn = 0;
    this.contributionFlatTotal = 0;
    this.playersMax = 0;
    this.playersCurrent = 0;
    this.playersAverage = 0;
    this.isMinimumContribution = false;
    this.isMVP = false;
  }

  public void updateRun(int ownContribution, int totalContribution, int players) {
    ticks++;

    // Update minimum & maximum players accordingly
    // TODO: Max players value can only increase while players are joining
    //  during Nex's spawn animation, after that they can only decrease
    //  because of this, somehow set PlayersMax until no new players can enter the fight
    if (players < playersCurrent) playersCurrent = players;
    if (players > playersMax) playersMax = players;

    this.playersAverage = (playersMax + playersCurrent) / 2.0;

    // Add to own and total contribution values
    contributionFlatOwn += ownContribution;
    contributionFlatTotal += totalContribution;

    if (contributionFlatOwn <= 0 || contributionFlatTotal <= 0) {
      uniqueChanceRoll = 0;
      uniqueChancePercent = 0;
      contributionPercent = 0;
    } else {
      uniqueChanceRoll = (int) Math.ceil(43.0 * (100.0 / contributionPercent));
      uniqueChancePercent = (1.0 / (double) uniqueChanceRoll) * 100.0;
      contributionPercent =
          ((double) contributionFlatOwn / (double) contributionFlatTotal) * 100.0;
    }
  }
}