package com.drewpercraft.blockbank.commands;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Branch;

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
			if (args[0].equals("create")) {
				List<String> validNames = plugin.getValidBankNames();
				if (validNames != null) {
					options.addAll(validNames);
				}
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
		plugin.getLogger().fine("Looking for " + subParam);
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
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() < 1) {
			args.add("announcements");
			return subCommand_help(sender, args);
		}
		
		String bankName;
		
		if (args.size() == 1) {
			Bank bank = plugin.getPlayerBank((OfflinePlayer) sender);
			if (bank == null) {
				plugin.sendMessage(sender, "PlayerNotInBranch");
				return true;
			}
			bankName = bank.getName();
		}else{
			bankName = args.remove(0);
		}
		
		boolean setting = Utils.getBoolean(args.get(0));

		Bank bank = plugin.getBank(bankName);
		if (bank != null) {
			bank.setAnnouncements(setting);
			plugin.sendMessage(sender, "BankUpdated", bank.getTitle());
		}else{
			plugin.sendMessage(sender, "BankNotFound");
		}
		return true;
	}
	
	public boolean subCommand_create(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		if (args.size() == 0) return false;
		
		String bankName = args.get(0).toLowerCase();
		List<String> validBankNames = plugin.getValidBankNames();
		if (validBankNames == null || validBankNames.contains(bankName)) {
			if (plugin.getBank(bankName) == null) {
				Bank bank = new Bank(plugin, bankName);
				plugin.addBank(bank);
				plugin.sendMessage(sender, "BankCreated", bankName);
			}else{
				plugin.sendMessage(sender, "BankAlreadyExists", bankName);
			}
		}
		return true;
	}

	public boolean subCommand_info(CommandSender sender, Vector<String> args)
	{
		OfflinePlayer offlinePlayer = (OfflinePlayer) sender;
		
		if (offlinePlayer.getPlayer() == null) {
			plugin.sendMessage(sender, "ConsoleNotAllowed");
			return true;
		}
		Branch branch = plugin.getPlayerBranch(offlinePlayer);
		if (branch == null) {
			plugin.sendMessage(sender, "PlayerNotInBranch");
			return true;
		}
		DecimalFormat dFormat = new DecimalFormat("##.##"); 
		plugin.sendMessage(sender, "BankTitle", branch.getBank().getTitle());
		plugin.sendMessage(sender, "BranchTitle", branch.getTitle());
		plugin.sendMessage(sender, "BranchAnnouncements", branch.isAnnouncements());
		plugin.sendMessage(sender, "BranchOpen", Utils.intToTime(branch.getOpenHour()));
		plugin.sendMessage(sender, "BranchClose", Utils.intToTime(branch.getCloseHour()));
		plugin.sendMessage(sender, "BankSavings", dFormat.format(branch.getBank().getSavingsRate()));
		plugin.sendMessage(sender, "BankTotalDeposits", plugin.getVaultAPI().format(branch.getBank().getTotalDeposits()));
		return true;
	}
	
	public boolean subCommand_help(CommandSender sender, Vector<String> args)
	{
		//TODO Bank Help Command
		plugin.sendMessage(sender, "CommandNotImplemented");
		return true;
	}
	/*
	 * Param list:
	 * 		0: page (optional)
	 */
	public boolean subCommand_list(CommandSender sender, Vector<String> args)
	{	
		
		int page = 1;
		if (args.size() > 0) {
			page = Utils.getInt(args.get(0));
		}
		int index = (page - 1) * 10;
		if (index < 0) index = 0;
		
		List<String> bankNames = new ArrayList<String>();
		bankNames.addAll(plugin.getBanks().keySet());
		
		Collections.sort(bankNames);
		
		int pageCount = (bankNames.size() / 10) + 1;
		
		int lastIndex = index + 10;
		if (lastIndex > bankNames.size()) {
			lastIndex = bankNames.size();
		}
		
		String title = plugin.getMessage("Bank") + " " + plugin.getMessage("List");
		plugin.sendMessage(sender, "ListHeader", title, page, pageCount);
		for(int i = index; i < lastIndex; i++ ) {			
			String bankName = bankNames.get(i);
			plugin.sendMessage(sender, "ListEntry", i+1, plugin.getBank(bankName) + " (" + bankName+ ")");
		}
		return true;
	}
	
	public boolean subCommand_loan(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() < 1) {
			args.add("loan");
			return subCommand_help(sender, args);
		}
		
		//TODO If only one argument passed try to get the bank the player is standing in
		if (args.size() < 2) return false;
		
		String bankName = args.remove(0);
		double rate = Utils.getDouble(args.remove(0));
		if (rate < 0) {
			plugin.sendMessage(sender, "NegativeAmountUsed");
			return true;
		}
		
		Bank bank = plugin.getBank(bankName);
		if (bank != null) {
			bank.setLoanRate(rate);
			plugin.sendMessage(sender, "BankUpdated", bank.getTitle());
		}else{
			plugin.sendMessage(sender, "BankNotFound");
		}
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
		//TODO Bank Remove Command
		plugin.sendMessage(sender, "CommandNotImplemented");
		return true;
	}

	public boolean subCommand_savings(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;		

		if (args.size() < 1) {
			args.add("savings");
			return subCommand_help(sender, args);
		}
		
		String bankName;
		
		if (args.size() == 1) {
			Bank bank = plugin.getPlayerBank((OfflinePlayer) sender);
			if (bank == null) {
				plugin.sendMessage(sender, "PlayerNotInBranch");
				return true;
			}
			bankName = bank.getName();
		}else{
			bankName = args.remove(0);
		}

		
		double rate = Utils.getDouble(args.remove(0));
		if (rate < 0) {
			plugin.sendMessage(sender, "NegativeAmountUsed");
			return true;
		}
		
		Bank bank = plugin.getBank(bankName);
		if (bank != null) {
			bank.setSavingsRate(rate);
			plugin.sendMessage(sender, "BankUpdated", bank.getTitle());
		}else{
			plugin.sendMessage(sender, "BankNotFound");
		}
		return true;
	}
	
	public boolean subCommand_title(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() < 2) return false;
		//TODO Add Help statement
		//TODO If only one argument passed try to get the bank the player is standing in

		String bankName = args.remove(0);
		String title = StringUtils.join(args, " ");
		Bank bank = plugin.getBank(bankName);
		if (bank != null) {
			bank.setTitle(title);
			plugin.sendMessage(sender, "BankUpdated", bank.getTitle());
		}else{
			plugin.sendMessage(sender, "BankNotFound");
		}
		return true;		
	}
	
	public boolean subCommand_vaults(CommandSender sender, Vector<String> args)
	{
		if (Utils.PermissionCheckFailed(sender, "blockbank.admin", plugin.getMessage("PermissionError"))) return true;
		
		if (args.size() < 2) return false;
		//TODO Add Help statement
		//TODO If only one argument passed try to get the bank the player is standing in

		String bankName = args.remove(0);
		int maxVaults = Utils.getInt(args.remove(0));
		if (maxVaults < 0) {
			plugin.sendMessage(sender, "NegativeAmountUsed");
			return true;
		}
		
		Bank bank = plugin.getBank(bankName);
		if (bank != null) {
			bank.setMaxVaults(maxVaults);
			plugin.sendMessage(sender, "BankUpdated", bank.getTitle());
		}else{
			plugin.sendMessage(sender, "BankNotFound");
		}
		return true;
	}
	
}
