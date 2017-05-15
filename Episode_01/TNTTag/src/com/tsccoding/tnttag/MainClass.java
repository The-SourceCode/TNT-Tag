package com.tsccoding.tnttag;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.tsccoding.tnttag.PlayerData.PlayerManager;

public class MainClass extends JavaPlugin {

	public ArrayList<PlayerManager> playermanager = new ArrayList<PlayerManager>();
	
	public void onEnable() {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nTNT RUN has been Enabled\n\n");
		getServer().getPluginManager().registerEvents(new EventsClass(), this);
		loadConfig();
	}

	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nTNT RUN has been Disabled\n\n");
	}

	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

}
