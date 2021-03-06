package com.drewpercraft.blockbank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.drewpercraft.JSONWriter;

public class Player {

	private final BlockBank plugin;
	private final String filePath;
	private final String filename;
	private final UUID uuid;
	private JSONObject data = null;
	private boolean modified = false;

	
	public Player(BlockBank plugin, UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
		
		this.filePath = plugin.getPlayerDataPath();
		this.filename = this.filePath + File.separator + uuid + ".json";
		load();
	}
	
	public static class BalanceCompare implements Comparator<UUID> {

		Map<UUID, Player> base;
		
		public BalanceCompare(Map<UUID, Player> base)
		{
			this.base = base;
		}

		@Override
		public int compare(UUID a, UUID b)
		{
			if (base.get(a).getWorth() <= base.get(b).getWorth()) {
				return -1;
			}
			return 1;
			
		}
	}

	public File getPlayerFile()
	{
		return new File(filename);
	}
	
	public boolean deletePlayerFile()
	{
		plugin.getLogger().info("Deleting " + filename);
		return getPlayerFile().delete();
	}
	
	@SuppressWarnings("unchecked")
	public void load()
	{
		File playerFile = getPlayerFile();
		String playerName = "";
		try
		{
			data = new JSONObject();
			playerName = plugin.getServer().getOfflinePlayer(uuid).getName();
			if (playerName == null) {
				playerName = "Unknown";
			}
			boolean newPlayer = playerFile.createNewFile();
			if (!newPlayer) {
				plugin.getLogger().info("Loading " + playerName + " / " + uuid.toString());
				JSONParser parser = new JSONParser();
				data = (JSONObject) parser.parse(new FileReader(playerFile));
				
				//Upgrade file to new format
				if (!data.containsKey("banks")) {
					plugin.log.info("Upgrading file format for " + playerName);
					JSONObject bankMap = new JSONObject();
					Set<String> banks = plugin.getBanks().keySet();
					for(Iterator<String> bankNameIT = banks.iterator(); bankNameIT.hasNext();) {
						String bankName = bankNameIT.next();
						if (data.containsKey(bankName)) {
							bankMap.put(bankName, (Double) data.get(bankName));
							data.remove(bankName);
						}
					}
					data.put("banks", bankMap);
					save();
				}
			} 
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            plugin.log.warning("Data file for " + playerName + " / " + uuid.toString() + " is corrupt");
            
        }
		if (!data.containsKey("balance")) {
			plugin.log.warning("Data file for " + playerName + " / " + uuid.toString() + " was missing a balance field");
			data.put("balance", 0.0);
		}
		
		if (!data.containsKey("banks")) {
			data.put("banks", new JSONObject());
		}
		
		if (!data.containsKey("offlineInterest")) {
			data.put("offlineInterest", new JSONObject());
		}

		if (!data.containsKey("offlineDividend")) {
			data.put("offlineDividend", 0.0);
		}

		data.put("uuid", uuid.toString());
		//This will catch name updates. The uuid and the player name in the file are not used, 
		//but are there to make data mining easier		
		data.put("playerName", playerName);
	}
	
