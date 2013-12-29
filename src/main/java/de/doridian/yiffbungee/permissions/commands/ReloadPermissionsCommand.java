package de.doridian.yiffbungee.permissions.commands;

import de.doridian.yiffbungee.main.ICommand;
import de.doridian.yiffbungee.main.util.PlayerHelper;
import de.doridian.yiffbungee.main.util.YiffBungeeCommandException;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import net.md_5.bungee.api.CommandSender;

@ICommand.Command(names = "greloadpermissions", permission = "yiffbungee.reloadpermissions")
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
