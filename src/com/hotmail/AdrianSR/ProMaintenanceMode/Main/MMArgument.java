package com.hotmail.AdrianSR.ProMaintenanceMode.Main;

import org.bukkit.command.CommandSender;

public interface MMArgument 
{
	String getHelp();
	boolean useByPlayerOnly();
	String getArgumentName();
	String getDescription();
	void executeCommand(CommandSender sender, String label, String[] args);
	String getPermission();
}
