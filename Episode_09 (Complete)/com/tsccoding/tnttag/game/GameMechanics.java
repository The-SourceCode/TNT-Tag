package com.tsccoding.tnttag.game;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.tsccoding.tnttag.PlayerScoreboard;
import com.tsccoding.tnttag.TNTMain;
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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

import net.md_5.bungee.api.ChatColor;

public class GameMechanics implements Listener {

    private TNTMain plugin = TNTMain.getPlugin(TNTMain.class);

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        if (!plugin.gameManager.isStarted) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            plugin.playermanager.put(uuid, new PlayerManager(uuid, false, 0, false, false));
            plugin.playersInGame.add(uuid);
            plugin.gameManager.lobbyWait(player);

            Bukkit.getOnlinePlayers().forEach(online -> plugin.playerScoreboard.scoreLobby(online));

            if (plugin.gameManager.lobbySpawn != null) {
                player.teleport(plugin.gameManager.lobbySpawn);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        event.setQuitMessage("");
        if (plugin.playermanager.containsKey(uuid) && plugin.playersInGame.contains(uuid)) {
            plugin.playermanager.remove(uuid);
            plugin.playersInGame.remove(uuid);
        }
    }

    @EventHandler
    public void playerTagPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            event.setDamage(0);

            if (plugin.gameManager.isStarted) {
                Player tagger = (Player) event.getDamager();
                UUID taggerUUID = tagger.getUniqueId();
                PlayerManager taggerPlayerManager = plugin.playermanager.get(taggerUUID);

                Player tagged = (Player) event.getEntity();
                UUID taggedUUID = tagged.getUniqueId();
                PlayerManager taggedPlayerManager = plugin.playermanager.get(taggedUUID);

                if (taggerPlayerManager.isHasTNT() && taggedPlayerManager.isHasTNT()) {
                    event.setCancelled(true);
                    tagger.sendMessage(ChatColor.RED + "This player already has TNT");
                    return;
                }

                if (taggerPlayerManager.isHasTNT()) {
                    taggerPlayerManager.setHasTNT(false);
                    tagger.getInventory().setHelmet(null);
                    tagger.getInventory().clear();
                    tagger.sendMessage(ChatColor.YELLOW + "You have tagged " + ChatColor.RED + tagged.getName());
                    tagger.setPlayerListName(ChatColor.GREEN + tagger.getName());

                    tagged.setPlayerListName(ChatColor.RED + tagged.getName());
                    tagged.sendMessage(ChatColor.YELLOW + "You have been tagged by " + ChatColor.GREEN + tagger.getName());
                    taggedPlayerManager.setHasTNT(true);
                    tagged.getInventory().setHelmet(new ItemStack(Material.TNT));
                    tagged.playSound(tagger.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
                    tagged.getInventory().addItem(new ItemStack(Material.TNT, 576));
                }
            }
        }
    }

    public void tntCheck(BukkitRunnable runnable) {

        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            if (plugin.playersInGame.contains(uuid)) {
                PlayerManager playerManager = plugin.playermanager.get(uuid);
                if (playerManager.isHasTNT()) {

                    playerManager.setHasTNT(false);
                    playerManager.setIsdead(true);
                    Location playerLocation = player.getLocation();
                    playerLocation.getWorld().createExplosion(playerLocation.getX(), playerLocation.getY(),
                            playerLocation.getZ(), 1, false, false);
                    player.setPlayerListName(ChatColor.GRAY + player.getName());
                    player.getInventory().setHelmet(null);
                    player.setGameMode(GameMode.SPECTATOR);

                    plugin.playersInGame.remove(uuid);
                    plugin.playerScoreboard.scoreGame(player, plugin.gameManager.explosionCountdown);
                    player.sendMessage(ChatColor.RED + "BOOM! Players have exploded! TNT has been placed.");

                }
            }
        });


        if (plugin.playersInGame.size() == 1) {
            Player last = Bukkit.getPlayer(plugin.playersInGame.get(0));
            Bukkit.getServer().broadcastMessage(ChatColor.GREEN + last.getName() + ChatColor.YELLOW + " has won! They have been rewarded " + ChatColor.GOLD + "6100 Coins");
            runnable.cancel();
            Bukkit.getOnlinePlayers().forEach(player -> {
                plugin.gameManager.gameStop(player);
            });
        } else {
            plugin.gameManager.explosionCountdown = 20;
            plugin.gameMechanics.tntPlacer();
        }
    }

    public void tntPlacer() {
        int taggers = 1;
        while (taggers <= (plugin.playersInGame.size() / 2)) {

            System.out.println(plugin.playersInGame.size() + " DIVIDE: " + plugin.playersInGame.size() / 2 + "TAGGERS: "  + taggers);
            Random random = new Random();
            int index = random.nextInt(plugin.playersInGame.size());
            Player randomPlayer = Bukkit.getPlayer(plugin.playersInGame.get(index));
            PlayerManager taggerPlayerManager = plugin.playermanager.get(randomPlayer.getUniqueId());

            if (!taggerPlayerManager.isIsdead() && !taggerPlayerManager.isHasTNT()) {
                taggerPlayerManager.setHasTNT(true);
                randomPlayer.getInventory().setHelmet(new ItemStack(Material.TNT));
                randomPlayer.updateInventory();
                randomPlayer.setPlayerListName(ChatColor.RED + randomPlayer.getName());
                randomPlayer.getInventory().addItem(new ItemStack(Material.TNT, 576));
                randomPlayer.updateInventory();
                randomPlayer.sendMessage(ChatColor.RED + "You're it! Tag someone before times runs out!");

            }

            taggers++;
        }

    }

    @EventHandler
    public void editInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            UUID uuid = event.getWhoClicked().getUniqueId();
            PlayerManager playerManager = plugin.playermanager.get(uuid);
            if (playerManager.isHasTNT()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.gameManager.isStarted) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void placeBlockEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
