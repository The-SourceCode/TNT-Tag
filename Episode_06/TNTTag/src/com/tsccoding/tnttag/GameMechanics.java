package com.tsccoding.tnttag;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

public class GameMechanics implements Listener {

	private MainClass plugin = MainClass.getPlugin(MainClass.class);

	private int taggersSelected;

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		
		if (plugin.gameManager.isStarted() == false) {
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			if (plugin.playersLeftGame.contains(player)) {
				// TODO bungee push back
			}
			event.setJoinMessage("");
			plugin.playermanager.put(uuid, new PlayerManager(uuid, false, 0, false, false));
			plugin.playersInGame.add(player);
			plugin.gameManager.lobbyWait(player);
		} else {
			// TODO bungee push back
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		event.setQuitMessage("");
		plugin.playermanager.remove(uuid);
		plugin.playersInGame.remove(player);
		plugin.playersLeftGame.add(player);
	}

	@EventHandler
	public void playerTNTTag(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			event.setDamage(0);

			Player tagger = (Player) event.getDamager();
			UUID taggerUUID = tagger.getUniqueId();
			PlayerManager taggerPlayerManager = plugin.playermanager.get(taggerUUID);

			Player tagged = (Player) event.getEntity();
			UUID taggedUUID = tagged.getUniqueId();
			PlayerManager taggedPlayerManager = plugin.playermanager.get(taggedUUID);
			
			if(taggerPlayerManager.isHasTNT() == true && taggedPlayerManager.isHasTNT() == true){
				event.setCancelled(true);
				tagger.sendMessage("§cThis player already has TNT");
				return;
			}

			if(taggerPlayerManager.isHasTNT() == true){
				taggerPlayerManager.setHasTNT(false);
				tagger.getInventory().setHelmet(null);
				tagger.getInventory().clear();
				
				taggedPlayerManager.setHasTNT(true);
				tagged.getInventory().setHelmet(new ItemStack(Material.TNT));
				tagged.playSound(tagger.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
				tagged.sendMessage("§cYou Have Been Tagged!!!!");
				tagged.getInventory().addItem(new ItemStack(Material.TNT, 576));
			}
		}
	}

	public void tntCheck(Player player) {
	}

	public void tntPlacer() {
		// TODO Auto-generated method stub
		
	}

}
