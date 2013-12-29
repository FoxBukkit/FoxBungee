package de.doridian.yiffbungee.main.listeners;

import de.doridian.yiffbungee.main.YiffBungee;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class YiffBungeeMainListener implements Listener {
	@EventHandler
	public void onPlayerJoin(ServerConnectEvent event) {
		YiffBungee.instance.playerHelper.setPlayerDisplayName(event.getPlayer());
		YiffBungee.instance.playerHelper.setPlayerListName(event.getPlayer());
	}
}
