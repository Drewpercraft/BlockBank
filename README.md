BlockBank
=========
BlockBank was developed in order to provide a complete banking system for RPG/Adventure servers that prefer that their banking system be more realistic. BlockBank allows players to establish banks that other players can deposit/withdraw money from, create safety deposit chests. Players earn interest on their deposits, and pay interest on their loans. Each bank can set their hours of operations, location, the interest rates paid/charged, and if the "creepy bill collectors" are sent to collect on delinquent accounts. Players can have multiple accounts in each bank, and players can co-mingle accounts into a single vault inside the bank to allow for shared access. To establish a vault in the bank, players place a sign on a vacant chest in the bank's vault room to associate it with the first account that does not have a vault. These chests are protected by the bank's security guard and prevent anyone other than the account holder from accessing them (similar to the Lockette plugin).

Authors
=======
Drewper & Hitechwizard, those great guys running Drewpercraft.com! Come see the plugin in action at minecraft.drewpercraft.com

How To Use
==========
As most bankers to, banks have operating hours and transactions can only occur when the bank is open. Check out the info for each bank for information on their hours of operation.

Make a Deposit
--------------
Depositing money is simple, and smart because the bank will pay you interest on your deposits. 

/deposit 475.29

This will deposit $475.29 into your bank account of the branch or atm you are currently in.

Make a Withdrawal
-----------------
Works exactly like making a deposit, except use "withdraw" instead of "deposit".

Getting a vault
---------------
Once you have an account established, you may claim a vault container in the bank. To do so, just place a sign on any of the chests located in the bank and it will then be associated with your account. The bank may limit the number of vaults you may have.

Protection
----------
The bank's security is pretty tight. If you attempt to open a vault that does not belong to you or otherwise attempt to grief the bank, the bank's security guard is authorized to use lethal force.

Commands
========
/bank list - Show the list of key names used to identify the banks

/bank info [name] - Show information about the bank and branch locations.

/balance - Show the amount of money on hand and in all your BlockBank Accounts

/balancetop - Show the player money rankings

OP Commands
===========
/bank announcements {on|off} - Turn broadcast announcements for this bank on or off

/branch create {bankName} {regionName} - Creates a branch for the bank using the WorldGuard region specified

/bank rate {savings|loan} {rate} - The rate paid/charged for accounts

/bank open - The minecraft hour the bank opens.

/bank close - The minecraft hour the bank closes.

/bank maxOfflineInterest {days}- Number of days account owner can be offline before forfeiting any interest earned.

/bank abandoned {days} - Number of days account owner can be offline before the account is abandoned.

/bank update {bank name} vaults {max} - Limits the number of accounts/vaults a player may open.

Permissions
===========
blockbank.open = Allows user to open an account

blockbank.vault = Allows user to select a vault

blockbank.loan = Allows user to take out a loan