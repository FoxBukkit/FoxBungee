/**
 * This file is part of YiffBungee.
 *
 * YiffBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbungee.bans.listeners;

import de.doridian.yiffbungee.bans.Ban;
import de.doridian.yiffbungee.bans.BanResolver;
import de.doridian.yiffbungee.main.YiffBungeeListener;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BansPlayerListener extends YiffBungeeListener {
	@EventHandler
	public void onPlayerPreLogin(LoginEvent event) {
		String name = event.getConnection().getName();
		UUID uuid = event.getConnection().getUniqueId();

		Ban ban = BanResolver.getBan(name, uuid);
		if(ban == null) {
			ban = BanResolver.getBan("[IP]" + event.getConnection().getAddress().getAddress().getHostAddress(), null);
			if(ban != null) {
				ban.setUser(name, uuid);
				ban.refreshTime();
				BanResolver.addBan(ban);
			}
		}
		if(ban != null) {
			event.setCancelReason("[YB] Banned: " + ban.getReason());
			event.setCancelled(true);
		}
	}
}
