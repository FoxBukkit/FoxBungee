package de.doridian.yiffbungee.main;

import de.doridian.yiffbungee.main.util.Utils;
import net.md_5.bungee.api.plugin.PluginManager;

public class YiffBungeeSubPlugin {
	protected final YiffBungee plugin;
	protected YiffBungeeSubPlugin() {
		this.plugin = YiffBungee.instance;
	}

	protected final void loadListenersAndCommands() {
		final String packageName = getClass().getPackage().getName();

		final PluginManager pluginManager = plugin.getProxy().getPluginManager();

		for(Class<? extends YiffBungeeListener> clazz : Utils.getSubClasses(YiffBungeeListener.class, packageName + ".listeners")) {
			try {
				YiffBungeeListener yiffBungeeListener = clazz.getConstructor().newInstance();
				pluginManager.registerListener(plugin, yiffBungeeListener);
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
