package com.hotmail.AdrianSR.ProMaintenanceMode.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.ProMaintenanceMode.Utils.Config;

public class Listeners implements Listener
{
	public Listeners(JavaPlugin pl) 
	{
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler
	public void list(ServerListPingEvent eve) 
	{
		if (MM.getMaintenanceMode() == null) {
			return;
		}
		
		eve.setMaxPlayers(0);
		
		if (Config.MOTD_USE.toBoolean()) {
			eve.setMotd(Config.MOTD_MESSAGE.toStringReplaceNumber(MM.getMaintenanceMode().getTimeWithFormat()));
		}
		
		if (Config.ICON_USE.toBoolean() && MM.getIcon() != null) {
			eve.setServerIcon(MM.getIcon());
		}
	}
	
	@EventHandler
	public void join(PlayerJoinEvent eve) 
	{
		if (MM.getMaintenanceMode() == null) {
			return;
		}
		
		final Player p = eve.getPlayer();
		
		if (p.hasPermission("maintenance.join") || p.hasPermission("maintenance.on") || p.isOp()) {
			return;
		}
		
		p.kickPlayer(Config.KICK_MESSAGE.toString());
	}
}
