package de.doridian.yiffbungee;

import de.doridian.yiffbungee.bans.listeners.MCBansPlayerListener;
import net.md_5.bungee.api.plugin.Plugin;

public class YiffBungee extends Plugin {
	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, new MCBansPlayerListener());
	}
}
