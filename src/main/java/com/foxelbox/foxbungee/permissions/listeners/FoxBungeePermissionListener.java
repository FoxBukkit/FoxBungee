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
package com.foxelbox.foxbungee.permissions.listeners;

import com.foxelbox.foxbungee.main.FoxBungeeListener;
import com.foxelbox.foxbungee.permissions.FoxBungeePermissionHandler;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.event.EventHandler;

public class FoxBungeePermissionListener extends FoxBungeeListener {
	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		event.setHasPermission(FoxBungeePermissionHandler.instance.has(event.getSender(), event.getPermission()));
	}
}
