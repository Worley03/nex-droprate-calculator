package com.nexdropratecalculator;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
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
  private NexDroprateCalculatorPanel panel;
  private NavigationButton navButton;
  @Inject private OverlayManager overlayManager;
  @Inject private NexDroprateCalculatorOverlay overlay;
  @Inject private NexDroprateCalculatorConfig config;

  private int ownContribution = 0;
  private int totalContribution = 0;
  private boolean inFight = false;
  private boolean inFightInit = false;
  private boolean isMVP = false;
  private boolean minContribution = false;
  private int waitTicks = 0;
  private boolean dumpResults = false;
  private boolean AtNex = false;

  @Override
  protected void startUp() throws Exception {
    log.debug("Starting Nex Droprate Calculator Plugin");

    panel = injector.getInstance(NexDroprateCalculatorPanel.class);
    panel.init();

    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    navButton = NavigationButton.builder().tooltip("Nex Calculator").icon(icon).panel(panel).build();

    clientToolbar.addNavigation(navButton);

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
    if (event.getKey().equals("showOverlay")) {
      if (config.showOverlay() && (AtNex))
      {
        overlayManager.add(overlay);
      } else {
        overlayManager.remove(overlay);
      }
    }
  }

  @Subscribe
  public void onGameTick(GameTick tick) {
    if (config.showOverlay() && (AtNex))
    {
      overlayManager.add(overlay);
    }

    NPC nex = client.getNpcs().stream()
            .filter(npc -> npc.getId() >= 11278 && npc.getId() <= 11282)
            .findFirst()
            .orElse(null);

    inFight = nex != null;
    log.debug("inFight status: {}", inFight);

    if (inFight) {
      if (!inFightInit) {
        log.debug("Initializing fight");
        waitTicks = 2;
        dumpResults = true;
        inFightInit = true;
        isMVP = false;
        minContribution = false;
        ownContribution = 0;
        totalContribution = 0;
        AtNex = true;
        overlayManager.add(overlay);
      }

      int players = (int) client.getPlayers().stream().count();
      log.debug("Number of players in fight: {}", players);
      panel.updateValues(ownContribution, totalContribution, players, isMVP, minContribution, 1);
      overlay.updateValues(ownContribution, totalContribution, players, isMVP, minContribution, 1);
      ownContribution = 0;
      totalContribution = 0;
      return;
    }

    if (waitTicks > 0) {
      waitTicks--;
      log.debug("Waiting ticks: {}", waitTicks);
    } else {
      if (dumpResults) {
        log.debug("Dumping results");
        panel.updateValues(0, 0, 0, isMVP, minContribution, 0);
        overlay.updateValues(0, 0, 0, isMVP, minContribution, 0);
        dumpResults = false;
        inFightInit = false;
      }
    }

    if (inFight) {
      log.debug("Exiting fight");
      panel.updateValues(0, 0, 0, false, false, -1);
      overlay.updateValues(0, 0, 0, false, false, -1);
    }
    inFight = false;
    inFightInit = false;
    AtNex = false;
  }


  @Subscribe
  public void onChatMessage(ChatMessage chatMessage) {
    Player player = client.getLocalPlayer();
    log.debug("Received chat message: {}", chatMessage.getMessage());

    if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
      if (chatMessage.getMessage().contains("You were the MVP for this fight")) {
        log.debug("MVP message detected");
        isMVP = true;
      }
      if (chatMessage.getMessage().contains("received a drop")) {
        log.debug("Drop message detected");
        minContribution = true;
      }
    }
  }

  @Subscribe
  public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
    if (inFight) {
      if (hitsplatApplied.getActor() instanceof NPC) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        if (hitsplat.isMine()) {
          log.debug("Hitsplat applied to me: {}", hitsplat.getAmount());
          ownContribution += hitsplat.getAmount();
        }
        totalContribution += hitsplat.getAmount();
        log.debug("Total contribution updated: {}", totalContribution);
      }
    }
  }

  @Provides
  NexDroprateCalculatorConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(NexDroprateCalculatorConfig.class);
  }
}