package com.tsccoding.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class playerScoreboard {

	public static ScoreboardManager m;
	public static Scoreboard b;
	public static Objective o;
	public static Score gameMode;
	public static Score time;
	public static Score coins;
	public static Score alive;
	public static Score dead;
	
	
	private static MainClass plugin = MainClass.getPlugin(MainClass.class);
	
	public static void scoreGame(Player player, int timeLeft){
		m = Bukkit.getScoreboardManager();
		b = m.getNewScoreboard();
		o = b.registerNewObjective("tnttag", "");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.GOLD + "THESOURCECODE");
		
		if(plugin.playersInGame.size() == 1){
			time = o.getScore(ChatColor.WHITE + "Time: " + ChatColor.GREEN + "Complete");
			time.setScore(4);
			return;
		}else{
			time = o.getScore(ChatColor.WHITE + "Time: " + ChatColor.GREEN + timeLeft);
			time.setScore(4);
		}
		coins = o.getScore(ChatColor.WHITE + "Coins: " + ChatColor.GREEN + "100");
		coins.setScore(3);
		
		gameMode = o.getScore(ChatColor.WHITE + "Game: " + ChatColor.GREEN + "TNT TAG");
		gameMode.setScore(2);
		
		alive = o.getScore(ChatColor.WHITE + "Alive: " + ChatColor.GREEN + plugin.playersInGame.size());
		alive.setScore(1);
		
		dead = o.getScore(ChatColor.WHITE + "Alive: " + ChatColor.GREEN + (Bukkit.getOnlinePlayers().size() - plugin.playersInGame.size()));
		dead.setScore(0);
		
		player.setScoreboard(b);
	}
	
	public static void scoreLobby(Player player){
		m = Bukkit.getScoreboardManager();
		b = m.getNewScoreboard();
		o = b.registerNewObjective("tnttag", "");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.GOLD + "THESOURCECODE");
		
		time = o.getScore(ChatColor.WHITE + "Time: " + ChatColor.GRAY + "Not Started");
		time.setScore(4);
		
		
		
		gameMode = o.getScore(ChatColor.WHITE + "Game: " + ChatColor.GREEN + "TNT TAG");
		gameMode.setScore(3);
		
		coins = o.getScore(ChatColor.WHITE + "Coins: " + ChatColor.GREEN + "100");
		coins.setScore(2);
		
		alive = o.getScore(ChatColor.WHITE + "Players: " + ChatColor.GREEN + plugin.playersInGame.size() + "" + ChatColor.WHITE + "/" + ChatColor.GREEN + plugin.gameManager.playerNeeded);
		alive.setScore(1);
		
		player.setScoreboard(b);
	}
}
