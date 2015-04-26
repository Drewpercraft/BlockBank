package com.drewpercraft.blockbank.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.blockbank.BlockBank;

public class CommandBorrow implements TabExecutor {

private final BlockBank plugin;
	
	public CommandBorrow(BlockBank plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender player, Command command, String alias, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//TODO Borrow Command
		plugin.sendMessage(sender, "CommandNotImplemented");
		return true;
	}

}
