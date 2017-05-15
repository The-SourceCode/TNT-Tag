package com.tsccoding.tnttag.PlayerData;

import java.util.UUID;

import org.bukkit.event.Listener;

public class PlayerManager implements Listener {
	
	private UUID uuid;
	private boolean ingame;
	private int coinsearned;
	private boolean isdead;
	
	public PlayerManager(UUID uuid, boolean ingame, int coinsearned, boolean isdead){
		this.setUuid(uuid);
		this.setIngame(ingame);
		this.setCoinsearned(coinsearned);
		this.setIsdead(isdead);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isIngame() {
		return ingame;
	}

	public void setIngame(boolean ingame) {
		this.ingame = ingame;
	}

	public int getCoinsearned() {
		return coinsearned;
	}

	public void setCoinsearned(int coinsearned) {
		this.coinsearned = coinsearned;
	}

	public boolean isIsdead() {
		return isdead;
	}

	public void setIsdead(boolean isdead) {
		this.isdead = isdead;
	}
	

}
