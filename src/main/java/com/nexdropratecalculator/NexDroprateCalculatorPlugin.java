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
  private NexDroprateCalculatorPanel panel;
  private NavigationButton navButton;
  @Inject private OverlayManager overlayManager;
  @Inject private NexDroprateCalculatorOverlay overlay;
  @Inject private NexDroprateCalculatorConfig config;

  private int ownContribution = 0;
  private int totalContribution = 0;
  private boolean isMVP = false;              // Whether player is deemed MVP of the finished fight (updated at the end of the fight)
  private boolean minContribution = false;    // Whether player qualifies for minimal contribution (updated at the end of the fight)
  private int ticksUntilDumpResults = -1;     // Time in ticks until dumping fight results
  private int ticksUntilOverlayRemoval = -1;  // Time in ticks until hiding overlay
  private int lastNexStatus = 0;              // Default value of VarbitID.NEX_BARRIER

  private boolean isInFight() {
    return client.getVarbitValue(VarbitID.NEX_BARRIER) == 3;
  }

  @Override
  protected void startUp() throws Exception {
    log.debug("Starting Nex Droprate Calculator Plugin");

    panel = injector.getInstance(NexDroprateCalculatorPanel.class);
    panel.init();

    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    navButton = NavigationButton.builder().tooltip("Nex Calculator").icon(icon).panel(panel).build();

    clientToolbar.addNavigation(navButton);

    clientThread.invoke(() -> {
      if (config.showOverlay() && isInFight()) {
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
    if (event.getGroup().equals("nex-droprate-calculator") && (event.getKey().equals("showOverlay"))) {
      if (config.showOverlay() && isInFight()) {
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
      if (newStatus == 3) {
        // Player is within Nex's arena and Nex either just spawned or player joined during her spawn animation
        log.debug("Initializing Nex fight");
        isMVP = false;
        minContribution = false;
        ownContribution = 0;
        totalContribution = 0;
        // Schedule dump in 2 ticks // open question: should dump be scheduled on 3 -> 1 (player tp's out/dies)?
        log.debug("Scheduling dump in 2 ticks");
        ticksUntilDumpResults = 2;
        if (config.showOverlay()) {
          overlayManager.add(overlay);
        }
      } else if (lastNexStatus == 3 && (newStatus == 2 || newStatus == 1)) {
        // Either Nex just died (3 -> 2) or player left arena while Nex was alive (3 -> 1)
        // Schedule overlay removal in 30 seconds
        log.debug("Scheduling overlay removal in 50 ticks");
        ticksUntilOverlayRemoval = 50;
      }
      // Store previous value of Nex's status
      lastNexStatus = event.getValue();
    }
  }

  @Subscribe
  public void onGameTick(GameTick tick) {
    if (isInFight()) {
      int playerCount = (int) client.getTopLevelWorldView().players().stream().count();
      panel.updateValues(ownContribution, totalContribution, playerCount, isMVP, minContribution, 1);
      overlay.updateValues(ownContribution, totalContribution, playerCount, isMVP, minContribution, 1);
      ownContribution = 0;
      totalContribution = 0;
      ticksUntilOverlayRemoval = -1; // Cancel countdown since we're in the fight
      return;
    }

    // Player is not in a fight - delay executions by decrementing values until they reach 0
    if (ticksUntilDumpResults > -1) {
      if (ticksUntilDumpResults == 0) {
        log.debug("Dumping Nex fight results");
        int playerCount = (int) client.getTopLevelWorldView().players().stream().count();
        panel.updateValues(0, 0, 0, isMVP, minContribution, 0);
        overlay.updateValues(0, 0, playerCount, isMVP, minContribution, 0);
      }
      ticksUntilDumpResults--;
    }

    if (ticksUntilOverlayRemoval > -1) {
      if (ticksUntilOverlayRemoval == 0) {
        log.debug("30 seconds passed since leaving Nex, removing overlay");
        overlayManager.remove(overlay);
        panel.updateValues(0, 0, 0, false, false, -1);
        overlay.updateValues(0, 0, 0, false, false, -1);
      }
      ticksUntilOverlayRemoval--;
    }
  }

  @Subscribe
  public void onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
      if (chatMessage.getMessage().equals("You were the MVP for this fight!")) {
        isMVP = true;
        log.debug("Nex MVP message detected");
      }
      // Checking for Nex-specific drop message ensures we don't faultily assume player qualifying
      // for minimal contribution in the event of someone in the arena receiving a unique drop
      if (chatMessage.getMessage().startsWith("<col=ef0083>Nex dropped:")) {
        minContribution = true;
        log.debug("Nex drop message detected");
      }
    }
  }

  @Subscribe
  public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
    if (isInFight()) {
      if (hitsplatApplied.getActor() instanceof NPC) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        if (hitsplat.isMine()) {
          ownContribution += hitsplat.getAmount();
          log.debug("Hitsplat applied to me: {}", hitsplat.getAmount());
        }
        if (hitsplat.getHitsplatType() != HitsplatID.HEAL) {
          totalContribution += hitsplat.getAmount();
          log.debug("Total contribution updated: {}", totalContribution);
        }
      }
    }
  }

  @Provides
  NexDroprateCalculatorConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(NexDroprateCalculatorConfig.class);
  }
}