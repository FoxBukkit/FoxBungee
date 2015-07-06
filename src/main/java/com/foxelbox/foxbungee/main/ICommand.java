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

import com.foxelbox.foxbungee.main.util.FoxBungeeCommandException;
import com.foxelbox.foxbungee.main.util.PlayerHelper;
import com.foxelbox.foxbungee.main.util.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

public abstract class ICommand extends Command {
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface Command {
		String[] names();
		String usage() default "";
		String help() default "";
		String permission() default "foxbukkit.no.one.is.allowed.except.asterisk.people\n";
	}

	public static ICommand constructCommand(Class<? extends ICommand> cmdClass) {
		Command commandAnnotation =  cmdClass.getAnnotation(Command.class);
		final String[] names = commandAnnotation.names();
		final String permission = commandAnnotation.permission();
		String[] aliases = new String[names.length - 1];
		System.arraycopy(names, 1, aliases, 0, aliases.length);
		try {
			return cmdClass.getConstructor(String.class, String.class, String[].class).newInstance(names[0], permission, aliases);
		} catch (NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected ICommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	@Override
	public final void execute(CommandSender commandSender, String[] strings) {
		try {
			run(commandSender, strings, Utils.concatArray(strings, 0, ""));
		} catch (FoxBungeeCommandException e) {
			PlayerHelper.sendDirectedMessage(commandSender, "Error: " + e.getMessage());
		}
	}

	public abstract void run(CommandSender commandSender, String[] args, String argStr) throws FoxBungeeCommandException;
}
