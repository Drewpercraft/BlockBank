package com.drewpercraft.blockbank.tasks;

import java.util.concurrent.Callable;

import org.bukkit.scheduler.BukkitRunnable;

import com.drewpercraft.blockbank.BlockBank;

public class AnnouncementTask extends BukkitRunnable {

	private final BlockBank plugin;
	private final String world;
	
	public AnnouncementTask(BlockBank plugin, String world) {
		this.plugin = plugin;
		this.world = world;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
