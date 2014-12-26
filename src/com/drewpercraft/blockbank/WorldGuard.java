package com.drewpercraft.blockbank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WorldGuard {

	private WorldGuard() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isPlayerInRegion(OfflinePlayer offlinePlayer, String regionName)
	{
		Player player = offlinePlayer.getPlayer();
		if (player == null) return false;
		
		ApplicableRegionSet regions = WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		
		for(ProtectedRegion region : regions) {
			if (region.getId().equalsIgnoreCase(regionName)) return true;
		}
		return false;
	}
	
	public static boolean isValidRegion(OfflinePlayer offlinePlayer, String regionName)
	{
		Player player = offlinePlayer.getPlayer();
		if (player == null) return false;
		
		ProtectedRegion region = WGBukkit.getRegionManager(player.getWorld()).getRegion(regionName);
		
		return (region != null);
	}
}