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
package com.foxelbox.foxbungee.main;

import com.foxelbox.dependencies.config.Configuration;
import com.foxelbox.dependencies.redis.RedisManager;
import com.foxelbox.dependencies.threading.IThreadCreator;
import com.foxelbox.foxbungee.main.util.PlayerHelper;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;

import java.util.ArrayList;
import java.util.List;

public class FoxBungee extends Plugin {
	public static FoxBungee instance;

	public PlayerHelper playerHelper;

	private List<FoxBungeeSubPlugin> subPlugins;

    public Configuration configuration;

    public RedisManager redisManager;

	public GroupedThreadFactory groupedThreadFactory;

	@Override
	public void onEnable() {
		instance = this;

        getDataFolder().mkdirs();

		groupedThreadFactory = new GroupedThreadFactory(this);
		
        configuration = new Configuration(getDataFolder());

        redisManager = new RedisManager(new IThreadCreator() {
			@Override
			public Thread createThread(Runnable runnable) {
				return groupedThreadFactory.newThread(runnable);
			}
		}, configuration);

		playerHelper = new PlayerHelper(this);

		subPlugins = new ArrayList<>();

		searchSubPlugin("com.foxelbox.foxbungee.main.FBMainSubPlugin");
		searchSubPlugin("com.foxelbox.foxbungee.database.FBDatabaseSubPlugin");
		searchSubPlugin("com.foxelbox.foxbungee.bans.FBBansSubPlugin");
		searchSubPlugin("com.foxelbox.foxbungee.permissions.FBPermissionsSubPlugin");

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
			Class<?> fbSubPluginClazz = Class.forName(className);
			if(!FoxBungeeSubPlugin.class.isAssignableFrom(fbSubPluginClazz))
				throw new Exception("Not derived from FoxBungeeSubPlugin");
			FoxBungeeSubPlugin foxBungeeSubPlugin = (FoxBungeeSubPlugin)fbSubPluginClazz.getConstructor().newInstance();
			foxBungeeSubPlugin.onLoad();
			foxBungeeSubPlugin.loadListenersAndCommands();
		} catch (Exception e) {
			System.out.println("Failed to load sub-plugin '" + className + "'");
			e.printStackTrace();
		}
	}
}
