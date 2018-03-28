package com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.Config;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.Utf8YamlConfiguration;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.Util;

public class MM extends JavaPlugin
{
	private static MM instance;
	private static MaintenanceMode mode;
	private static CachedServerIcon newIcon = null;
	private static ProtocolLibHandler lib = null;
	
	@Override
	public void onEnable() 
	{
		// Set Instance;
		instance = this;
		
		// Print Enable Message!
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MaintenanceMode] Enabled!");

		// Check ProtocolLib in server
		Plugin plugin = this.getServer().getPluginManager().getPlugin("ProtocolLib");
		if (plugin == null || !plugin.isEnabled()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MaintenanceMode] ProtocolLib not Found. Disabling...!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Maintenance Icon
		loadIcon();
		
		// Config.
		loadConfig();
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		if (f.exists()) {
			Utf8YamlConfiguration config = Utf8YamlConfiguration.loadConfiguration(f);
			Integer l = config.getInt("last-maintance-mode-time");
			if (l > 0 || l == -1)
			{
				MaintenanceMode mm = new MaintenanceMode(l, TimeUnit.SECONDS);
				setMaintenanceMode(mm);
				mm.Start(config.getBoolean("Permanent"));
			}
		}
		
		// Maintenance Command.
		new MMCommand(this);
		MMCommand.registerArgument(new EnableMMArgument());
		MMCommand.registerArgument(new StopMMArgument());
		MMCommand.registerArgument(new ReloadMMArgument());
		
		// Register events
		new Listeners(this);
		lib = new ProtocolLibHandler(this);
		lib.register();
	}
	
	public void reload() 
	{
		loadConfig();
	}
	
	public static boolean testingPlugin() 
	{
		return false;
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
	
	public void loadIcon()
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
						else {
							Util.print(ChatColor.RED + "The maintenance icon could not be loaded.");
							newIcon = null;
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						newIcon = null;
					} catch (Exception e) {
						e.printStackTrace();
						newIcon = null;
					}
				}
				else {
					Util.print(ChatColor.RED + "The Maintenance Icon Must be 64 x 64 pixels.");
					newIcon = null;
				}
			}
		} 
		catch (IOException e) 
		{
			Util.print(ChatColor.RED + "The maintenance icon could not be loaded. Because: " + e.getMessage());
			newIcon = null;
		}
	}
	
	private void loadConfig()
	{
		if (!getDataFolder().isDirectory()) {
			getDataFolder().mkdir();
		}
		
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		int save = 0;
		if (f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Utf8YamlConfiguration config = Utf8YamlConfiguration.loadConfiguration(f);
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
		for (Handler handler : getLogger().getHandlers()) {
			handler.close();
		}
		
		if (lib == null) {
			return;
		}

		// Unregister ProtocolLib listener
		lib.unregister();

		// Save Maintenance Mode
		if (!Config.LAST_MAINTANCE_SAVE.toBoolean()) {
			return;
		}

		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		if (!f.exists())
			loadConfig();

		Utf8YamlConfiguration config = Utf8YamlConfiguration.loadConfiguration(f);
		MaintenanceMode mm = getMaintenanceMode();
		if (mm == null) {
			return;
		}

		config.set("last-maintance-mode-time", (mm.isPermanent() ? -1 : (int) getMaintenanceMode().getRemainingTime()));
		config.set("Permanent", mm.isPermanent());
		
		try {
			config.save(f);
		} catch (IOException e) {
		}
	}
}
