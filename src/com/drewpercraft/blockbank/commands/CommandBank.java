package com.drewpercraft.blockbank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drewpercraft.blockbank.BlockBank;

public class CommandBank implements CommandExecutor {

	private final BlockBank plugin;
	
	public CommandBank(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,	String[] arg3) {
		// TODO Auto-generated method stub
		this.plugin.getLogger().info(String.format("CommandBank 1: %s  2: %s  3: %s", arg1, arg2, arg3));
		return false;
	}

}
