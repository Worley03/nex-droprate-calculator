package com.nexdropratecalculator;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
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

@Slf4j
@PluginDescriptor(name = "Nex Droprate Calculator")
public class NexDroprateCalculatorPlugin extends Plugin {
  @Inject private Client client;

  @Inject private ClientToolbar clientToolbar;

  private NexDroprateCalculatorPanel panel;

  private NavigationButton navButton;

  @Inject private NexDroprateCalculatorConfig config;

  private int ownContribution = 0;
  private int totalContribution = 0;

  private boolean inFight = false;
  private boolean inFightInit = false;

  private boolean isMVP = false;
  private boolean minContribution = false;
  private int waitTicks = 0;
  private boolean dumpResults = false;

  @Override
  protected void startUp() throws Exception {
    panel = injector.getInstance(NexDroprateCalculatorPanel.class);
    panel.init();

    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

    navButton =
        NavigationButton.builder().tooltip("Info").icon(icon).priority(10).panel(panel).build();

    clientToolbar.addNavigation(navButton);
  }

  @Override
  protected void shutDown() {
    panel.deinit();
    clientToolbar.removeNavigation(navButton);
    panel = null;
    navButton = null;
  }

  @Subscribe
  public void onGameTick(GameTick tick) {
    Player player = client.getLocalPlayer();
    WorldPoint location = player.getWorldLocation();

    if (location.getX() >= 6550
        && location.getX() <= 6578
        && location.getY() >= 1606
        && location.getY() <= 1632) {
      // Player is in nex arena
      NPC nex =
          client.getNpcs().stream()
              .filter(npc -> npc.getId() >= 11278 && npc.getId() <= 11282)
              .findFirst()
              .orElse(null);

      if (nex != null) inFight = true;
      else inFight = false;

      if (inFight) {
        if (!inFightInit) {
          waitTicks = 2;
          dumpResults = true;
          inFightInit = true;
          isMVP = false;
          minContribution = false;
          ownContribution = 0;
          totalContribution = 0;
        }
        int players =
            (int)
                client.getPlayers().stream()
                    .filter(
                        pla ->
                            player.getWorldLocation().getX() >= 6550
                                && player.getWorldLocation().getX() <= 6578
                                && player.getWorldLocation().getY() >= 1606
                                && player.getWorldLocation().getY() <= 1632)
                    .count();
        panel.updateValues(ownContribution, totalContribution, players, isMVP, minContribution, 1);
        ownContribution = 0;
        totalContribution = 0;
        return;
      }

      if (waitTicks > 0) {
        waitTicks--;
      } else {
        if (dumpResults) {
          panel.updateValues(0, 0, 0, isMVP, minContribution, 0);
          dumpResults = false;
          inFightInit = false;
        }
      }
    } else {
      if (inFight) {
        panel.updateValues(0, 0, 0, false, false, -1);
      }
      inFight = false;
      inFightInit = false;
    }
  }

  @Subscribe
  public void onChatMessage(ChatMessage chatMessage) {
    Player player = client.getLocalPlayer();
    if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
      log.debug("GAMEMESSAGE LOGGING WORKS");
      String message = chatMessage.getMessage();
      if (message.contains("You were the MVP for this fight")) {
        log.debug("MVP MESSAGE WORKS");
        isMVP = true;
      }
      if (message.contains("received a drop")) {
        log.debug("DROP MESSAGE WORKS");
        minContribution = true;
      }
    }
  }

  @Subscribe
  public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
    if (inFight) {
      if (hitsplatApplied.getActor() instanceof NPC) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        if (hitsplat.isMine()) ownContribution += hitsplat.getAmount();
        totalContribution += hitsplat.getAmount();
      }
    }
  }

  @Provides
  NexDroprateCalculatorConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(NexDroprateCalculatorConfig.class);
  }
}
