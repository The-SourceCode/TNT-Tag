package com.tsccoding.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

import net.md_5.bungee.api.ChatColor;

public class GameManager implements Listener {

	private MainClass plugin = MainClass.getPlugin(MainClass.class);

	private int lobbyCountdown = 10;
	private int explosionCountdown = 30;
	public int playersNeeded = 2;
	private boolean isStarted;

	Location lobbySpawn;
	Location gameSpawn;

	public void setupGame() {
		if (plugin.getConfig().contains("GameSpawn")) {
			this.gameSpawn = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("GameSpawn.world")),
					plugin.getConfig().getDouble("GameSpawn.X"), plugin.getConfig().getDouble("GameSpawn.Y"),
					plugin.getConfig().getDouble("GameSpawn.Z"), (float) plugin.getConfig().getDouble("GameSpawn.yaw"),
					(float) plugin.getConfig().getDouble("GameSpawn.pitch"));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Game Spawn Located");
		}

		if (plugin.getConfig().contains("LobbySpawn")) {
			this.lobbySpawn = new Location(
					Bukkit.getServer().getWorld(plugin.getConfig().getString("LobbySpawn.world")),
					plugin.getConfig().getDouble("LobbySpawn.X"), plugin.getConfig().getDouble("LobbySpawn.Y"),
					plugin.getConfig().getDouble("LobbySpawn.Z"),
					(float) plugin.getConfig().getDouble("LobbySpawn.yaw"),
					(float) plugin.getConfig().getDouble("LobbySpawn.pitch"));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Lobby Spawn Located");
		}

		for (Player online : Bukkit.getOnlinePlayers()) {
			plugin.playersInGame.add(online);
			plugin.playermanager.put(online.getUniqueId(),
					new PlayerManager(online.getUniqueId(), false, 0, false, false));
			lobbyWait(online);

			online.setFoodLevel(20);
			online.setHealth(20);

			playerScoreboard.scoreLobby(online);
		}
	}

	public void lobbyWait(Player player) {
		int online = Bukkit.getOnlinePlayers().size();
		player.sendMessage(ChatColor.YELLOW + "There are current §7(§f" + online + "§7/§f" + playersNeeded
				+ "§7) §fplayers online");
		playerCheck(online);
	}

	public void gameStart() {
		explosionCountdown();
		plugin.gameMechanics.tntPlacer();

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setWalkSpeed(.5f);
			player.teleport(gameSpawn);
			player.getInventory().clear();
		}

	}

	public void gameStop(Player player) {
		// implement bungeecord
		player.setWalkSpeed(.2f);
		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		player.getInventory().setHelmet(null);
		player.getInventory().clear();

		player.setGameMode(GameMode.ADVENTURE);

		plugin.playersInGame.clear();
		plugin.playermanager.clear();

		player.setPlayerListName(ChatColor.WHITE + player.getName());
	}

	public boolean playerCheck(int online) {
		if (online >= playersNeeded) {
			if (isStarted == false) {
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
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (explosionCountdown > 0) {
						explosionCountdown = explosionCountdown - 1;
					} else {
						plugin.gameMechanics.tntCheck(player);
						explosionCountdown = 30;
					}
				}
			}

		}.runTaskTimerAsynchronously(plugin, 0, 20l);

	}

	public void lobbyCountdown() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (lobbyCountdown > 0) {
					lobbyCountdown = lobbyCountdown - 1;
				} else {
					gameStart();
					this.cancel();
				}

			}
		}.runTaskTimerAsynchronously(plugin, 0, 20l);
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

}
