package com.nexdropratecalculator;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.events.ConfigChanged;

@Slf4j
@PluginDescriptor(name = "Nex Droprate Calculator")
public class NexDroprateCalculatorPlugin extends Plugin {
  @Inject private Client client;
  @Inject private ClientToolbar clientToolbar;
  @Inject private ClientThread clientThread;
  protected NexDroprateCalculatorPanel panel;
  private NavigationButton navButton;
  @Inject private OverlayManager overlayManager;
  @Inject private NexDroprateCalculatorOverlay overlay;
  @Inject private NexDroprateCalculatorConfig config;

  private int ownContribution = 0;            // Value of own damage splats (resets each tick during the fight)
  private int totalContribution = 0;          // Value of all damage splats (resets each tick during the fight)
  private int lastNexStatus = 0;              // Default value of VarbitID.NEX_BARRIER

  // Timers for scheduling actions to run in N ticks
  private int ticksUntilDumpResults = -1;     // Time in ticks until dumping fight results
  private int ticksUntilOverlayRemoval = -1;  // Time in ticks until hiding overlay
  private static final int DUMP_RESULTS_TICKS = 2;
  private static final int REMOVE_OVERLAY_TICKS = 50;

  private boolean isNexAlive() {
    return client.getVarbitValue(VarbitID.NEX_BARRIER) == 3;
  }

  private void startFight() {
    panel.setCurrentRunActive(true);
    this.ticksUntilOverlayRemoval = -1; // Cancel countdown since we're in the fight
    log.debug("Initializing Nex fight");
    ownContribution = 0;
    totalContribution = 0;
    if (config.showOverlay()) {
      overlayManager.add(overlay);
    }

    int playerCount = (int) client.getTopLevelWorldView().players().stream().count();
    panel.startCurrentRun(playerCount);
  }

  @Override
  protected void startUp() throws Exception {
    log.debug("Starting Nex Droprate Calculator Plugin");

    panel = injector.getInstance(NexDroprateCalculatorPanel.class);
    panel.init();

    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    navButton = NavigationButton.builder().tooltip("Nex Calculator").icon(icon).panel(panel).build();

    clientToolbar.addNavigation(navButton);

    clientThread.invokeLater(() -> {
      if (config.showOverlay() && isNexAlive()) {
        overlayManager.add(overlay);
      }
    });

    log.debug("Nex Droprate Calculator Plugin started successfully");
  }

  @Override
  protected void shutDown() {
    log.debug("Shutting down Nex Droprate Calculator Plugin");

    panel.deinit();
    clientToolbar.removeNavigation(navButton);
    overlayManager.remove(overlay); // Remove the overlay from the overlay manager
    panel = null;
    navButton = null;

    log.debug("Nex Droprate Calculator Plugin shut down successfully");
  }

  @Subscribe
  public void onConfigChanged(ConfigChanged event) {
    if (!event.getGroup().equals(NexDroprateCalculatorConfig.GROUP)) {
      return;
    }
    if (event.getKey().equals(NexDroprateCalculatorConfig.SHOW_OVERLAY)) {
      if (config.showOverlay() && isNexAlive()) {
        overlayManager.add(overlay);
      } else {
        overlayManager.remove(overlay);
      }
    }
  }