	public void save()
	{
		File playerFile = getPlayerFile();
		try
		{
			playerFile.createNewFile();
			FileWriter os = new FileWriter(playerFile);
			plugin.getLogger().info("Saving " + getName());
			JSONWriter jWriter = new JSONWriter();
			jWriter.write(data.toString());
			os.write(jWriter.toString());
			jWriter.close();
			os.write(System.lineSeparator());
			os.close();
			modified = false;
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	public boolean isModified()
	{
		return modified;
	}
	
	public String getName()
	{
		return (String) data.get("playerName");
	}
	
	public UUID getUID()
	{
		return uuid;
	}
	
	public double getWorth()
	{
		Set<String> bankNames = plugin.getBanks().keySet();
		double worth = getBalance();
		for(String bankName : bankNames) {
			worth += getBankBalance(bankName);
		}
		return worth;
	}
	
	public double getBalance()
	{
		Double balance = (Double) data.get("balance");
		return balance.doubleValue();
	}
	
	@SuppressWarnings("unchecked")
	public void setBalance(double amount)
	{
		//Round amount to the correct number of decimals
		BigDecimal balance = new BigDecimal(amount).setScale(plugin.getDecimals(), RoundingMode.HALF_EVEN);
		data.put("balance", (double) balance.doubleValue());
		modified = true;
	}

	/*
	 *  Withdraw allows for overdraft
	 */
	public double withdraw(double amount) 
	{
		double newBalance = getBalance() - amount;
		setBalance(newBalance);
		return newBalance;
	}
	
	public double deposit(double amount) 
	{
		double newBalance = getBalance() + amount;
		setBalance(newBalance);
		return newBalance;
	}
	
	public double getBankBalance(String bankName)
	{
		JSONObject bankMap = (JSONObject) data.get("banks");
		if (bankMap.containsKey(bankName)) {
			Double balance = (Double) bankMap.get(bankName);
			return balance.doubleValue();
		}
		return 0.0;
	}
	
	@SuppressWarnings("unchecked")
	public void setBankBalance(String bankName, double amount)
	{
		JSONObject bankMap = (JSONObject) data.get("banks");
		BigDecimal balance = new BigDecimal(amount).setScale(plugin.getDecimals(), RoundingMode.HALF_EVEN);
		bankMap.put(bankName, (double) balance.doubleValue());
		data.put("banks", bankMap);
		modified = true;
	}
	
	public boolean bankWithdraw(String bankName, double amount) 
	{
		if (amount > getBankBalance(bankName)) return false;
		double newBankBalance = getBankBalance(bankName) - amount;
		double newBalance = getBalance() + amount;
		setBankBalance(bankName, newBankBalance);
		setBalance(newBalance);
		if (plugin.getLogTransactions()) {
			plugin.getLogger().info(String.format("Withdraw Bank %s: %s %s", bankName, plugin.getServer().getPlayer(uuid).getName(), plugin.getVaultAPI().format(amount)));
		}
		return true;
	}
	
	public boolean bankDeposit(String bankName, double amount) 
	{
		if (amount > getBalance()) return false;
		double newBankBalance = getBankBalance(bankName) + amount;
		double newBalance = getBalance() - amount;
		setBankBalance(bankName, newBankBalance);
		setBalance(newBalance);
		if (plugin.getLogTransactions()) {
			plugin.getLogger().info(String.format("Deposit Bank %s: %s %s", bankName, plugin.getServer().getPlayer(uuid).getName(), plugin.getVaultAPI().format(amount)));
		}
		return true;
	}
	
	public double getOfflineDividend()
	{
		if (data.containsKey("offlineDividend")) {
			Double offlineDividend = (Double) data.get("offlineDividend");
			return offlineDividend.doubleValue();
		}
		return 0.0;
	}
	
	@SuppressWarnings("unchecked")
	public void addOfflineDividend(double amount)
	{
		double previousOfflineDividend = 0.0;
		if (data.containsKey("offlineDividend")) {
			previousOfflineDividend = (Double) data.get("offlineDividend");
		}
		data.put("offlineDividend", previousOfflineDividend + amount);
		modified = true;
	}
	
	@SuppressWarnings("unchecked")
	public void resetOfflineDividend()
	{
		data.put("offlineDividend", 0.0);
		modified = true;
	}
	
	
	@SuppressWarnings("unchecked")
	public double getOfflineInterest(String bankName)
	{
		plugin.getLogger().fine("Checking: " + data.toJSONString());
		if (data.containsKey("offlineInterest")) {
			Map<String, Double> offlineInterestMap = (Map<String, Double>) data.get("offlineInterest");
			if (offlineInterestMap.containsKey(bankName)) {
				plugin.getLogger().fine(bankName + ": " + offlineInterestMap.toString());
				Double offlineInterest = offlineInterestMap.get(bankName);
				return offlineInterest.doubleValue();
			}
		}
		return 0.0;
	}
	
	@SuppressWarnings("unchecked")
	public void addOfflineInterest(String bankName, double amount)
	{
		Map<String, Double> offlineInterestMap = null;
		double previousEarnings = 0.0;
		if (data.containsKey("offlineInterest")) {
			offlineInterestMap = (Map<String, Double>) data.get("offlineInterest");
			if (offlineInterestMap.containsKey(bankName)) {
				previousEarnings = offlineInterestMap.get(bankName);
			}
		}else{
			offlineInterestMap = new HashMap<String, Double>();			
		}
		offlineInterestMap.put(bankName, previousEarnings + amount);
		data.put("offlineInterest", offlineInterestMap);
		modified = true;
	}
	
	@SuppressWarnings("unchecked")
	public void resetOfflineInterest(String bankName)
	{
		Map<String, Double> offlineInterestMap = null;
		if (data.containsKey("offlineInterest")) {
			offlineInterestMap = (Map<String, Double>) data.get("offlineInterest");
		}else{
			offlineInterestMap = new HashMap<String, Double>();
		}			
		offlineInterestMap.put(bankName, 0.0);
		data.put("offlineInterest", offlineInterestMap);
		modified = true;
	}
}
