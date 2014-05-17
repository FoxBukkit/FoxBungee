package de.doridian.yiffbungee.permissions;

import de.doridian.yiffbungee.main.YiffBungeeSubPlugin;

public class YBPermissionsSubPlugin extends YiffBungeeSubPlugin {
	@Override
	protected void onLoad() {
		YiffBungeePermissionHandler.instance.load();
	}
}
