package com.drewpercraft.blockbank.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
		if (args.length == 0) {
			Method[] methods = this.getClass().getDeclaredMethods();
			for(Method method : methods) {
				String methodName = method.getName();
				plugin.getLogger().info("TabComplete: " + methodName);
				if (methodName.startsWith("subCommand_")) {
					options.add(methodName.substring(12));
				}
			}
		}
		return options;
	}
	
	@Override
	public boolean onCommand(CommandSender player, Command command, String notused,	String[] args) 
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
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public boolean subCommand_announcements(CommandSender player, Vector<String> args)
	{
		plugin.getLogger().info("subCommand_announcements");
		
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() == 0) {
			plugin.getLogger().info("Missing status");
			return false;
		}
		
		boolean setting = Utils.getBoolean(args.get(0));
		String status;
		if (setting) {
			status = plugin.getMessage("Enabled");
		}else{
			status = plugin.getMessage("Disabled");
		}
		plugin.getConfig().set("announcements", setting);
		plugin.getLogger().info("Global Announcements set to " + status);
		player.sendMessage(plugin.getMessage("GlobalSetAnnouncements", status));
		return true;
	}
	
	public boolean subCommand_atm(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_create(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_loan(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_reload(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_remove(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}

	public boolean subCommand_savings(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_vaults(CommandSender player, String[] args)
	{
		if (Utils.PermissionCheckFailed(player, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		return false;
	}
	
}
