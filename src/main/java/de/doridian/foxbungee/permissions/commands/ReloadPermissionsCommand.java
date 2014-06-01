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
package de.doridian.foxbungee.permissions.commands;

import de.doridian.foxbungee.main.ICommand;
import de.doridian.foxbungee.main.util.PlayerHelper;
import de.doridian.foxbungee.main.util.FoxBungeeCommandException;
import de.doridian.foxbungee.permissions.FoxBungeePermissionHandler;
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
