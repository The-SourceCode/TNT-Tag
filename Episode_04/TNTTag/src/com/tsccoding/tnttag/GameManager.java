package com.tsccoding.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager implements Listener {

	private MainClass plugin = MainClass.getPlugin(MainClass.class);

	private int lobbyCountdown = 10;
	private int explosionCountdown = 30;
	private int playerNeeded = 2;
	private boolean isStarted;

	Location lobbySpawn;
	Location gameSpawn;

	public void setupGame() {
		this.gameSpawn = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("GameSpawn.world")),
				plugin.getConfig().getDouble("GameSpawn.X"), plugin.getConfig().getDouble("GameSpawn.Y"),
				plugin.getConfig().getDouble("GameSpawn.Z"));

		this.lobbySpawn = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("LobbySpawn.world")),
				plugin.getConfig().getDouble("LobbySpawn.X"), plugin.getConfig().getDouble("LobbySpawn.Y"),
				plugin.getConfig().getDouble("LobbySpawn.Z"));
	}

	public void lobbyWait(Player player) {
		int online = Bukkit.getOnlinePlayers().size();
		Bukkit.getServer().broadcastMessage(online + "");
		playerCheck(online);
	}

	public void gameStart() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(gameSpawn);
			player.setWalkSpeed(4);
		}

		explosionCountdown();
	}

	public void gameStop() {
		// implement bungeecord
	}

	public void playerCheck(int online) {
		if (online == playerNeeded) {
			lobbyCountdown();
			setStarted(true);
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
