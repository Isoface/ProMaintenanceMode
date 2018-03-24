package com.hotmail.AdrianSR.ProMaintenanceMode.Utils;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.hotmail.AdrianSR.ProMaintenanceMode.Main.MM;

public class PrlibManager
{
	public static final String EMPTY_ID = "0-0-0-0-0"; // Easiest format
    public static final UUID EMPTY_UUID = UUID.fromString(EMPTY_ID);
	private static boolean enabled = false;
	private static ProtocolManager protocolManager;
	
	public static void init() 
	{
		try {
			if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
				protocolManager = ProtocolLibrary.getProtocolManager();
				enabled = true;
			}
		} catch (Throwable localThrowable) {}
	}
	
	public static void initPacketListener() 
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MM.getInstance(),
				new PacketType[] { PacketType.Status.Server.SERVER_INFO }) 
		{
			public void onPacketSending(PacketEvent event)
			{
				//ShowList.fillShowList(null);
				final WrappedServerPing ping = (WrappedServerPing) event.getPacket().getServerPings().read(0);
				//ping.setPlayersOnline(Bukkit.getOnlinePlayers().size());
		       // ping.setPlayersMaximum(Bukkit.getMaxPlayers());
				//ping.setMotD("Testing!!!");
				ping.setPlayersOnline(8);
				ping.setPlayers(Collections.singleton(new WrappedGameProfile(EMPTY_UUID, ChatColor.RED + "Maintenance")));
				ping.setPlayersVisible(false);
				ping.setVersionName(ChatColor.RED + "MaintenanceVersion");
				ping.setPlayersOnline(0);

				//event.getPacket().getServerPings().write(0, ping);
			}
		});
	}
	
	public static boolean isEnabled() 
	{
		return enabled;
	}
	
	public static ProtocolManager getProtocolManager() 
	{
		return protocolManager;
	}
}
