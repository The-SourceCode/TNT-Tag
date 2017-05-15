package com.tsccoding.tnttag;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

public class EventsClass implements Listener {

	private MainClass plugin = MainClass.getPlugin(MainClass.class);

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		plugin.playermanager.add(new PlayerManager(uuid, false, 0, false));

	}
	
}
