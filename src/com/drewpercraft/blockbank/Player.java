package com.drewpercraft.blockbank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Player {

	private final BlockBank plugin;
	private final String filePath;
	private final String filename;
	private final UUID uuid;
	private JSONObject data = new JSONObject();

	
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
	
	@SuppressWarnings("unchecked")
	public void load()
	{
		File playerFile = getPlayerFile();
		String playerName = "";
		try
		{
			boolean newPlayer = playerFile.createNewFile();
			playerName = plugin.getServer().getOfflinePlayer(uuid).getName();
			data.put("uuid", uuid.toString());
			data.put("playerName", playerName);
			data.put("balance", 0.0);
			if (!newPlayer) {
				plugin.getLogger().info("Loading " + playerName + " / " + uuid.toString());
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(new FileReader(playerFile));				
				if (obj.containsKey("balance")) {
					data.put("balance", obj.get("balance"));
				}else{
					plugin.log.warning("Data file for " + playerName + " / " + uuid.toString() + " is missing a balance field");
				}
				for(String bankName : plugin.getBanks().keySet()) {
					if (obj.containsKey(bankName)) {
						data.put(bankName, obj.get(bankName));
					}
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
	}
	
	public void save()
	{
		File playerFile = getPlayerFile();
		try
		{
			playerFile.createNewFile();
			FileWriter os = new FileWriter(playerFile);
			plugin.getLogger().fine("Saving " + playerFile.getAbsolutePath());
			os.write(data.toString());
			os.close();
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	public String getName()
	{
		return (String) data.get("playerName");
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
		int decimals = plugin.getDecimals() * 100;
		data.put("balance", (double) Math.round(amount * decimals) / decimals);
		save();
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
		if (data.containsKey(bankName)) {
			Double balance = (Double) data.get(bankName);
			return balance.doubleValue();
		}
		return 0.0;
	}
	
	@SuppressWarnings("unchecked")
	public void setBankBalance(String bankName, double amount)
	{
		//Round amount to the correct number of decimals
		int decimals = plugin.getDecimals() * 100;
		data.put(bankName, (double) Math.round(amount * decimals) / decimals);
		save();
	}
	
	public boolean bankWithdraw(String bankName, double amount) 
	{
		if (amount > getBankBalance(bankName)) return false;
		double newBankBalance = getBankBalance(bankName) - amount;
		double newBalance = getBalance() + amount;
		setBankBalance(bankName, newBankBalance);
		setBalance(newBalance);
		if (plugin.getLogTransactions()) {
			plugin.getLogger().info(String.format("Withdraw Bank %s: %s %s%12.2f", player.getName(), plugin.getCurrencySymbol(), amount));
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
			plugin.getLogger().info(String.format("Deposit Bank %s: %s %s%12.2f", bankName, player.getName(), plugin.getCurrencySymbol(), amount));
		}
		return true;
	}
}
