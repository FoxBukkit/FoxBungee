/**
 * This file is part of FoxBungee.
 *
 * FoxBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbungee.main;

import de.doridian.dependencies.config.Configuration;
import de.doridian.dependencies.redis.RedisManager;
import de.doridian.foxbungee.main.util.PlayerHelper;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class FoxBungee extends Plugin {
	public static FoxBungee instance;

	public PlayerHelper playerHelper;

	private List<FoxBungeeSubPlugin> subPlugins;

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

		searchSubPlugin("de.doridian.foxbungee.main.FBMainSubPlugin");
		searchSubPlugin("de.doridian.foxbungee.database.FBDatabaseSubPlugin");
		searchSubPlugin("de.doridian.foxbungee.bans.FBBansSubPlugin");
		searchSubPlugin("de.doridian.foxbungee.permissions.FBPermissionsSubPlugin");

		for(FoxBungeeSubPlugin foxBungeeSubPlugin : subPlugins) {
			try {
				foxBungeeSubPlugin.onEnable();
			} catch (Exception e) {
				System.out.println("Failed to enable sub-plugin '" + foxBungeeSubPlugin.getClass().getName() + "'");
				e.printStackTrace();
			}
		}
	}

	private void searchSubPlugin(String className) {
		try {
			Class<?> ybSubPluginClazz = Class.forName(className);
			if(!FoxBungeeSubPlugin.class.isAssignableFrom(ybSubPluginClazz))
				throw new Exception("Not derived from FoxBungeeSubPlugin");
			FoxBungeeSubPlugin foxBungeeSubPlugin = (FoxBungeeSubPlugin)ybSubPluginClazz.getConstructor().newInstance();
			foxBungeeSubPlugin.onLoad();
			foxBungeeSubPlugin.loadListenersAndCommands();
		} catch (Exception e) {
			System.out.println("Failed to load sub-plugin '" + className + "'");
			e.printStackTrace();
		}
	}
}
