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
package de.doridian.yiffbungee.main.listeners;

import de.doridian.yiffbungee.main.YiffBungee;
import de.doridian.yiffbungee.main.YiffBungeeListener;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;

public class YiffBungeeMainListener extends YiffBungeeListener {
	@EventHandler
	public void onPlayerJoin(ServerConnectEvent event) {
        YiffBungee.instance.playerHelper.refreshUUID(event.getPlayer());
		YiffBungee.instance.playerHelper.setPlayerDisplayName(event.getPlayer());
		YiffBungee.instance.playerHelper.setPlayerListName(event.getPlayer());
	}
}
