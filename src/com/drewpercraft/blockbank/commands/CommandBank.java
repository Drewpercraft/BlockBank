package com.drewpercraft.blockbank.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.BlockBank;

public class CommandBank implements TabExecutor {

	private final BlockBank plugin;
	
	public CommandBank(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender player, Command command, String label, String[] args)
	{
		List<String> options = new ArrayList<String>();
		if (args.length == 1) {
			Method[] methods = this.getClass().getDeclaredMethods();
			for(Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("subCommand_" + args[0])) {
					options.add(methodName.substring(11));
				}
			}
		}		
		Collections.sort(options);
		return options;
	}
	
	@Override
	public boolean onCommand(CommandSender player, Command command, String label, String[] args) 
	{
		// bank requires at least one extra argument
		if (args.length < 1) return false;

		//Pop the "bank" param out of the args and pass the rest of the params to the appropriate handler		
		Vector<String> params = new Vector<String>(Arrays.asList(args));
		String subParam = "subCommand_" + params.remove(0).toLowerCase();
		plugin.getLogger().info("Looking for " + subParam);
		Method method = getMethodByName(subParam);
		if (method != null) {
			Boolean result = new Boolean(false);
			try {
				result = (Boolean) method.invoke(this, player, params);
			} catch (Exception e) {
				e.printStackTrace();
				plugin.sendMessage(player, "ExceptionMessage");
			} 
			// TODO Add false check to provide better help
			return result.booleanValue();
		}
		return false;
	}
	
	public Method getMethodByName(String name)
	{
		Method[] methods = this.getClass().getDeclaredMethods();
		for(Method method : methods) {
			String methodName = method.getName();
			plugin.getLogger().info("TabComplete: " + methodName);
			if (methodName.startsWith(name)) {
				return method;
			}
		}
		return null;
	}
	
	/*
	 *   /bank announcement [bank name] on|off
	 */
	public boolean subCommand_announcements(CommandSender player, Vector<String> args)
	{
		plugin.getLogger().info("subCommand_announcements");
		
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() == 0) {
			plugin.getLogger().info("Missing status");
			return false;
		}
		
		if (args.size() == 1) {
			//TODO Check to see if a player is in a branch, otherwise
			//choose the global setting
			args.insertElementAt("global", 0);
		}
		
		boolean setting = Utils.getBoolean(args.get(1));
		String status;
		if (setting) {
			status = plugin.getMessage("Enabled");
		}else{
			status = plugin.getMessage("Disabled");
		}
		if (args.get(0).equalsIgnoreCase("global")) {
			plugin.getConfig().set("announcements", setting);
			plugin.getLogger().info("Global Announcements set to " + status);
			plugin.sendMessage(player, "GlobalSetAnnouncements", status);
		}else{
			
		}
		return true;
	}
	
	public boolean subCommand_atm(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_create(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_loan(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() == 0) {
			plugin.getLogger().info("Missing loan rate");
			return false;
		}
		
		double setting = Utils.getDouble(args.get(0));
		
		String status = String.format("%.2f", setting);
		plugin.getConfig().set("loanRate", setting);
		plugin.getLogger().info("Global Announcements set to " + status);
		plugin.sendMessage(player, "GlobalSetAnnouncements", status);
		return true;
	}
	
	public boolean subCommand_reload(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		plugin.reloadConfig();
		if (plugin.loadConfiguration()) {
			player.sendMessage(plugin.getMessage("ReloadSuccess"));
		}else{
			player.sendMessage(plugin.getMessage("ReloadFail"));
		}		
		return true;
	}
	
	public boolean subCommand_remove(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}

	public boolean subCommand_savings(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_vaults(CommandSender player, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		return false;
	}
	
}
