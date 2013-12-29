package de.doridian.yiffbungee;

import de.doridian.yiffbungee.bans.listeners.BansPlayerListener;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import de.doridian.yiffbungee.permissions.commands.ReloadPermissionsCommand;
import de.doridian.yiffbungee.util.PlayerHelper;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;

public class YiffBungee extends Plugin implements Listener {
	public static YiffBungee instance;

	public PlayerHelper playerHelper;

	@Override
	public void onEnable() {
		instance = this;
		playerHelper = new PlayerHelper(this);

		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new BansPlayerListener());
		pluginManager.registerListener(this, this);
		pluginManager.registerListener(this, YiffBungeePermissionHandler.instance);

		ICommand.registerCommand(ReloadPermissionsCommand.class);
	}

	@EventHandler
	public void onPlayerJoin(ServerConnectEvent event) {
		playerHelper.setPlayerDisplayName(event.getPlayer());
		playerHelper.setPlayerListName(event.getPlayer());
	}
}
