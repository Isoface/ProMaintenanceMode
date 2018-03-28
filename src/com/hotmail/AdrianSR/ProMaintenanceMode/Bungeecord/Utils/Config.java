package com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public enum Config
{
	// Save Last MaintanceMode Time
	LAST_MAINTANCE_SAVE("Last-MaintanceMode.SaveTime", true),
	LAST_MAINTANCE_LOAD("Last-MaintanceMode.LoadTime", true),
	
	// Kick
	KICK_MESSAGE("KickMessage", "&cWe are in maintenance . Try Again Later :)"),
	
	// Time Format
	TIME_FORMAT("TimeFormat", "H:mm:ss"),
	
	// Maintenance Icon
	ICON_USE("Use-Maintenance-Icon", true),
	
	// Hover
	HOVER_USE("Hover.Enabled", true),
	HOVER_MESSAGE("Hover.Message", "&4&lThis server is in maintenace!"),
	
	// Version String
	VERSION_STRING_USE("Version.Use", true),
	VERSION_STRING_MESSAGE("Version.Message", "&e&lMAINTENACE"),
	
	// Motd
	MOTD_USE("ChangeMotd.Enabled", true),
	MOTD_PERMANETN_MM_STRING("ChangeMotd.PermanentTitle", "&4&lIndefinite"),
	MOTD_MESSAGE("ChangeMotd.Title", "&cServer in Maintenance {n} &aAvailable in &6{#}");
	
	// Variables
	private final String path;
    private final Object def;
    private static Configuration file;
	
	Config(String path, Object def) 
	{
		this.path = path;
		this.def = def;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public Object getDefault() 
	{
		return def;
	}
	
	/**
	 * Set the {@code YamlConfiguration} to use.
	 * 
	 * @param config
	 *            The config to set.
	 */
	public static void setFile(Configuration config)
	{
		file = config;
	}
	
	@Override
	public String toString()
	{
		if (!(def instanceof String))
			return null;
		//
		final String gg = file.getString(path, (String)def);
		//
		return ChatColor.translateAlternateColorCodes('&', gg != null ? gg : "" + def);
	}
	
	public String toShortenString(int characters)
	{
		final String string = toString();
		//
		if(string.length() <= characters)
			return string;
		//
		return string.substring(0, characters);
	}
	
	public String toStringReplaceAll(String oldValue, String newValue)
	{
		return toString().replaceAll(oldValue, newValue);
	}
	
	public String toStringReplacement(String word)
	{
		return replaceWord(toString(), word);
	}
	
	public String toStringReplaPlayerName(String word)
	{
		return toString().replace("{player}", word);
	}
	
	public String toStringReplacement(int number)
	{
		return replaceNumber(toString(),number);
	}
	
	public String toStringReplaceNumber(String word) 
	{
		return replaceNumber(toString(), word);
	}
	
	public String toStringReplacement(int number, String word)
	{
		return replaceWord(replaceNumber(toString(),number),word);
	}
	
	private String replaceWord(String string, String word)
	{
		return string.replace("{w}", word);
	}
	
	private String replaceNumber(String string, int number)
	{
		return string.replace("{#}", ""+number);
	}
	
	private String replaceNumber(String string, String word)
	{
		return string.replace("{#}", word);
	}
	
	public String[] toStringArray()
	{
		String s = toString();
		//
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public String[] toStringArray(int number)
	{
		String s = this.toStringReplacement(number);
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public String[] toStringArray(int number, String word)
	{
		String s = this.toStringReplacement(number, word);
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public String[] toStringArray(String word)
	{
		String s = this.toStringReplacement(word);
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public int toInt() 
	{
		if (!(def instanceof Integer))
			return 0;
		//
		return file.getInt(path, (Integer)def);
	}
	
	public double toDouble() 
	{
		return (double)toInt();
	}
	
	public boolean toBoolean() 
	{
		if (!(def instanceof Boolean))
			return false;
		//
		return file.getBoolean(path, (Boolean)def);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> toStringList()
	{
		if (!(def instanceof List))
			return null;
		//
		final List<String> list = file.getStringList(path);
		//
		return list != null ? list : (List<String>)def;
	}
	
	public static int setDefaultIfNotSet(Configuration section, String path, Object str) 
	{
		if (section != null) {
			if (!section.contains(path)) {
				if (str != null) {
					if (str instanceof String)
						section.set(path, (String) str);
					else
						section.set(path, str);
					return 1;
				}
			}
		}
		return 0;
	}
}
