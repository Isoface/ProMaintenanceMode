package com.hotmail.AdrianSR.ProMaintenanceMode.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.common.base.Throwables;
import com.hotmail.AdrianSR.ProMaintenanceMode.Main.MM;

public class Util
{
	public static final String EMPTY_ID = "0-0-0-0-0"; // Easiest format
    public static final UUID EMPTY_UUID = UUID.fromString(EMPTY_ID);
    
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


	public static Object getHandle(World world) {
		Object nms_entity = null;
		Method entity_getHandle = getMethod(world.getClass(), "getHandle");
		try {
			nms_entity = entity_getHandle.invoke(world);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nms_entity;
	}

	public static Object getHandle(Entity entity) {
		Object nms_entity = null;
		Method entity_getHandle = getMethod(entity.getClass(), "getHandle");
		try {
			nms_entity = entity_getHandle.invoke(entity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nms_entity;
	}

	public static Field getField(Class<?> cl, String field_name) {
		try {
			Field field = cl.getDeclaredField(field_name);
			return field;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes())) {
				return m;
			}
		}
		return null;
	}

	public static Method getMethod(Class<?> cl, String method, Integer args) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method) && args.equals(new Integer(m.getParameterTypes().length))) {
				return m;
			}
		}
		return null;
	}

	public static Method getMethod(Class<?> cl, String method) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}

	public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;

		if (l1.length != l2.length)
			return false;
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}

		return equal;
	}
	
	public static String substringBefore(String s, char c) {
        int pos = s.indexOf(c);
        return pos >= 0 ? s.substring(0, pos) : s;
    }
	
	private static Method getPlayersMethod()
	{
		Method legacy_getOnlinePlayers = null;
		try 
		{
			Method method = Server.class.getMethod("getOnlinePlayers");
			if (method.getReturnType() == Player[].class) 
			{
				legacy_getOnlinePlayers = method;
			}
		} 
		catch (Throwable ignored) 
		{
			
		}
		return legacy_getOnlinePlayers;
	}
	
	public static Collection<? extends Player> getPlayers() 
	{
		Collection<? extends Player> players;

		try 
		{
			// Meh, compatibility
			players = MM.getInstance().getServer().getOnlinePlayers();
		} 
		catch (NoSuchMethodError e) 
		{
			try 
			{
				
				players = Arrays.asList((Player[]) getPlayersMethod().invoke(MM.getInstance().getServer()));
			} 
			catch (InvocationTargetException ex) 
			{
				throw Throwables.propagate(ex.getCause());
			} 
			catch (IllegalAccessException ex)
			{
				throw Throwables.propagate(ex);
			}
		}

		return players;
	}
	
	public static Iterator<String> getRandomPlayers()
	{
		Collection<? extends Player> players = getPlayers();
		List<String> result = new ArrayList<>(players.size());

		for (Player player : players) 
		{
			result.add(player.getName());
		}

		return Randoms.shuffle(result).iterator();
	}
}
