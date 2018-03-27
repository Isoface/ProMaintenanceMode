package com.hotmail.AdrianSR.ProMaintenanceMode.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadMMArgument implements MMArgument
{
	@Override
	public String getHelp()
	{
		return ChatColor.RED + "Usage: '/Maintenance reload";
	}

	@Override
	public boolean useByPlayerOnly() 
	{
		return false;
	}

	@Override
	public String getArgumentName()
	{
		return "Reload";
	}

	@Override
	public String getDescription() 
	{
		return "Reload the config";
	}

	@Override
	public void executeCommand(CommandSender sender, String label, String[] args) 
	{
		MM.getInstance().reload();
		sender.sendMessage(ChatColor.GREEN + "Config Reloaded!");
	}

	@Override
	public String getPermission() 
	{
		return "maintenance.op";
	}
}
