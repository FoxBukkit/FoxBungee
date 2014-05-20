/**
 * This file is part of YiffBungee.
 *
 * YiffBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbungee.main;

import de.doridian.dependencies.config.Configuration;
import de.doridian.dependencies.redis.RedisManager;
import de.doridian.yiffbungee.main.util.PlayerHelper;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class YiffBungee extends Plugin {
	public static YiffBungee instance;

	public PlayerHelper playerHelper;

	private List<YiffBungeeSubPlugin> subPlugins;

    public Configuration configuration;

    public RedisManager redisManager;

	@Override
	public void onEnable() {
		instance = this;

        getDataFolder().mkdirs();

        configuration = new Configuration(getDataFolder());

        redisManager = new RedisManager(configuration);

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
