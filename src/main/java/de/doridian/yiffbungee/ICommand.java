package de.doridian.yiffbungee;

import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import de.doridian.yiffbungee.util.PlayerHelper;
import de.doridian.yiffbungee.util.Utils;
import de.doridian.yiffbungee.util.YiffBungeeCommandException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

public abstract class ICommand extends Command {
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface Names { String[] value(); }
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface Permission { String value(); }

	private final String permission;

	public static ICommand constructCommand(Class<? extends ICommand> cmdClass) {
		final String[] names = cmdClass.getAnnotation(Names.class).value();
		final String permission = cmdClass.getAnnotation(Permission.class).value();
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
		super(name, "yiffbukkit.basecommand", aliases);
		this.permission = permission;
	}

	@Override
	public final void execute(CommandSender commandSender, String[] strings) {
		if(!YiffBungeePermissionHandler.instance.has(commandSender, permission)) {
			PlayerHelper.sendDirectedMessage(commandSender, "Access denied");
			return;
		}
		try {
			run(commandSender, strings, Utils.concatArray(strings, 0, ""));
		} catch (YiffBungeeCommandException e) {
			PlayerHelper.sendDirectedMessage(commandSender, "Error: " + e.getMessage());
		}
	}

	public abstract void run(CommandSender commandSender, String[] args, String argStr) throws YiffBungeeCommandException;
}
