package com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Main;

import java.util.Collections;
import java.util.Iterator;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.Config;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.StatusHandler;
import com.hotmail.AdrianSR.ProMaintenanceMode.Bukkit.Utils.Util;

public class ProtocolLibHandler extends StatusHandler {
	private StatusPacketListener listener;
	private Iterator<String> randomPlayers;

	protected ProtocolLibHandler(JavaPlugin plugin) {
		super(plugin);
	}

	public final class StatusPacketListener extends PacketAdapter {
		@SuppressWarnings("deprecation")
		public StatusPacketListener() {
			super(PacketAdapter
					.params(bukkit, PacketType.Status.Server.OUT_SERVER_INFO, PacketType.Handshake.Client.SET_PROTOCOL)
					.optionAsync());
		}

		@Override
		public void onPacketReceiving(PacketEvent event) {
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			// Check plugin is enabled.
			if (bukkit == null || !bukkit.isEnabled())
				return;

			// Chech maintenancemode is enabled.
			if (MM.getMaintenanceMode() == null) {
				return;
			}

			// Get WrappedServerPing
			final WrappedServerPing ping = event.getPacket().getServerPings().read(0);
			final String playerName = event.getPlayer().getName();

			// Set the MOTD
			// Establece el MOTD
			if (Config.MOTD_USE.toBoolean()) {
				ping.setMotD(Util.wc(Config.MOTD_MESSAGE.toStringReplaPlayerName(playerName)).replace("{n}", "\n")
						.replace("{#}", MM.getMaintenanceMode().getTimeWithFormat()));
			}

			// Set version string. (Only when the protocol is whit X).
			// Establece La version que sale alado de la cantidad de jugadores. (Solo cuando
			// el Protocol esta en X).
			if (Config.VERSION_STRING_USE.toBoolean()) {
				ping.setVersionName(Util.wc(Config.VERSION_STRING_MESSAGE.toStringReplaPlayerName(playerName)));
			}

			// Es como la version del servidor. Si es mas alta que la del jugador saldra una
			// X
			ping.setVersionProtocol((Integer) 9999);

			// Set Players Online.
			// Establece el numero de jugadores conectados al servidor.
			ping.setPlayersOnline(0);

			// Set Max Players Online.
			// Establece el maximo de jugadores
			ping.setPlayersMaximum(0);

			if (Config.HOVER_USE.toBoolean()) {
				if (ping.isPlayersVisible()) {
					// Seted on false to disable the message in the hover: "... (players) more ..."
					// En "False" para que no salga en el hover el mensaje: "... (jugadores) more
					// ..."
					ping.setPlayersVisible(false);

					// Player hover
					ping.setPlayers(Collections.singleton(new WrappedGameProfile(Util.EMPTY_UUID,
							Util.wc(Config.HOVER_MESSAGE.toStringReplaPlayerName(playerName)))));
				}
			}
		}
	}

	public Iterator<String> getRandomPlayers() {
		if (randomPlayers != null)
			return randomPlayers;

		this.randomPlayers = Util.getRandomPlayers();
		return randomPlayers;
	}

	@Override
	public boolean register() {
		if (listener == null) {
			ProtocolLibrary.getProtocolManager().addPacketListener(this.listener = new StatusPacketListener());
			return true;
		} else
			return false;
	}

	@Override
	public boolean unregister() {
		if (listener != null) {
			ProtocolLibrary.getProtocolManager().removePacketListener(listener);
			this.listener = null;
			return true;
		} else
			return false;
	}
}
