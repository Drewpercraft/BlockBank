package com.drewpercraft.blockbank.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drewpercraft.blockbank.BlockBank;

public class CommandBalanceTop implements CommandExecutor {

	private final BlockBank plugin;
	private final Logger log;
	
	public CommandBalanceTop(BlockBank plugin) {
		this.plugin = plugin;
		this.log = this.plugin.getLogger();
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,	String[] arg3) {
		// TODO Auto-generated method stub
		log.info(String.format("CommandBalanceTop 1: %s  2: %s  3: %s", arg1, arg2, arg3));
		return false;
	}

}
