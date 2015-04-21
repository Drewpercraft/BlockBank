BlockBank [![Build Status](https://travis-ci.org/Drewpercraft/BlockBank.svg?branch=master)](https://travis-ci.org/Drewpercraft/BlockBank)
=========
BlockBank was developed in order to provide a complete banking system for RPG/Adventure servers that prefer that their banking system be more realistic. BlockBank allows players to establish banks that other players can deposit/withdraw money from and create safety deposit chests. Players can earn interest on their deposits and pay interest on their loans. Each bank can set their hours of operations, location, the interest rates paid/charged, and if the "creepy bill collectors" are sent to collect on delinquent accounts. 

Authors
=======
Drewper & Hitechwizard, those great guys running Drewpercraft.com! Come see the plugin in action at mc.drewpercraft.com

How To Use
==========
As most bankers do, banks have operating hours and transactions can only occur when the bank is open. Check out the info for each bank for information on their hours of operation.

Make a Deposit
--------------
Depositing money is simple, and smart because the bank will pay you interest on your deposits. Just walk into an bank branch and use the command:

`/deposit 475.29`

This will deposit $475.29 into your bank account of the branch or atm you are currently in.

Make a Withdrawal
-----------------
Works exactly like making a deposit, except use "withdraw" instead of "deposit".

Getting a vault
---------------
To be deteremined.

Protection
----------
The bank's security is pretty tight. If you attempt to open a vault that does not belong to you or otherwise attempt to grief the bank, the bank's security guard is authorized to use lethal force.

Commands
========
`/bank list` - Show the list of key names used to identify the banks

`/bank info [name]` - Show information about the bank and branch locations named, or the one you are currently in.

`/bal` or `/balance` - Show the amount of money on hand and in all your BlockBank Accounts.

`/baltop` or `/balancetop` - Show the player money rankings

OP Commands
===========
`/bank announcements [on|off]` - Turn broadcast announcements for this bank on or off

`/bank [savings|loan] [rate]` - The rate paid/charged for accounts

`/bank reload` - Reload the configuration file and all player accounts

`/branch create [bankName] [regionName]` - Creates a branch for the bank named using the WorldGuard region specified

`/branch announcements [on|off]` - Turn the announcements for the particular branch on or off

`/branch open [hour]` - The minecraft hour the bank opens.

`/branch close [hour]` - The minecraft hour the bank closes.


Permissions
===========
`blockbank.open` = Allows user to open an account

`blockbank.vault` = Allows user to select a vault

`blockbank.loan` = Allows user to take out a loan

`blockbank.admin` = Allows the user to make admin changes to BlockBank
