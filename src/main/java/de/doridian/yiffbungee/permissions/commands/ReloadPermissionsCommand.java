package de.doridian.yiffbungee.permissions.commands;

import de.doridian.yiffbungee.ICommand;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import de.doridian.yiffbungee.util.PlayerHelper;
import de.doridian.yiffbungee.util.YiffBungeeCommandException;
import net.md_5.bungee.api.CommandSender;

@ICommand.Names("greloadpermissions")
@ICommand.Permission("yiffbukkit.reloadpermissions")
public class ReloadPermissionsCommand extends ICommand {
	public ReloadPermissionsCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBungeeCommandException {
		YiffBungeePermissionHandler.instance.reload();
		PlayerHelper.sendDirectedMessage(commandSender, "Permissions system reloaded!");
	}
}
