package com.drewpercraft;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class Utils {

	private Utils() {
		// TODO Auto-generated constructor stub
	}

	public static OfflinePlayer getPlayerByName(String playerName) 
	{
		OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
		for (OfflinePlayer player : players) {
			if (player.getName().equalsIgnoreCase(playerName)) {
				Bukkit.getServer().getLogger().info(String.format("Converted %s to %s", playerName, player.getUniqueId()));
				return player;				
			}
		}
		Bukkit.getServer().getLogger().info("Player " + playerName + " not found.");
		return null;
	}
	
	public static String intToTime(int value) 
	{
		int hour = value;
		String ampm = "am";
		if (hour > 12) {
			hour -= 12;
			if (hour != 12) ampm = "pm";
		}
		if (hour == 0) hour = 12;
		return String.format("%d:00%s", hour, ampm);
	}
	
	public static String getPossessive(String string) 
	{
		if (string.endsWith("s")) {
			return string + "'";
		}
		return string + "'s";	
	}
	
	public static double getDouble(String string) {
		double amount;
		try {
			amount = Double.parseDouble(string);
		}
		catch (NumberFormatException e)
		{
			amount = 0;
		}
		return amount;
	}
	
	
}
