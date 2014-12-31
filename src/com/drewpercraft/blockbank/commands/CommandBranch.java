package com.drewpercraft.blockbank.commands;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.WorldGuard;

public final class CommandBranch implements TabExecutor {

	private final BlockBank plugin;
	
	public CommandBranch(BlockBank plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		// branch requires at least one extra argument
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
	 * Param list:
	 * 		0: bank name
	 * 		1: region name
	 */
	public Boolean subParam_create(CommandSender sender, Vector<String> args)
	{		
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() < 2) return false;
		String bankName = args.get(0);
		String regionName = args.get(1);
		
		Bank bank = plugin.getBank(args.get(0));
		if (bank == null) {
			plugin.sendMessage(sender, "BankDoesNotExist", bankName);
			return true;
		}
		
		OfflinePlayer player = Utils.getPlayerByName(sender.getName());
		if (!WorldGuard.isValidRegion(player, regionName)) {
			plugin.sendMessage(sender, "RegionDoesNotExist", regionName);
			return true;
		}
		
		if (player.getPlayer() == null) {
			plugin.sendMessage(sender, "ConsoleNotAllowed");
			return true;
		}
		
		if (plugin.getBankByRegion(regionName) != null) {
			plugin.sendMessage(sender, "BranchAlreadyExists");
			return true;
		}
		
		bank.createBranch(regionName);
		plugin.sendMessage(sender, "BranchCreated", bank.getName(), regionName);
		return true;
	}
}
