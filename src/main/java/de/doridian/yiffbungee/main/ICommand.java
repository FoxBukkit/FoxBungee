package de.doridian.yiffbungee.main;

import de.doridian.yiffbungee.main.util.PlayerHelper;
import de.doridian.yiffbungee.main.util.Utils;
import de.doridian.yiffbungee.main.util.YiffBungeeCommandException;
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
		String permission() default "yiffbukkit.no.one.is.allowed.except.asterisk.people\n";
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

	public static void registerCommand(Class<? extends ICommand> cmdClass) {
		final ICommand iCmd = constructCommand(cmdClass);
		YiffBungee.instance.getProxy().getPluginManager().registerCommand(YiffBungee.instance, iCmd);
	}

	protected ICommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	@Override
	public final void execute(CommandSender commandSender, String[] strings) {
		try {
			run(commandSender, strings, Utils.concatArray(strings, 0, ""));
		} catch (YiffBungeeCommandException e) {
			PlayerHelper.sendDirectedMessage(commandSender, "Error: " + e.getMessage());
		}
	}

	public abstract void run(CommandSender commandSender, String[] args, String argStr) throws YiffBungeeCommandException;
}
