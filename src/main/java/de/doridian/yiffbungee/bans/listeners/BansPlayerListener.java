package de.doridian.yiffbungee.bans.listeners;

import de.doridian.yiffbungee.bans.Ban;
import de.doridian.yiffbungee.bans.BanResolver;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BansPlayerListener implements Listener {
	@EventHandler
	public void onPlayerPreLogin(PreLoginEvent event) {
		String name = event.getConnection().getName();

		Ban ban = BanResolver.getBan(name);
		if(ban != null) {
			event.setCancelReason("[YB] Banned: " + ban.getReason());
			event.setCancelled(true);
		}
	}
}
