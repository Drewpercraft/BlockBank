package com.drewpercraft;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WorldGuard {

	private WorldGuard() 
	{

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
	
	public static boolean isValidRegion(String worldName, String regionName)
	{
		World world = Bukkit.getServer().getWorld(worldName);
		Map<String, ProtectedRegion> regions = WGBukkit.getRegionManager(world).getRegions();		
		return regions.containsKey(regionName);
	}
}
