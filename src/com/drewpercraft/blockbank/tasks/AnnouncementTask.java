package com.drewpercraft.blockbank.tasks;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Branch;

public class AnnouncementTask extends BukkitRunnable {

	private final BlockBank plugin;
	private final String worldName;
	private int lastHourChecked;
	
	public AnnouncementTask(BlockBank plugin, String worldName) {
		this.plugin = plugin;
		this.worldName = worldName;
		this.lastHourChecked = 0;
	}

	@Override
	public void run() {
		World world = plugin.getServer().getWorld(worldName);
		int hour = Utils.GetWorldHour(world.getTime());
		if (hour != lastHourChecked) {
			plugin.getLogger().fine("Running Announcement Task for " + worldName + " hour: " + hour + " lastHourChecked: " + lastHourChecked);
			Map<String, Bank> banks = plugin.getBanks();
			for(Iterator<String> bankNameIT = banks.keySet().iterator(); bankNameIT.hasNext();) {
				String bankName = bankNameIT.next();
				Map<String, Branch> branches = banks.get(bankName).getBranches();
				for(Iterator<String> branchNameIT = branches.keySet().iterator(); branchNameIT.hasNext();) {
					String branchName = branchNameIT.next();
					Branch branch = branches.get(branchName);
					if (branch.getWorld().equals(worldName)) {
						int openHour = branch.getOpenHour();
						int closeHour = branch.getCloseHour();
						if (branch.isAnnouncements()) {
							if (openHour == hour) {
								plugin.broadcastMessage("BranchOpening", branches.get(branchName).getTitle());
							}
							if (closeHour == hour) {
								plugin.broadcastMessage("BranchClosing", branches.get(branchName).getTitle());
							}
						}
					}
				}
			}
		}
		lastHourChecked = hour;
	}
}
