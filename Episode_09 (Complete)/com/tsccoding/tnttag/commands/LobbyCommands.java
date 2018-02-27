package com.tsccoding.tnttag.commands;

import com.tsccoding.tnttag.TNTMain;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommands implements CommandExecutor {

    private TNTMain plugin = TNTMain.getInstance();
    private Commands commands = plugin.commands;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase(commands.cmd1)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location pLoc = player.getLocation();
                if (!player.isOp()) return true;
                if (args.length == 1) {
                    if(args[0].equalsIgnoreCase("spawn")){
                        plugin.getConfig().set("lobbySpawn", pLoc);
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Config set for Lobby Spawn");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Not enough arguments.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            }
        }
        return true;
    }
}
