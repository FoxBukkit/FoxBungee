package de.doridian.yiffbungee;

import de.doridian.yiffbungee.bans.listeners.BansPlayerListener;
import de.doridian.yiffbungee.permissions.commands.ReloadPermissionsCommand;
import de.doridian.yiffbungee.util.PlayerHelper;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class YiffBungee extends Plugin implements Listener {
	public static YiffBungee instance;

	public PlayerHelper playerHelper;

	@Override
	public void onEnable() {
		instance = this;
		playerHelper = new PlayerHelper(this);

		getProxy().getPluginManager().registerListener(this, new BansPlayerListener());
		getProxy().getPluginManager().registerListener(this, this);

		ICommand.registerCommand(ReloadPermissionsCommand.class);
	}

	@EventHandler
	public void onPlayerJoin(ServerConnectEvent event) {
		playerHelper.setPlayerDisplayName(event.getPlayer());
		playerHelper.setPlayerListName(event.getPlayer());
	}
}
