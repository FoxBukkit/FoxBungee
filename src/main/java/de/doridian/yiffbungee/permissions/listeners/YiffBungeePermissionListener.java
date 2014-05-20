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
package de.doridian.yiffbungee.permissions.listeners;

import de.doridian.yiffbungee.main.YiffBungeeListener;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.event.EventHandler;

public class YiffBungeePermissionListener extends YiffBungeeListener {
	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		event.setHasPermission(YiffBungeePermissionHandler.instance.has(event.getSender(), event.getPermission()));
	}
}
