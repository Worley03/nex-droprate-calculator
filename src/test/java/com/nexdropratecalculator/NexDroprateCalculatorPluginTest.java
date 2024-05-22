package com.nexdropratecalculator;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NexDroprateCalculatorPluginTest {
  public static void main(String[] args) throws Exception {
    ExternalPluginManager.loadBuiltin(NexDroprateCalculatorPlugin.class);
    RuneLite.main(args);
  }
}
