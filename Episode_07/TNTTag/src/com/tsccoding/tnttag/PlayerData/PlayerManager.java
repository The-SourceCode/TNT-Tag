package com.tsccoding.tnttag.PlayerData;

import java.util.UUID;

import org.bukkit.event.Listener;

public class PlayerManager implements Listener {
	
	private UUID uuid;
	private boolean ingame;
	private int coinsearned;
	private boolean isdead;
	private boolean hasTNT;
	
	public PlayerManager(UUID uuid, boolean ingame, int coinsearned, boolean isdead, boolean hasTNT){
		this.setUuid(uuid);
		this.setIngame(ingame);
		this.setCoinsearned(coinsearned);
		this.setIsdead(isdead);
		this.setHasTNT(hasTNT);
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

	public boolean isHasTNT() {
		return hasTNT;
	}

	public void setHasTNT(boolean hasTNT) {
		this.hasTNT = hasTNT;
	}
	

}
