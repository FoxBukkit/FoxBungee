package de.doridian.yiffbungee.main;

import de.doridian.yiffbungee.bans.listeners.BansPlayerListener;
import de.doridian.yiffbungee.main.listeners.YiffBungeeMainListener;
import de.doridian.yiffbungee.main.util.PlayerHelper;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import de.doridian.yiffbungee.permissions.commands.ReloadPermissionsCommand;
import de.doridian.yiffbungee.permissions.listeners.YiffBungeePermissionListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class YiffBungee extends Plugin {
	public static YiffBungee instance;

	public PlayerHelper playerHelper;

	@Override
	public void onEnable() {
		instance = this;
		playerHelper = new PlayerHelper(this);

		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new BansPlayerListener());
		pluginManager.registerListener(this, new YiffBungeeMainListener());
		pluginManager.registerListener(this, new YiffBungeePermissionListener());

		ICommand.registerCommand(ReloadPermissionsCommand.class);

		YiffBungeePermissionHandler.instance.reload();
	}
}
