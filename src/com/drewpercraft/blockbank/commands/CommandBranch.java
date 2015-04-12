package com.drewpercraft.blockbank.commands;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Branch;
import com.drewpercraft.blockbank.WorldGuard;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class CommandBranch implements TabExecutor {

	private final BlockBank plugin;
	
	public CommandBranch(BlockBank plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = (Player) sender;
		if (player == null) return null;

		plugin.getLogger().info("tab completer for " + label + " called with " + args.length + " arguments");
		for(String arg : args) {
			plugin.getLogger().info("arg: " + arg);
		}
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
			if (args[0].equalsIgnoreCase("create")) {
				options.addAll(plugin.getBanks().keySet());
			}
		}
		
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("create")) {
				Map<String, ProtectedRegion> regions = WGBukkit.getRegionManager(player.getWorld()).getRegions();
				options.addAll(regions.keySet());
			}
		}
		Collections.sort(options);
		return options;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		// branch requires at least one extra argument
		if (args.length < 1) return false;

		//Pop the "branch" param out of the args and pass the rest of the params to the appropriate handler		
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
	 *      2: world name (optional)
	 */
	public Boolean subCommand_create(CommandSender sender, Vector<String> args)
	{		
		OfflinePlayer player = (OfflinePlayer) sender;
		if (player.getPlayer() == null) {
			plugin.sendMessage(sender, "ConsoleNotAllowed");
			return true;
		}
		
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() < 2) return false;
		
		String bankName = args.get(0);
		String regionName = args.get(1);
		
		//Make the world an optional parameter
		String worldName = player.getPlayer().getWorld().getName();
		if (args.size() == 3) {
			worldName = args.get(2);
		}
		
		Bank bank = plugin.getBank(args.get(0));
		if (bank == null) {
			plugin.sendMessage(sender, "BankDoesNotExist", bankName);
			return true;
		}
		
		if (!WorldGuard.isValidRegion(worldName, regionName)) {
			plugin.sendMessage(sender, "RegionDoesNotExist", worldName, regionName);
			return true;
		}
		
		if (plugin.getBankByRegion(worldName, regionName) != null) {
			plugin.sendMessage(sender, "BranchAlreadyExists");
			return true;
		}
		
		bank.createBranch(worldName, regionName);
		plugin.sendMessage(sender, "BranchCreated", bank.getName(), regionName);
		return true;
	}
	
	public Boolean subCommand_debug(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		OfflinePlayer player = (OfflinePlayer) sender;
		
		Bank bank = plugin.getPlayerBank(player);
		plugin.getLogger().info("Player in bank: " + (bank != null));
		
		Branch branch = plugin.getPlayerBranch(player);
		plugin.getLogger().info("Player in branch: " + (branch != null));
		
		if (bank != null && branch != null) {
			plugin.getLogger().info("Bank Title: " + bank.getTitle());
			plugin.getLogger().info("Branch title: " + branch.getTitle());
		}
		return true;
	}
	
	/*
	 * Param list:
	 * 		0: page (optional)
	 */
	public Boolean subCommand_list(CommandSender sender, Vector<String> args)
	{	
		
		int page = 1;
		if (args.size() > 0) {
			page = Utils.getInt(args.get(0));
		}
		int index = (page - 1) * 10;
		if (index < 0) index = 0;
		
		Set<String> bankNames = plugin.getBanks().keySet();
		List<String> branchNames = new ArrayList<String>();

		for(String bankName : bankNames) {
			for(String branchName : plugin.getBank(bankName).getBranches().keySet()) {
				branchNames.add(plugin.getBank(bankName).getTitle() + " / " + plugin.getBank(bankName).getBranch(branchName).getTitle());
			}
		}
		
		Collections.sort(branchNames);
		
		int pageCount = (branchNames.size() / 10) + 1;
		
		int lastIndex = index + 10;
		if (lastIndex > branchNames.size()) {
			lastIndex = branchNames.size();
		}
		
		plugin.getLogger().info(String.format("Showing branches #%d to %d", index+1, lastIndex));
		plugin.sendMessage(sender, "BranchListHeader", page, pageCount);
		for(int i = index; i < lastIndex; i++ ) {			
			plugin.sendMessage(sender, "BranchListEntry", i+1, branchNames.get(i));
		}
		return true;
	}
			
}
