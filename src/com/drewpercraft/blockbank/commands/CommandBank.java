package com.drewpercraft.blockbank.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;

public class CommandBank implements TabExecutor {

	private final BlockBank plugin;
	
	public CommandBank(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		plugin.getLogger().info("tab completer for " + label + " called ");
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
		
		if (args.length == 2) {
			if (args[0].equals("announcements")) {
				// Provide a list of banks + "global"
				options.add(plugin.getMessage("global"));
			}
			
			if (args[0].equals("create")) {
				options.addAll(plugin.getValidBankNames());
			}
		}
		Collections.sort(options);
		return options;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
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
				result = (Boolean) method.invoke(this, sender, params);
			} catch (Exception e) {
				e.printStackTrace();
				plugin.sendMessage(sender, "ExceptionMessage");
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
			if (methodName.startsWith(name)) {
				return method;
			}
		}
		return null;
	}
	
	/*
	 *   /bank announcement [bank name] on|off
	 */
	public boolean subCommand_announcements(CommandSender sender, Vector<String> args)
	{
		plugin.getLogger().info("subCommand_announcements");
		
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
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
			plugin.setConfig("announcements", setting);
			plugin.getLogger().info("Global Announcements set to " + status);
			plugin.sendMessage(sender, "GlobalSetAnnouncements", status);
		}else{
			//TODO Get the bank specified and set its announcements flag
		}
		return true;
	}
	
	public boolean subCommand_atm(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		//TODO
		return false;
	}
	
	public boolean subCommand_create(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		if (args.size() == 0) return false;
		
		String bankName = args.get(0).toLowerCase();
		List<String> validBankNames = plugin.getValidBankNames();
		if (validBankNames.contains(bankName)) {
			if (plugin.getBank(bankName) == null) {
				Bank bank = new Bank(plugin, bankName);
				plugin.addBank(bank);
				plugin.sendMessage(sender,  "BankCreated", bankName);
			}else{
				plugin.sendMessage(sender, "BankAlreadyExists", bankName);
			}
		}
		return true;
	}
	
	public boolean subCommand_list(CommandSender sender, Vector<String> args)
	{
		Map<String, Bank> banks = plugin.getBanks();
		List<String> message = new ArrayList<String>();
		message.addAll(banks.keySet());
		plugin.sendMessage(sender, "BankList", message);
		return true;
	}
	
	public boolean subCommand_loan(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() == 0) {
			plugin.getLogger().info("Missing loan rate");
			return false;
		}
		
		double setting = Utils.getDouble(args.get(0));
		
		String status = String.format("%.2f", setting);
		plugin.getConfig().set("loanRate", setting);
		plugin.getLogger().info("Global Announcements set to " + status);
		plugin.sendMessage(sender, "GlobalSetAnnouncements", status);
		return true;
	}
	
	public boolean subCommand_reload(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		plugin.reloadConfig();
		if (plugin.loadConfiguration()) {
			sender.sendMessage(plugin.getMessage("ReloadSuccess"));
		}else{
			sender.sendMessage(plugin.getMessage("ReloadFail"));
		}		
		return true;
	}
	
	public boolean subCommand_remove(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}

	public boolean subCommand_savings(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		return false;
	}
	
	public boolean subCommand_vaults(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		return false;
	}
	
}
