package com.drewpercraft.blockbank;

import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.drewpercraft.Utils;
import com.drewpercraft.WorldGuard;

public class Branch {
	
	private BlockBank plugin;
	private final Logger log;
	private final Bank bank;
	private final String name;
	
	private ConfigurationSection config = null;
	
	public Branch(Bank bank, String name)
	{
		this.plugin = bank.getPlugin();
		this.log = plugin.getLogger();
		this.bank = bank;
		this.name = name;
		loadConfiguration();
	}
	
	public void loadConfiguration()
	{
		log.info(String.format("\tLoading Branch: %s", getName()));
		ConfigurationSection branches = bank.getConfig().getConfigurationSection("branches");
		if (branches == null) {
			branches = bank.getConfig().createSection("branches");
		}
		config = branches.getConfigurationSection(name);
		if (config == null) {			
			config = bank.getConfig().getConfigurationSection("branches").createSection(name);
			config.set("announcements", bank.getConfig().getBoolean("announcements", true));
			config.set("openHour", bank.getConfig().getInt("openHour", 8));
			config.set("closeHour", bank.getConfig().getInt("closeHour", 17));
			config.set("maxVaults", bank.getConfig().getInt("maxVaults", 10));
			config.set("title", name);
			plugin.saveConfig();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s branch of %s", config.getString("title"), bank);
	}

	public boolean isPlayerInBranch(OfflinePlayer offlinePlayer)
	{
		return WorldGuard.isPlayerInRegion(offlinePlayer, name);
	}

	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return this.bank;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	public String getTitle() {
		return this.config.getString("title", getName());
	}
	
	public String getWorld() {
		return this.config.getString("world", "Unknown");
	}
	/**
	 * @param title the display name of this branch
	 */
	public void setTitle(String title) {
		//TODO Check to make sure we're not renaming this bank to a name already in use
		//TODO Implement setting error
		if (title.length() < 1) return;
		this.config.set("title", title);
		this.plugin.saveConfig();
	}
	
	public void setWorld(String world) {
		//TODO worldName should be verified
		this.config.set("world", world);
		this.plugin.saveConfig();
	}
	/**
	 * @return the announcements
	 */
	public boolean isAnnouncements() {
		return this.config.getBoolean("announcements", this.plugin.getDefaultAnnouncements());
	}

	/**
	 * @param announcements the announcements to set
	 */
	public void setAnnouncements(boolean announcements) {
		this.config.set("announcements", announcements);
		this.plugin.saveConfig();
	}
	/**
	 * @return the openHour
	 */
	public int getOpenHour() {
		return this.config.getInt("openHour", this.plugin.getDefaultOpenHour());
	}
	/**
	 * @param openHour the openHour to set
	 */
	public void setOpenHour(int openHour) {
		if (openHour < 0 || openHour > 23) return;
		this.config.set("openHour", openHour);
		this.plugin.saveConfig();
	}
	/**
	 * @return the closeHour
	 */
	public int getCloseHour() {
		return this.config.getInt("closeHour", this.plugin.getDefaultCloseHour());
	}
	/**
	 * @param closeHour the closeHour to set
	 */
	public void setCloseHour(int closeHour) {
		if (closeHour < 1 || closeHour > 24) return;
		this.config.set("closeHour", closeHour);
		this.plugin.saveConfig();
	}

	public boolean isOpen() {
		World world = plugin.getServer().getWorld(this.config.getString("world"));
		int hour = Utils.GetWorldHour(world.getTime());
		return (hour >= this.config.getInt("openHour") && (hour < this.config.getInt("closeHour")));
	}
	
	public boolean isClosed() {
		return !isOpen();
	}
	
	
}
