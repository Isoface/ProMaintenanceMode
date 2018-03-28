package com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils;

import java.util.UUID;

import org.bukkit.entity.Player;

public class FakePlayer 
{
	UUID uuid;
	String playerName;
	String displayName;
	//int ping;

	public FakePlayer(Player player)
	{
		this.uuid = player.getUniqueId();
		this.playerName = player.getName();
		this.displayName = player.getDisplayName();
		//this.ping = FPOcbo.getPlayerPing(player);
	}

	public FakePlayer(String fakeName, String fakeListName, UUID uuid, int ping)
	{
		this.uuid = uuid;
		this.playerName = fakeName;
		this.displayName = fakeListName;
		//this.ping = ping;
	}
}
