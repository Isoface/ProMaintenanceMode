package com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils.Charsets;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils.Config;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils.Util;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class BM extends Plugin implements Listener
{
	public static BM INSTANCE;
	public PingListener listener = null;
	private static @Getter Favicon newIcon;
	private static @Getter @Setter MaintenanceMode maintenanceMode;
	
	@Override
	public void onEnable()
	{
		// Set Instance
		INSTANCE = this;
		
		// Load config and icon
		loadConfig();
		loadIcon();
		
		// Register ping listener
		try {
			new PingListener(this).register();
		}
		catch(Throwable t) {
			t.printStackTrace();
			getLogger().log(Level.SEVERE, "The Pro Maintenancemode could not be enabled!");
		}
	
		// Register command and events in this class
		getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand());
		getProxy().getPluginManager().registerListener(this, this);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PostLoginEvent eve) {
		// Get Player and check
		final ProxiedPlayer p = eve.getPlayer();
		if (p == null || p.getName() == null || !p.isConnected()) {
			return;
		}

		// Check maintenance mode enabled
		if (getMaintenanceMode() == null) {
			return;
		}

		// kick player
		if (p.hasPermission("maintenance.join") || p.hasPermission("maintenance.on")) {
			return;
		}

		getProxy().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run() {
				if (p == null || !p.isConnected()) {
					return;
				}

				p.disconnect(Util.wc2(Config.KICK_MESSAGE.toString()));
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
	private final List<String> arguments = Arrays.asList(new String[] { "help", "Start", "Reload", "Stop" });
	private final class MaintenanceCommand extends Command
	{
		public MaintenanceCommand() 
		{
			super("Maintenance", null, "mm", "mn", "mmode");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void execute(CommandSender sender, String[] args) 
		{
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.GOLD + "Commands:");
					sender.sendMessage("");
					//
					List<String> s = arguments;
					//
					for (String arg : s) {
						if (arg != null) {
							sender.sendMessage(ChatColor.YELLOW + "- /Maintenance " + ChatColor.GOLD + arg);
							sender.sendMessage("");
						}
					}
				}
				else if (args[0].equalsIgnoreCase("Start")) {
					// Check Permission
					if (!sender.hasPermission("maintenance.op")) {
						sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
						return;
					}

					// Start
					if (args.length > 1) 
					{
						Integer i = null;
						
						try {
							i = Integer.valueOf(args[1]);
						}
						catch(IllegalArgumentException t) {
							if (args[1].equalsIgnoreCase("permanent") || args[1].equalsIgnoreCase("perm")) {
								MaintenanceMode m = new MaintenanceMode(Integer.valueOf(-1), (TimeUnit)null);
								setMaintenanceMode(m);
								m.Start(true);
								sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Maintenance Mode Started in Permanent way!");
							}
							else {
								sender.sendMessage(ChatColor.RED + "Invalid Number.");
							}
							return;
						}
						
						if (i != null && i.intValue() > 0)
						{
							if (args.length > 2) 
							{
								try
								{
									TimeUnit unit = Util.getUnit(args[2]);
									if (unit == TimeUnit.DAYS || unit == TimeUnit.HOURS || unit == TimeUnit.MINUTES)
									{
										sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Maintenance Mode Started! " + 
										"The maintenance mode will turn off in " + ChatColor.GOLD + ChatColor.BOLD.toString() + i.toString() + " " + unit.toString());
										
										// Set Maintenance Mode.
										MaintenanceMode m = new MaintenanceMode(i, unit);
										setMaintenanceMode(m);
										m.Start(false);
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
					else sender.sendMessage(ChatColor.RED + "Usage: '/Maintenance start [time] [time unit]' (Time Units: [Minutes, Hours, Days])");
				}
				else if (args[0].equalsIgnoreCase("Reload")) {
					// Check Permission
					if (!sender.hasPermission("maintenance.op")) {
						sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
						return;
					}
					
					// Reload
					loadConfig();
					loadIcon();
					sender.sendMessage(ChatColor.GREEN + "Config and Icon Reloaded!");
				}
				else if (args[0].equalsIgnoreCase("Stop")) {
					// Check Permission
					if (!sender.hasPermission("maintenance.op")) {
						sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
						return;
					}
					
					// Stop
					if (getMaintenanceMode() == null) {
						sender.sendMessage(ChatColor.RED + "The active maintenance mode was not found.");
						return;
					}
					
					getMaintenanceMode().Stop();
					setMaintenanceMode(null);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Invalid Command Syntax. Use: '/Maintenance help' to get help");
				}
			}
		}
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
						Favicon ic = Favicon.create(img);
						if (ic != null) {
							newIcon = ic;
							print(ChatColor.GREEN + "The maintenance icon has been successfully loaded.");
						}
						else {
							print(ChatColor.RED + "The maintenance icon could not be loaded.");
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
					print(ChatColor.RED + "The Maintenance Icon Must be 64 x 64 pixels.");
					newIcon = null;
				}
			} else {
				newIcon = null;
			}
		} 
		catch (IOException e) 
		{
			print(ChatColor.RED + "The maintenance icon could not be loaded. Because: " + e.getMessage());
			newIcon = null;
		}
	}
	
	private void saveResource(String resourcePath, boolean replace) 
	{
		if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                this.getProxy().getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
        	this.getProxy().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
	}
	
	private void loadConfig() 
	{
		// Check data folder
		if (!getDataFolder().isDirectory()) {
			getDataFolder().mkdir();
		}
		
		// Check File
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		int save = 0;
		if (!f.exists()) {
			f = Util.createNewUTF8File(getDataFolder(), "ProMaintenanceMode.yml");
		}

		// Load Config
		final ConfigurationProvider pv = ConfigurationProvider.getProvider(YamlConfiguration.class);
		Configuration config = null;
		// Load
		try {
			config = pv.load(new InputStreamReader(new FileInputStream(f), Charsets.UTF_8));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (config  == null) {
			return;
		}
		
		// Save defaults
		for (Config g : Config.values())
		{
			if (g.getDefault() instanceof String)
				save += Config.setDefaultIfNotSet(config, g.getPath(), ((String)g.getDefault()).replace("§", "&"));
			else
				save += Config.setDefaultIfNotSet(config, g.getPath(), g.getDefault());
		}
		
		if (save > 0) {
			try {
				Writer writer = new OutputStreamWriter(new FileOutputStream(f), Charsets.UTF_8); // UFT_8 writer
				pv.save(config, writer);	
				Util.convertFileToUFT_8(f, f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Load last maintenance from config
		Integer l = config.getInt("last-maintance-mode-time");
		if (l > 0 || l == -1)
		{
			MaintenanceMode mm = new MaintenanceMode(l, TimeUnit.SECONDS);
			setMaintenanceMode(mm);
			mm.Start(config.getBoolean("Permanent"));
		}
		
		// Set config file.
		Config.setFile(config);
	}
	
	@Override
	public void onDisable()
	{
		// Unregiser listener
		if (listener != null) {
			listener.unregister();
		}
		
		// Save current maintenance mode.
		if (!Config.LAST_MAINTANCE_SAVE.toBoolean()) {
			return;
		}
		
		// Check File
		File f = new File(getDataFolder(), "ProMaintenanceMode.yml");
		if (!f.exists()) {
			loadConfig();
		}
		
		// Get Provider and load Config
		final ConfigurationProvider prd = ConfigurationProvider.getProvider(YamlConfiguration.class);
		Configuration config = null;
		try {
			config = prd.load(f);
		} catch (IOException e1) {
			print(ChatColor.RED + "Error saving current Maintenance Mode");
			e1.printStackTrace();
		}
		
		// Check is not null
		if (config == null) {
			return;
		}
		
		// Check not null current Maintenance Mode
		MaintenanceMode mm = getMaintenanceMode();
		if (mm == null) {
			return;
		}

		config.set("last-maintance-mode-time", (mm.isPermanent() ? -1 : (int) getMaintenanceMode().getRemainingTime()));
		config.set("Permanent", mm.isPermanent());
		
		try {
			prd.save(config, f);
		} catch (IOException e) {
			print(ChatColor.RED + "Error saving current Maintenance Mode");
			e.printStackTrace();
		}
	}
	
	public static void print(String mess) 
	{
		INSTANCE.getLogger().info(Util.wc2(mess));
	}
}
