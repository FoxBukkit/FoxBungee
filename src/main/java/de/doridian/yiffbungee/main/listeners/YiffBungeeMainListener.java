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
