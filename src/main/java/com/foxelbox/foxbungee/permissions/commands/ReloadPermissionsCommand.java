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
package com.foxelbox.foxbungee.permissions.commands;

import com.foxelbox.foxbungee.main.ICommand;
import com.foxelbox.foxbungee.main.util.FoxBungeeCommandException;
import com.foxelbox.foxbungee.main.util.PlayerHelper;
import com.foxelbox.foxbungee.permissions.FoxBungeePermissionHandler;
import net.md_5.bungee.api.CommandSender;

@ICommand.Command(names = "greloadpermissions", permission = "foxbungee.reloadpermissions")
public class ReloadPermissionsCommand extends ICommand {
	public ReloadPermissionsCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws FoxBungeeCommandException {
		FoxBungeePermissionHandler.instance.reload();
		PlayerHelper.sendDirectedMessage(commandSender, "Permissions system reloaded!");
	}
}
