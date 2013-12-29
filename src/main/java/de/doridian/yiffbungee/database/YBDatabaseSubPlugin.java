package de.doridian.yiffbungee.database;

import de.doridian.yiffbungee.main.YiffBungeeSubPlugin;

public class YBDatabaseSubPlugin extends YiffBungeeSubPlugin {
	@Override
	protected void onLoad() {
		DatabaseConnectionPool.initMe();
	}
}
