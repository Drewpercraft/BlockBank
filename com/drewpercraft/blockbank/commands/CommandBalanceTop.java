package com.drewpercraft.blockbank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drewpercraft.blockbank.BlockBank;

public class CommandBalanceTop implements CommandExecutor {

	private final BlockBank plugin;
	
	public CommandBalanceTop(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		// TODO Auto-generated method stub
		this.plugin.getLogger().info("BlockBank: command BalanceTop");
		return false;
	}

}
