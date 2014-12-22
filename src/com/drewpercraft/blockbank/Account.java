package com.drewpercraft.blockbank;

public class Account {
	
	private final Bank bank;
	/*
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private static enum Type { SAVINGS, LOAN };
	
		
	private String player = "";
	private Type type = Type.SAVINGS;
	private double balance  = 0;
	private double interestRate = 0;	// Interest rate account earns/charged per Minecraft year
    */
	public Account(Bank bank)
	{
		this.bank = bank;
	}
}
