package com.nexdropratecalculator;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.NpcID;
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

@Slf4j
@PluginDescriptor(name = "Nex Droprate Calculator")
public class NexDroprateCalculatorPlugin extends Plugin {
  @Inject private Client client;
  @Inject private ClientToolbar clientToolbar;
  @Inject private ClientThread clientThread;
  @Inject private ScheduledExecutorService executor;
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
  private int ticksUntilFightStart = -1;
  private static final int DUMP_RESULTS_TICKS = 2;
  private static final int REMOVE_OVERLAY_TICKS = 50;
  private static final int FIGHT_START_DELAY_TICKS = 4; // Fight always starts 4 ticks after Nex shouts 'Fill my soul...'

  private static final String KILL_LOG_FILE_NAME = "nex-kills.log";
  private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private boolean isNexAlive() {
    return client.getVarbitValue(VarbitID.NEX_BARRIER) == 3;
  }

  private void startFight() {
    log.debug("Initializing Nex fight");
    panel.setCurrentRunActive(true);
    ticksUntilOverlayRemoval = -1; // Cancel potential pending countdown
    ownContribution = 0;
    totalContribution = 0;
    overlay.setShouldRender(true);

    int playerCount = (int) client.getTopLevelWorldView().players().stream().count();
    panel.startCurrentRun(playerCount);
  }

  @Override
  protected void startUp() throws Exception {
    log.debug("Starting Nex Droprate Calculator Plugin");

    // Panel
    panel = injector.getInstance(NexDroprateCalculatorPanel.class);
    panel.init();
    // Side panel button
    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    navButton = NavigationButton.builder().tooltip("Nex Calculator").icon(icon).panel(panel).build();
    clientToolbar.addNavigation(navButton);
    // Overlay
    overlayManager.add(overlay);

    // In case plugin was turned on mid-fight, check for Nex's presence
    clientThread.invokeLater(() -> {
      log.debug("Invoking on client thread");
      if (panel == null) {
        return;
      }
      // Nex uses NpcID.NEX only once she's attackable, meaning fight has already started so this check is enough
      if (client.getTopLevelWorldView().npcs().stream().anyMatch(npc -> npc.getId() == NpcID.NEX)) {
        startFight();
      }
    });

    log.debug("Nex Droprate Calculator Plugin started successfully");
  }

  @Override
  protected void shutDown() {
    log.debug("Shutting down Nex Droprate Calculator Plugin");

    // Overlay (it's removed first since it relies on panel - which should not be null)
    overlay.setShouldRender(false);
    overlayManager.remove(overlay);
    // Panel
    panel = null;
    // Side panel button
    clientToolbar.removeNavigation(navButton);
    navButton = null;

    log.debug("Nex Droprate Calculator Plugin shut down successfully");
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
          log.debug("Scheduling finished Nex fight dump in {} ticks", DUMP_RESULTS_TICKS);
          ticksUntilDumpResults = DUMP_RESULTS_TICKS;
          ticksUntilOverlayRemoval = REMOVE_OVERLAY_TICKS;
        } else if (newStatus == 1 || newStatus == 0) {
          // Ignore 2 -> 1 and 2 -> 0 statuses since this usually means player is leaving after a finished fight
          if (lastNexStatus != 2) {
            // Either player left arena while Nex was alive (3 -> 1) or player logged out (X -> 0)
            // Cancel fight status immediately but schedule overlay removal to be done later
            panel.finishCurrentRun(RunState.CANCELLED, 0);
            log.debug("Player left Nex fight, scheduling overlay removal in {} ticks", REMOVE_OVERLAY_TICKS);
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
    if (ticksUntilFightStart > -1) {
      if (ticksUntilFightStart == 0) {
        // Only actually start if the player is still at Nex (they could've left since the delay start)
        if (isNexAlive()) {
          startFight();
        }
      }
      ticksUntilFightStart--;
    }

    if (ticksUntilDumpResults > -1) {
      if (ticksUntilDumpResults == 0) {
        log.debug("Dumping finished Nex fight results");
        panel.finishCurrentRun(RunState.FINISHED, DUMP_RESULTS_TICKS);
        logKillToFile(panel.currentRun);
      }
      ticksUntilDumpResults--;
    }

    if (ticksUntilOverlayRemoval > -1) {
      if (ticksUntilOverlayRemoval == 0) {
        log.debug("30 seconds passed since leaving Nex, removing overlay");
        overlay.setShouldRender(false);
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

    // Use Nex's static lines to determine start of the fight. This solution is better than
    // detecting hitsplats - Nex could be not attacked and her first damaging attack is delayed
    if (message.equals("Nex|Fill my soul with smoke!")) {
      // Nex fight will actually start in a few ticks, delay until that exact moment
      log.debug("Scheduling fight start in {} ticks", FIGHT_START_DELAY_TICKS);
      ticksUntilFightStart = FIGHT_START_DELAY_TICKS;
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
    if (!panel.isCurrentRunActive()) {
      return;
    }

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

  private void logKillToFile(NexDroprateCalculatorRun run) {
    if (!config.logKills()) {
      return;
    }
    Player player = client.getLocalPlayer();
    if (player == null) {
      return;
    }

    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
    String killTime = Utils.formatDuration(run.getTicks());
    String contribution = String.format("%.2f%%", run.getContributionPercent());
    String avgPlayers = String.format("%.2f", run.calculateAvgPlayers());

    String line = String.join(",", timestamp, killTime, contribution, avgPlayers);

    executor.execute(() -> {
      // First, ensure subdirectories for plugin and user exist
      Path path = null;
      try {
        path = Files.createDirectories(Paths.get(RuneLite.RUNELITE_DIR.getPath(), NexDroprateCalculatorConfig.GROUP, player.getName()));
      } catch (IOException e) {
        log.error("Failed to create directories");
        return; // early out and don't attempt to write to log file
      }

      boolean logFileExists = Files.exists(path.resolve(KILL_LOG_FILE_NAME));
      try (PrintWriter writer = new PrintWriter(new FileWriter(path.resolve(KILL_LOG_FILE_NAME).toString(), true))) {
        if (!logFileExists) {
          writer.println("timestamp,kill_time,contribution,avg_players");
        }
        writer.println(line);
        log.debug("Logged kill to file: {}", line);
      } catch (IOException e) {
        log.error("Failed to write to kill log", e);
      }
    });
  }

  @Provides
  NexDroprateCalculatorConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(NexDroprateCalculatorConfig.class);
  }
}