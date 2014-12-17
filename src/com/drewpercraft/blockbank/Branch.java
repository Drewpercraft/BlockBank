package com.drewpercraft.blockbank;

import java.util.logging.Logger;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldedit.regions.Region;

public class Branch {
	
	private BlockBank plugin;
	private final Logger log;
	private final Bank bank;
	private final UUID id;
	private Region region = null;
	
	private ConfigurationSection config = null;
	
	public Branch(Bank bank, UUID id)
	{
		this.plugin = bank.getPlugin();
		this.log = plugin.getLogger();
		this.bank = bank;
		this.id = id;
		loadConfiguration();
	}
	
	public void loadConfiguration()
	{
		this.config = this.bank.getConfig().getConfigurationSection("branches").getConfigurationSection(this.id.toString());
		log.info(String.format("\tLoading Branch: %s", getName()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s branch of %s, name, bank");
	}


	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return bank;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.config.getString("name", "Un-named Bank");
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		//TODO Check to make sure we're not renaming this bank to a name already in use
		//TODO Implement setting error
		if (name.length() < 1) return;
		this.config.set("name", name);
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
		if (closeHour < 0 || closeHour > 23) return;
		this.config.set("closeHour", closeHour);
		this.plugin.saveConfig();
	}
	/**
	 * @return the region
	 */
	public Region getRegion() {
		return region;
	}
	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.config.set("region", region);
		this.plugin.saveConfig();
	}
	
	
}
