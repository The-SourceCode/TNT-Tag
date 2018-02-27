package com.tsccoding.tnttag.commands;

import com.tsccoding.tnttag.TNTMain;

public class Commands {
    private TNTMain plugin = TNTMain.getInstance();

    public String cmd1 = "lobby";
    public String cmd2 = "game";
    public String cmd3 = "force";

    public void onEnable(){
        registerCommands();
    }

    private void registerCommands() {
        this.plugin.getCommand(cmd1).setExecutor(new LobbyCommands());
        this.plugin.getCommand(cmd2).setExecutor(new GameCommands());
        this.plugin.getCommand(cmd3).setExecutor(new ForceStart());
    }
}
