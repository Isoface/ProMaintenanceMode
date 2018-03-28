package com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Main;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.Util;

public class MMCommand implements CommandExecutor
{
	private static Map<String, MMArgument> arguments = new TreeMap<String, MMArgument>();
	
	public MMCommand(JavaPlugin pl) 
	{
		pl.getCommand("Maintenance").setExecutor(this);
		registerArgument(new MMArgument() 
		{
			@Override
			public String getHelp() {
				return ChatColor.RED + "Invalid Command Syntax. Use '/Maintenance help'. to get Help";
			}

			@Override
			public boolean useByPlayerOnly() {
				return false;
			}

			@Override
			public String getArgumentName() {
				return "help";
			}
			
			@Override
			public String getDescription() {
				return "Get Help.";
			}

			@Override
			public void executeCommand(CommandSender sender, String label, String[] args) {
				sender.sendMessage(ChatColor.GOLD + "Commands:");
				sender.sendMessage("");
				//
				List<String> s = Util.toList(arguments.keySet());
				Collections.reverse(s);
				//
				for (String arg : s) {
					if (arg != null) {
						sender.sendMessage(ChatColor.YELLOW + "- /Maintenance " + ChatColor.GOLD + arg);
						sender.sendMessage(ChatColor.YELLOW + "	 Description: " + ChatColor.GOLD + getArgument(arg).getDescription());
						sender.sendMessage("");
					}
				}
			}

			@Override
			public String getPermission() {
				return "";
			}
		});
	}
	
	public static MMArgument getArgument(String name) 
	{
		return arguments.get(name);
	}
	
	public static void registerArgument(MMArgument argument)
	{
		if(argument != null)
		{
			arguments.put(argument.getArgumentName().toLowerCase(), argument);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length > 0)
		{
			MMArgument arg = arguments.get(args[0].toLowerCase());
			if(arg != null)
			{
				if(arg.useByPlayerOnly())
				{
					if(!(sender instanceof Player))
					{
						sender.sendMessage(ChatColor.RED+"The argument " + ChatColor.GOLD + arg.getArgumentName()+ChatColor.RED+" must be used by a player.");
						return true;
					}
				}
				//
				if (arg.getPermission().isEmpty() || sender.hasPermission(arg.getPermission()))
					arg.executeCommand(sender, label, excludeFirstArgument(args));
				else
					sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
			}
			else sender.sendMessage(ChatColor.RED + "Invalid Command Syntax. Use: '/Maintenance help' to get help"); 
		}
		else sender.sendMessage(ChatColor.RED + "Invalid Command Syntax. Use: '/Maintenance help' to get help"); 
			
		//
		return true;
	}
	
	private String[] excludeFirstArgument(String[] args)
	{
		String[] r = new String[args.length-1];
		if(r.length == 0)
			return r;
		for(int x = 1; x < args.length; x++)
			r[x-1] = args[x];
		return r;
	}
}
