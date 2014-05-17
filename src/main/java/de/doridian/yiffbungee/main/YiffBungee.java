package de.doridian.yiffbungee.main;

import de.doridian.yiffbungee.main.util.PlayerHelper;
import de.doridian.yiffbungee.main.util.RedisManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YiffBungee extends Plugin {
	public static YiffBungee instance;

	public PlayerHelper playerHelper;

	private List<YiffBungeeSubPlugin> subPlugins;

	@Override
	public void onEnable() {
		instance = this;

        new File("plugins/YiffBungee").mkdirs();

		RedisManager.initialize();

		playerHelper = new PlayerHelper(this);

		subPlugins = new ArrayList<>();

		searchSubPlugin("de.doridian.yiffbungee.main.YBMainSubPlugin");
		searchSubPlugin("de.doridian.yiffbungee.database.YBDatabaseSubPlugin");
		searchSubPlugin("de.doridian.yiffbungee.bans.YBBansSubPlugin");
		searchSubPlugin("de.doridian.yiffbungee.permissions.YBPermissionsSubPlugin");

		for(YiffBungeeSubPlugin yiffBungeeSubPlugin : subPlugins) {
			try {
				yiffBungeeSubPlugin.onEnable();
			} catch (Exception e) {
				System.out.println("Failed to enable sub-plugin '" + yiffBungeeSubPlugin.getClass().getName() + "'");
				e.printStackTrace();
			}
		}
	}

	private void searchSubPlugin(String className) {
		try {
			Class<?> ybSubPluginClazz = Class.forName(className);
			if(!YiffBungeeSubPlugin.class.isAssignableFrom(ybSubPluginClazz))
				throw new Exception("Not derived from YiffBungeeSubPlugin");
			YiffBungeeSubPlugin yiffBungeeSubPlugin = (YiffBungeeSubPlugin)ybSubPluginClazz.getConstructor().newInstance();
			yiffBungeeSubPlugin.onLoad();
			yiffBungeeSubPlugin.loadListenersAndCommands();
		} catch (Exception e) {
			System.out.println("Failed to load sub-plugin '" + className + "'");
			e.printStackTrace();
		}
	}
}
