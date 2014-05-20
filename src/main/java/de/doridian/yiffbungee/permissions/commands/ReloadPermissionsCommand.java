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
