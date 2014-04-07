package de.doridian.yiffbungee.bans.listeners;

import de.doridian.yiffbungee.bans.Ban;
import de.doridian.yiffbungee.bans.BanResolver;
import de.doridian.yiffbungee.main.YiffBungeeListener;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BansPlayerListener extends YiffBungeeListener {
	@EventHandler
	public void onPlayerPreLogin(PreLoginEvent event) {
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
