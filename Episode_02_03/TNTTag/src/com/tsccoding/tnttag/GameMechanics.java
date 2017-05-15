package com.tsccoding.tnttag;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

public class GameMechanics implements Listener {

	private MainClass plugin = MainClass.getPlugin(MainClass.class);

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		plugin.playermanager.put(uuid, new PlayerManager(uuid, false, 0, false,false));
		plugin.gameManager.lobbyWait(player);

	}

	public void tntCheck(Player player) {
		// TODO Auto-generated method stub
	}

}
