package com.hotmail.AdrianSR.ProMaintenanceMode.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class Util
{
	public static void print(String mess)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MaintenanceMode] " + mess);
	}
	
	public static String shortenString(String string, int characters)
	{
		if(string.length() <= characters)
			return string;
		//
		return string.substring(0, characters);
	}
	
	public static String wc(String g)
	{
		return g == null ? "null String" : ChatColor.translateAlternateColorCodes('&', g);
	}
	
	public static String remC(String g)
	{
		return g == null ? "null string" : ChatColor.stripColor(g);
	}
	
	public static ConfigurationSection createSectionIfNoExits(ConfigurationSection father, String newSectionName)
	{
		return father.isConfigurationSection(newSectionName) ? father.getConfigurationSection(newSectionName) : father.createSection(newSectionName);
	}
	
	public static int createSectionIfNoExitsInt(ConfigurationSection father, String newSectionName)
	{
		if (!father.isConfigurationSection(newSectionName) || father.getConfigurationSection(newSectionName) == null)
		{
			father.createSection(newSectionName);
			return 1;
		}
		//
		return 0;
	}

	public static int setDefaultIfNotSet(ConfigurationSection section, String path, Object str)
	{
		if(section != null)
		{
			if(!section.isSet(path))
			{
				if (str != null)
				{
					if (str instanceof String)
						section.set(path, (String)str);
					else
						section.set(path, str);
					
					return 1;
				}
			}
		}
		return 0;
	}
	
	public static <T> List<T> toList(Set<T> set)
	{
		List<T> tor = new ArrayList<T>();
		for (T t : set)
			tor.add(t);
		//
		return tor;
	}
	
	public static TimeUnit getUnit(String input)
	{
		TimeUnit u;
		switch(input.toLowerCase())
		{
			case "error":
				default:
				return null;
			
			case "m":
			case "min":
			case "mins":
			case "minute":
			case "minutes":
				u = TimeUnit.MINUTES;
				break;
				
			case "h":
			case "hr":
			case "hrs":
			case "hour":
			case "hours":
				u = TimeUnit.HOURS;
				break;
				
			case "d":
			case "day":
			case "days":
				u = TimeUnit.DAYS;
				break;
		}
		return u;
	}
	
	public static String format(long miliseconds)
    {
        return DurationFormatUtils.formatDuration(miliseconds, Config.TIME_FORMAT.toString());//"H:mm:ss");
    }
}
