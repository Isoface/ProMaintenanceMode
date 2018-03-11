package com.hotmail.AdrianSR.ProMaintenanceMode.Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import com.hotmail.AdrianSR.ProMaintenanceMode.Utils.Config;
import com.hotmail.AdrianSR.ProMaintenanceMode.Utils.Util;

public class MM extends JavaPlugin
{
	private static MM instance;
	private static MaintenanceMode mode;
	private static CachedServerIcon newIcon = null;
	
	@Override
	public void onEnable() 
	{
		// Set Instance;
		instance = this;
		
		// Print Enabled Message.
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MaintenanceMode] Enabled!");
		
		// Config.
		loadConfig();
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		if (f.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			Integer l = config.getInt("last-maintance-mode-time");
			if (l > 0) 
			{
				MaintenanceMode mm = new MaintenanceMode(l, TimeUnit.SECONDS);
				setMaintenanceMode(mm);
				mm.Start();
			}
		}
		
		// Maintenance Icon
		loadIcon();
		
		// Maintenance Command.
		new MMCommand(this);
		MMCommand.registerArgument(new EnableMMArgument());
		MMCommand.registerArgument(new StopMMArgument());
		
		// Register events
		new Listeners(this);
	}
	
	public static CachedServerIcon getIcon() 
	{
		return newIcon;
	}
	
	public static MM getInstance() 
	{
		return instance;
	}
	
	public static MaintenanceMode getMaintenanceMode() 
	{
		return mode;
	}
	
	public static void setMaintenanceMode(MaintenanceMode newMode) 
	{
		mode = newMode;
	}
	
	private void loadIcon() 
	{
		// Save Default Icon.
		File f = new File(getDataFolder(), "maintenance-icon.png");
		if (f == null || !f.exists()) {
			saveResource("maintenance-icon.png", false);
		}
		
		File icon = new File(this.getDataFolder(), "maintenance-icon.png");
		if (icon == null || !icon.exists()) {
			return;
		}
		
		try 
		{
			BufferedImage img = ImageIO.read(icon);
			if (img != null) {
				if (img.getHeight() == 64 && img.getHeight() == 64)
				{
					try {
						CachedServerIcon ic = Bukkit.getServer().loadServerIcon(icon);
						if (ic != null) {
							newIcon = ic;
							Util.print(ChatColor.GREEN + "The maintenance icon has been successfully loaded.");
						}
						else
							Util.print(ChatColor.RED + "The maintenance icon could not be loaded.");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
					Util.print(ChatColor.RED + "The Maintenance Icon Must be 64 x 64 pixels.");
			}
		} 
		catch (IOException e) 
		{
			Util.print(ChatColor.RED + "The maintenance icon could not be loaded. Because: " + e.getMessage());
		}
	}
	
	private void loadConfig()
	{
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		int save = 0;
		if (f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		for (Config g : Config.values()) 
		{
			if (g.getDefault() instanceof String)
				save += Util.setDefaultIfNotSet(config, g.getPath(), ((String)g.getDefault()).replace("§", "&"));
			else
				save += Util.setDefaultIfNotSet(config, g.getPath(), g.getDefault());
		}
		
		if (save > 0) {
			try {
				config.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Config.setFile(config);
	}
	
	@Override
	public void onDisable() 
	{
		if (mode == null || mode.getTimeUnit() == null || mode.getRemainingTime() <= 0) {
			return;
		}
		
		if (!Config.LAST_MAINTANCE_SAVE.toBoolean()) {
			return;
		}
		
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		if (!f.exists())
			loadConfig();
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("last-maintance-mode-time", (int)getMaintenanceMode().getRemainingTime());
		try {
			config.save(f);
		} 
		catch (IOException e) {}
	}
}
