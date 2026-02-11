package com.nexdropratecalculator;

import lombok.Data;

@Data
public class NexDroprateCalculatorRun {
  private RunState state;

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

  public NexDroprateCalculatorRun(RunState stateToSet) {
    this.state = stateToSet;
  }

  public void updateRun(int ownContribution, int totalContribution, int players) {
    ticks++;

    playersCurrent = players;
    playersWeightedValue += players;

    // Add to own and total contribution values
    contributionFlatOwn += ownContribution;
    contributionFlatTotal += totalContribution;

    if (contributionFlatOwn > 0 && contributionFlatTotal > 0) {
      contributionPercent = (double) contributionFlatOwn / contributionFlatTotal * 100.0;
      updateUniqueChance(contributionPercent);
    }
  }

  // After run finishes, we always wait few ticks before actually stopping it - this function accounts for that
  public void adjustAfterFinish(int ticksToSubtract) {
    ticks -= ticksToSubtract;
    playersWeightedValue -= playersCurrent * ticksToSubtract; // player count can't change within ticks after nex dies
  }

  public void updateUniqueChance(double contribPercent) {
    uniqueChanceRoll = (int) Math.ceil(43.0 * (100.0 / contribPercent));
    uniqueChancePercent = 1.0 / uniqueChanceRoll * 100.0;
  }

  public double calculateAvgPlayers() {
    if (ticks == 0) {
      return 0;
    }
    return (double) playersWeightedValue / ticks;
  }
}