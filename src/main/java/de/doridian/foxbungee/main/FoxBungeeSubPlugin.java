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

import de.doridian.foxbungee.main.util.Utils;
import net.md_5.bungee.api.plugin.PluginManager;

public class FoxBungeeSubPlugin {
	protected final FoxBungee plugin;
	protected FoxBungeeSubPlugin() {
		this.plugin = FoxBungee.instance;
	}

	protected final void loadListenersAndCommands() {
		final String packageName = getClass().getPackage().getName();

		final PluginManager pluginManager = plugin.getProxy().getPluginManager();

		for(Class<? extends FoxBungeeListener> clazz : Utils.getSubClasses(FoxBungeeListener.class, packageName + ".listeners")) {
			try {
				FoxBungeeListener foxBungeeListener = clazz.getConstructor().newInstance();
				pluginManager.registerListener(plugin, foxBungeeListener);
				System.out.println("Loaded listener '" + clazz.getName() + "'");
			} catch (Exception e) {
				System.out.println("Failed loading listener '" + clazz.getName() + "'");
				e.printStackTrace();
			}
		}

		for(Class<? extends ICommand> clazz : Utils.getSubClasses(ICommand.class, packageName + ".commands")) {
			try {
				ICommand iCmd = ICommand.constructCommand(clazz);
				pluginManager.registerCommand(plugin, iCmd);
				System.out.println("Loaded command '" + clazz.getName() + "'");
			} catch (Exception e) {
				System.out.println("Failed loading command '" + clazz.getName() + "'");
				e.printStackTrace();
			}
		}
	}

	protected void onLoad() {

	}

	protected void onEnable() {

	}
}
