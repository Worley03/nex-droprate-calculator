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
  private int playersMax;           // amount of players who were present at the start of the kill
  private int playersCurrent;       // amount of players currently in the room (can only be lower than the max)
  private int playersWeightedValue; // stores sum of players present each tick for weighted player count calculation
  private boolean isMinimumContribution;
  private boolean isMVP;

  public NexDroprateCalculatorRun(Utils.RUN_STATES stateToSet) {
    this.state = stateToSet;
  }

  public void updateRun(int ownContribution, int totalContribution, int players) {
    ticks++;

    playersCurrent = players;
    playersWeightedValue += players;

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

  public double calculateAvgPlayers() {
    if (ticks == 0) {
      return 0;
    }
    return (double) playersWeightedValue / ticks;
  }
}