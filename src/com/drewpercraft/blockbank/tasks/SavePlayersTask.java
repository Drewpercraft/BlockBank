package com.drewpercraft.blockbank.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.drewpercraft.blockbank.BlockBank;

public class SavePlayersTask extends BukkitRunnable {

	private final BlockBank plugin;
	
	public SavePlayersTask(BlockBank plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		plugin.getVaultAPI().savePlayers();
	}
}