  @Subscribe
  public void onVarbitChanged(VarbitChanged event) {
    if (event.getVarbitId() == VarbitID.NEX_BARRIER) {
      int newStatus = event.getValue();
      log.debug("NEX_BARRIER changed: {} -> {}", lastNexStatus, newStatus);
      // NOTE: It is possible that the varbit will change 0 -> 3 if player last logged out inside Nex's arena with Nex alive
      // hence need for the non-zero check on previous value, otherwise overlay will show up for a few seconds on login in case of 0 -> 3
      if (lastNexStatus != 0) {
        if (lastNexStatus == 3 && newStatus == 2) {
          // Nex just died (3 -> 2)
          // Schedule dump in 2 ticks - we need to delay this to wait for onChatMessage handler which updates mvp & min contribution
          log.debug("Scheduling finished Nex fight dump");
          ticksUntilDumpResults = DUMP_RESULTS_TICKS;
        } else if (newStatus == 1 || newStatus == 0) {
          // Ignore 2 -> 1 and 2 -> 0 statuses since this usually means player is leaving after a finished fight
          if (lastNexStatus != 2) {
            // Either player left arena while Nex was alive (3 -> 1) or player logged out (X -> 0)
            // Cancel fight status immediately but schedule overlay removal to be done later
            panel.finishCurrentRun(Utils.RUN_STATES.CANCELLED);
            log.debug("Player left Nex fight, scheduling overlay removal");
            ticksUntilOverlayRemoval = REMOVE_OVERLAY_TICKS;
          }
        }
      }
      // Store previous value of Nex's status
      lastNexStatus = event.getValue();
    }
  }

  @Subscribe
  public void onGameTick(GameTick tick) {
    // Player is currently in a Nex fight - update data of the ongoing fight each game tick
    if (isNexAlive() && panel.isCurrentRunActive()) {
      int playerCount = (int) client.getTopLevelWorldView().players().stream().count();
      panel.updateCurrentRun(ownContribution, totalContribution, playerCount);
      // Reset tick contributions
      ownContribution = 0;
      totalContribution = 0;
      return;
    }

    // Player is not in a Nex fight - delay executions by decrementing values until they reach 0
    if (ticksUntilDumpResults > -1) {
      if (ticksUntilDumpResults == 0) {
        log.debug("Dumping finished Nex fight results");
        panel.finishCurrentRun(Utils.RUN_STATES.FINISHED);
      }
      ticksUntilDumpResults--;
    }

    if (ticksUntilOverlayRemoval > -1) {
      if (ticksUntilOverlayRemoval == 0) {
        log.debug("30 seconds passed since leaving Nex, removing overlay");
        //overlay.currentRun.updateRun(0, 0, 0); // probably unnecessary since we're removing the overlay anyway
        overlayManager.remove(overlay);
      }
      ticksUntilOverlayRemoval--;
    }
  }

  @Subscribe
  public void onChatMessage(ChatMessage event) {
    if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.NPC_SAY) {
      return;
    }
    String message = event.getMessage();

    // Use Nex's static lines to determine start of the fight
    // This solution is better than detecting hitsplats since someone could trigger start arbitrarily by e.g. drinking a divine potion
    if (message.equals("Nex|Fill my soul with smoke!")) {
      // Skip checking for panel.isCurrentRunActive(), already assume this message means beginning of the fight
      startFight();
    }

    if (message.equals("You were the MVP for this fight!")) {
      panel.setCurrentRunMvp();
      log.debug("Nex MVP message detected");
    }
    // Checking for Nex-specific drop message ensures we don't faultily assume player qualifying
    // for minimal contribution in the event of someone in the arena receiving a unique drop
    if (message.startsWith("<col=ef0083>Nex dropped:")) {
      panel.setCurrentRunMinimumContribution();
      log.debug("Nex drop message detected");
    }

  }

  @Subscribe
  public void onHitsplatApplied(HitsplatApplied event) {
    if (!isNexAlive()) {
      return;
    }

    if (panel.isCurrentRunActive()) {
      if (event.getActor() instanceof NPC) {
        Hitsplat hitsplat = event.getHitsplat();
        if (hitsplat.isMine()) {
          ownContribution += hitsplat.getAmount();
          //log.debug("Hitsplat applied to me: {}", hitsplat.getAmount()); // very spammy
        }
        if (hitsplat.getHitsplatType() != HitsplatID.HEAL) {
          totalContribution += hitsplat.getAmount();
          //log.debug("Total contribution updated: {}", totalContribution); // very spammy
        }
      }
    }
  }

  @Provides
  NexDroprateCalculatorConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(NexDroprateCalculatorConfig.class);
  }
}