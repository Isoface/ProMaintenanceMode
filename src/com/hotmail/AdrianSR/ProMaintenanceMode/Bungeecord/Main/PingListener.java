package com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Main;

import com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils.Config;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils.Util;

import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener
{
	private final BM main;
	public PingListener(BM main) 
	{	
		if (main == null) {
			throw new NullPointerException("The Main cant be null!");
		}

		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProxyPing(ProxyPingEvent event)
	{
		// Check plugin is enabled.
		if (main == null || main.getProxy() == null) {
			return;
		}
		
		if (BM.getMaintenanceMode() == null) {
			return;
		}

		if (event.getResponse() == null) {
			return;
		}

		// Get Variables
		final ServerPing ping = event.getResponse();
		final ServerPing.Players players = ping.getPlayers();
		final ServerPing.Protocol version = ping.getVersion();

		// SET MOTD
		if (Config.MOTD_USE.toBoolean()) {
			ping.setDescription(Util.wc2(Config.MOTD_MESSAGE.toString()).replace("{n}", "\n").replace("{#}",
					BM.getMaintenanceMode().isPermanent() ? Config.MOTD_PERMANETN_MM_STRING.toString()
							: BM.getMaintenanceMode().getTimeWithFormat()));
		}
		
		// SET VERSION
		if (version != null) {
			// Set version string
			if (Config.VERSION_STRING_USE.toBoolean()) {
				version.setName(Util.wc2(Config.VERSION_STRING_MESSAGE.toString()));
			}
			
			// Set version protocol
			version.setProtocol((Integer) 9999);
		}

		if (players != null) {
			// Set Online Players
			players.setOnline(0);
			
			// Set Max Players
			players.setMax(0);
			
			// Set Hover
			if (Config.HOVER_USE.toBoolean()) {
				players.setSample(new ServerPing.PlayerInfo[] {
	                    new ServerPing.PlayerInfo(Config.HOVER_MESSAGE.toString(), Util.EMPTY_UUID) });
			}
		}
		
		// Set Icon
		if (Config.ICON_USE.toBoolean()) {
			Favicon icon = BM.getNewIcon();
			if (icon != null) {
				ping.setFavicon(icon);
			}
		}
	}

	public boolean register() 
	{
		if (main.listener == null) {
			main.getProxy().getPluginManager().registerListener(main, this);
			main.listener = this;
			return true;
		}
		return false;
	}

	public boolean unregister() 
	{
		if (main.listener != null) {
			main.getProxy().getPluginManager().unregisterListener(main.listener);
			return true;
		}
		return false;
	}
}
