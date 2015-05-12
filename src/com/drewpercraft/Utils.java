package com.drewpercraft;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public final class Utils {

	private Utils() {
		//Singleton implementation
	}

	
	
	public final class Pair<X, Y>
	{
		public final X x;
		public final Y y;
		public Pair(X x, Y y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static OfflinePlayer getPlayerByName(String playerName) 
	{
		OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
		for (OfflinePlayer player : players) {
			if (player.getName().equalsIgnoreCase(playerName)) {
				Bukkit.getServer().getLogger().fine(String.format("Converted %s to %s", playerName, player.getUniqueId()));
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
	
	public static int getInt(String string) {
		int amount;
		try {
			amount = Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			amount = 0;
		}
		return amount;
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
	
	public static boolean getBoolean(String string) {
		/*
		String value = string.toLowerCase();
		
		if (Integer.parseInt("0" + value) != 0 || 
				value.equals("on") || 
				value.startsWith("y") || 
				value.startsWith("t")) 
			return true;
		
		return false;
		*/
		if (string.equalsIgnoreCase("on")) return true;
		if (string.equalsIgnoreCase("yes")) return true;
		if (string.equalsIgnoreCase("enable")) return true;
		if (getInt(string) != 0) return true;
		return false;
	}

	/*
	 * Verify the player has the given permission and if not
	 * inform them of the problem.
	 */
	public static boolean PermissionCheckFailed(CommandSender player, String permission, String message) {
		if (!player.hasPermission(permission)) {
			player.sendMessage(message);
			return true;
		}
		return false;
	}
	
	public static int GetWorldHour(long time)
	{
		int hour = (int) (6 + (time / 1000));
		if (hour >= 24) hour -= 24;
		return hour;
	}
}
