package com.tsccoding.tnttag;

import com.tsccoding.tnttag.PlayerData.PlayerManager;
import com.tsccoding.tnttag.commands.Commands;
import com.tsccoding.tnttag.game.GameManager;
import com.tsccoding.tnttag.game.GameMechanics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TNTMain extends JavaPlugin {

    private static  TNTMain instance;
	public HashMap<UUID,PlayerManager> playermanager = new HashMap<UUID,PlayerManager>();
	public ArrayList<UUID> playersInGame = new ArrayList<>();

	public GameMechanics gameMechanics;
	public GameManager gameManager;
	public Commands commands;
	public PlayerScoreboard playerScoreboard;
	
	public void onEnable() {
	    setInstance(this);
		loadConfig();
		instanceClasses();
        gameManager.setupGame();
		commands.onEnable();

		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nTNT RUN has been Enabled\n\n");
		getServer().getPluginManager().registerEvents(new GameMechanics(), this);
	}

    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> gameManager.gameStop(player));
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nTNT RUN has been Disabled\n\n");
	}

    public static TNTMain getInstance() {
        return instance;
    }

    private static void setInstance(TNTMain instance) {
        TNTMain.instance = instance;
    }

	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	private void instanceClasses(){
		gameMechanics = new GameMechanics();
		gameManager = new GameManager();
		playerScoreboard = new PlayerScoreboard();
		commands = new Commands();
	}

}
