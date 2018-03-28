package com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StopMMArgument implements MMArgument
{
	@Override
	public String getHelp()
	{
		return ChatColor.RED + "Usage: '/Maintenance stop";
	}

	@Override
	public boolean useByPlayerOnly() 
	{
		return false;
	}

	@Override
	public String getArgumentName()
	{
		return "Stop";
	}

	@Override
	public String getDescription() 
	{
		return "Stop the Maintenance Mode";
	}

	@Override
	public void executeCommand(CommandSender sender, String label, String[] args) 
	{
		if (MM.getMaintenanceMode() == null) {
			sender.sendMessage(ChatColor.RED + "The active maintenance mode was not found.");
			return;
		}
		
		MM.getMaintenanceMode().Stop();
		MM.setMaintenanceMode(null);
	}

	@Override
	public String getPermission() 
	{
		return "maintenance.op";
	}
}
