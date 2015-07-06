/**
 * This file is part of FoxBungee.
 *
 * FoxBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbungee.bans.listeners;

import com.foxelbox.foxbungee.bans.Ban;
import com.foxelbox.foxbungee.bans.BanResolver;
import com.foxelbox.foxbungee.main.FoxBungeeListener;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;
import java.util.UUID;

public class BansPlayerListener extends FoxBungeeListener {
	@EventHandler
	public void onPlayerPreLogin(LoginEvent event) {
		final PendingConnection player = event.getConnection();
		final String name = player.getName();
		final UUID uuid = player.getUniqueId();
		final InetAddress address = player.getAddress().getAddress();

        BanResolver.addIPForPlayer(name, uuid, address);

		Ban ban = BanResolver.getBan(name, uuid);
		if(ban == null) {
			ban = BanResolver.getBan("[IP]" + address.getHostAddress(), null);
			if(ban != null) {
				ban.setUser(name, uuid);
				ban.refreshTime();
				BanResolver.addBan(ban);
			}
		}

		if(ban != null) {
			event.setCancelReason("[FB] Banned: " + ban.getReason());
			event.setCancelled(true);
			return;
		}

		BanResolver.addLogForPlayer(name, uuid, address, "login");
	}

	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent event) {
		final ProxiedPlayer player = event.getPlayer();
		BanResolver.addLogForPlayer(player.getName(), player.getUniqueId(), player.getAddress().getAddress(), "logout");
	}
}
