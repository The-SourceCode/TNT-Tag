package com.tsccoding.tnttag.game;

import com.tsccoding.tnttag.PlayerData.PlayerManager;
import com.tsccoding.tnttag.TNTMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

public class GameManager implements Listener {

    private TNTMain plugin = TNTMain.getInstance();

    private int lobbyCountdown = 10;
    public int explosionCountdown = 30;
    public int playersNeeded = 11;
    public boolean isStarted = false;

    public Location lobbySpawn;
    public Location gameSpawn;

    public void setupGame() {
        if (plugin.getConfig().contains("gameSpawn")) {
            this.gameSpawn = (Location) plugin.getConfig().get("gameSpawn");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Game Spawn Located");
        }

        if (plugin.getConfig().contains("lobbySpawn")) {
            this.lobbySpawn = (Location) plugin.getConfig().get("lobbySpawn");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Lobby Spawn Located");
        }

        playerCheck(Bukkit.getOnlinePlayers().size());
        for (Player online : Bukkit.getOnlinePlayers()) {
            plugin.playersInGame.add(online.getUniqueId());
            plugin.playermanager.put(online.getUniqueId(),
                    new PlayerManager(online.getUniqueId(), false, 0, false, false));
            lobbyWait(online);
            online.setFoodLevel(20);
            online.setHealth(20);
            plugin.playerScoreboard.scoreLobby(online);
            online.teleport(lobbySpawn);
        }
    }

    public void lobbyWait(Player player) {
        int online = Bukkit.getOnlinePlayers().size();
        player.sendMessage("§eThere are current §f" + online + "§f/" + playersNeeded);
        playerCheck(online);
    }

    public void gameStart() {
        isStarted = true;
        explosionCountdown();
        plugin.gameMechanics.tntPlacer();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setWalkSpeed(.5f);
            player.setInvulnerable(false);
            player.getInventory().clear();
            player.setPlayerListName(ChatColor.GREEN + player.getName());
            player.teleport(gameSpawn);
        });

    }

    public void gameStop(Player player) {
        player.setWalkSpeed(.2f);
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        player.getInventory().setHelmet(null);
        player.getInventory().clear();

        player.setGameMode(GameMode.ADVENTURE);
        isStarted = false;
        plugin.playersInGame.clear();
        plugin.playermanager.clear();

        player.setPlayerListName(ChatColor.GREEN + player.getName());

        if (lobbySpawn != null) {
            player.teleport(lobbySpawn);
        }
    }

    public boolean playerCheck(int online) {
        if (online >= playersNeeded) {
            if (!isStarted) {
                lobbyCountdown();
                setStarted(true);
            }
            return true;
        } else {
            return false;
        }
    }

    public void explosionCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (explosionCountdown > 0) {
                    explosionCountdown--;

                    Bukkit.getOnlinePlayers().forEach(player -> plugin.playerScoreboard.scoreGame(player, explosionCountdown));

                } else {
                    plugin.gameMechanics.tntCheck(this);
                }
            }

        }.runTaskTimer(plugin, 0, 20);

    }

    public void lobbyCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (lobbyCountdown > 0) {
                    if (playerCheck(plugin.playersInGame.size())) {
                        lobbyCountdown--;
                        Bukkit.getServer().broadcastMessage("§eThe game will start in §f" + lobbyCountdown + " seconds");
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.playSound(online.getLocation(), Sound.BLOCK_NOTE_PLING, 2, 2);
                        }
                    } else {
                        Bukkit.getServer().broadcastMessage("§ePlayer(s) left. Until §f" + playersNeeded + "§e players are online the game will not start");
                        this.cancel();
                        lobbyCountdown = 10;
                    }
                } else {
                    this.cancel();
                    gameStart();
                    Bukkit.getServer().broadcastMessage("§eGoodluck!");
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

}
