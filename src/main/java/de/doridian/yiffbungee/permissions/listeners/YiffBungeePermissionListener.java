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
