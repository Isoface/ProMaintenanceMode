package com.hotmail.AdrianSR.ProMaintenanceMode.Main;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hotmail.AdrianSR.ProMaintenanceMode.Utils.Util;

public class EnableMMArgument implements MMArgument
{
	@Override
	public String getHelp()
	{
		return ChatColor.RED + "Usage: '/Maintenance start [time] [time unit]' (Time Units: [Minutes, Hours, Days])";
	}

	@Override
	public boolean useByPlayerOnly() 
	{
		return false;
	}

	@Override
	public String getArgumentName()
	{
		return "Start";
	}

	@Override
	public String getDescription() 
	{
		return "Start the Maintenance Mode";
	}

	@Override
	public void executeCommand(CommandSender sender, String label, String[] args) 
	{
		if (MM.getMaintenanceMode() != null) {
			sender.sendMessage(ChatColor.RED + "The maintenance mode is already started. Stop it to start another.");
			return;
		}
		
		if (args.length > 0) 
		{
			Integer i = null;
			
			try {
				i = Integer.valueOf(args[0]);
			}
			catch(IllegalArgumentException t) {
				sender.sendMessage(ChatColor.RED + "Invalid Number.");
				return;
			}
			
			if (i != null && i.intValue() > 0)
			{
				if (args.length > 1) 
				{
					try
					{
						TimeUnit unit = Util.getUnit(args[1]);
						if (unit == TimeUnit.DAYS || unit == TimeUnit.HOURS || unit == TimeUnit.MINUTES)
						{
							sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Maintenance Mode Started! " + 
							"The maintenance mode will turn off in " + ChatColor.GOLD + ChatColor.BOLD.toString() + i.toString() + " " + unit.toString());
							
							// Set Maintenance Mode.
							MaintenanceMode m = new MaintenanceMode(i, unit);
							MM.setMaintenanceMode(m);
							m.Start();
						}
						else {
							sender.sendMessage(ChatColor.RED + "Invalid Time Unit.");
							return;
						}
					}
					catch(IllegalArgumentException t) {
						sender.sendMessage(ChatColor.RED + "Invalid Time Unit.");
						return;
					}
				}
			}
			else sender.sendMessage(ChatColor.RED + "The minimum maintenance time is 1 minute");
		}
		else sender.sendMessage(getHelp());
	}

	@Override
	public String getPermission() 
	{
		return "maintenance.op";
	}
}
