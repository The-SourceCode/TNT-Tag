package com.tsccoding.tnttag;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

import net.md_5.bungee.api.ChatColor;

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
			playerScoreboard.scoreLobby(player);
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

			if (taggerPlayerManager.isHasTNT() == true && taggedPlayerManager.isHasTNT() == true) {
				event.setCancelled(true);
				tagger.sendMessage("§cThis player already has TNT");
				return;
			}

			if (taggerPlayerManager.isHasTNT() == true) {
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

	public void tntCheck() {

		for (PlayerManager playerManager : plugin.playermanager.values()) {
			if (playerManager.isHasTNT() == true) {

				playerManager.setHasTNT(false);
				playerManager.setIsdead(true);

				Player player = Bukkit.getPlayer(playerManager.getUuid());
				plugin.playersInGame.remove(player);

				Location playerLocation = player.getLocation();
				playerLocation.getWorld().createExplosion(playerLocation.getX(), playerLocation.getY(),
						playerLocation.getZ(), 1, false, false);
				player.setPlayerListName(ChatColor.GRAY + player.getName());
				player.getInventory().setHelmet(null);
				player.setGameMode(GameMode.SPECTATOR);

			}
		}
	}

	public void tntPlacer() {
		
		new BukkitRunnable(){

			@Override
			public void run() {
				
				if(taggersSelected != 3){
					
					Player randomPlayer = plugin.playersInGame.get(ThreadLocalRandom.current().nextInt(plugin.playersInGame.size()));
					PlayerManager taggerPlayerManager = plugin.playermanager.get(randomPlayer.getUniqueId());
					
					if(taggerPlayerManager.isIsdead() != true && taggerPlayerManager.isHasTNT() != true){
						taggerPlayerManager.setHasTNT(true);
						randomPlayer.getInventory().setHelmet(new ItemStack(Material.TNT));
						randomPlayer.setPlayerListName(ChatColor.RED + randomPlayer.getName());
						randomPlayer.getInventory().addItem(new ItemStack(Material.TNT, 576));
						randomPlayer.sendMessage("§cYou're it!§e Tag someone before times runs out!");
					}
					
				}else{
					this.cancel();
					taggersSelected = 0;
				}
			}
			
		}.runTaskTimer(plugin, 20, 20);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		if(plugin.gameManager.isStarted() == true){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void placeBlockEvent(BlockPlaceEvent event){
		event.setCancelled(true);
	}
}
