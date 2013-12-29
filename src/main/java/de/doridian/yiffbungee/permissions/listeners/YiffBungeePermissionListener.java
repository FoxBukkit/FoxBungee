package de.doridian.yiffbungee.permissions.listeners;

import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class YiffBungeePermissionListener implements Listener {
	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		event.setHasPermission(YiffBungeePermissionHandler.instance.has(event.getSender(), event.getPermission()));
	}
}
