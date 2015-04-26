package com.drewpercraft.blockbank.tasks;

import java.util.concurrent.Callable;

import org.bukkit.entity.Player;

public class KickPlayerTask implements Callable<Void> {

	Player player;
	String message;
	
	public KickPlayerTask(Player p, String m) { 
		player = p;
		message = m;
	}
	
	@Override
	public Void call() {
		player.kickPlayer(message);
		return null;
	}

}
