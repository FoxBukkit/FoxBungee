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
package de.doridian.foxbungee.permissions;

import de.doridian.foxbungee.main.FoxBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class FoxBungeePermissionHandler {
	public static final FoxBungeePermissionHandler instance = new FoxBungeePermissionHandler();

	class GroupWorld {
		public final String group;
		public final String world;
		
		public GroupWorld(String group, String world) {
			this.group = group;
			this.world = world;
		}
		
		@Override
		public boolean equals(Object other) {
			return (other instanceof  GroupWorld) && equals((GroupWorld)other);
		}

		public boolean equals(GroupWorld other) {
			return other.group.equals(this.group) && other.world.equals(this.world);
		}
		
		@Override
		public int hashCode() {
			return (group.hashCode() / 2) + (world.hashCode() / 2);
		}
	}

	private boolean loaded = false;
	private final Map<String,String> playerGroups = FoxBungee.instance.redisManager.createCachedRedisMap("playergroups");
	private final HashMap<GroupWorld,HashSet<String>> groupPermissions = new HashMap<GroupWorld,HashSet<String>>();
	private final HashMap<GroupWorld,HashSet<String>> groupProhibitions = new HashMap<GroupWorld,HashSet<String>>();
	
	private String defaultWorld = "world";

	public void setDefaultWorld(String world) {
		defaultWorld = world;
	}

	public void load() {
		if(loaded) return;
		reload();
	}

	public void reload() {
		loaded = true;
		groupPermissions.clear();
		groupProhibitions.clear();

		final File permissionsDirectory = new File(FoxBungee.instance.getDataFolder(), "permissions");
		permissionsDirectory.mkdirs();
		File[] files = permissionsDirectory.listFiles();

		for(File file : files) {
			try {
				String currentWorld;
				GroupWorld currentGroupWorld = null;
				currentWorld = file.getName();
				if(currentWorld.indexOf('.') > 0) {
					currentWorld = currentWorld.substring(0, currentWorld.indexOf('.'));
				}
				HashSet<String> currentPermissions = null;
				HashSet<String> currentProhibitions = null;
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim().toLowerCase();
						if (line.length() < 1) continue;
						char c = line.charAt(0);
						if (c == '-') {
							line = line.substring(1).trim();
							currentPermissions.remove(line);
							currentProhibitions.add(line);
						} else if (c == '+') {
							line = line.substring(1).trim();
							currentPermissions.add(line);
							currentProhibitions.remove(line);
						} else {
							if (currentGroupWorld != null) {
								groupPermissions.put(currentGroupWorld, currentPermissions);
								groupProhibitions.put(currentGroupWorld, currentProhibitions);
							}
							int i = line.indexOf(' ');
							currentPermissions = new HashSet<>();
							currentProhibitions = new HashSet<>();
							if (i > 0) {
								currentGroupWorld = new GroupWorld(line.substring(0, i).trim(), currentWorld);
								GroupWorld tmp = new GroupWorld(line.substring(i + 1).trim(), currentWorld);
								currentPermissions.addAll(groupPermissions.get(tmp));
								currentProhibitions.addAll(groupProhibitions.get(tmp));
							} else {
								currentGroupWorld = new GroupWorld(line, currentWorld);
							}
						}
					}
					if (currentGroupWorld != null) {
						groupPermissions.put(currentGroupWorld, currentPermissions);
						groupProhibitions.put(currentGroupWorld, currentProhibitions);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {

	}

	public boolean has(CommandSender commandSender, String permission) {
		// Console can do everything
		return (!(commandSender instanceof ProxiedPlayer)) || has((ProxiedPlayer) commandSender, permission);
	}

	public boolean has(ProxiedPlayer player, String permission) {
		return has("world", player.getUniqueId(), permission);
	}

	public boolean has(String worldName, UUID playerName, String permission) {
		permission = permission.toLowerCase();

		GroupWorld currentGroupWorld = new GroupWorld(getGroup(playerName), worldName);

		HashSet<String> currentPermissions = groupPermissions.get(currentGroupWorld);
		if(currentPermissions == null) {
			currentGroupWorld = new GroupWorld(currentGroupWorld.group, defaultWorld);
			currentPermissions = groupPermissions.get(currentGroupWorld);
			if(currentPermissions == null) return false;
		}
		if(currentPermissions.contains(permission)) return true;

		HashSet<String> currentProhibitions = groupProhibitions.get(currentGroupWorld);
		if(currentProhibitions != null && currentProhibitions.contains(permission)) return false;

		int xpos = 0;
		String tperm = permission;
		while((xpos = tperm.lastIndexOf('.')) > 0) {
			tperm = tperm.substring(0, xpos);
			String tperm2 = tperm + ".*";
			if(currentProhibitions != null && currentProhibitions.contains(tperm2)) { currentProhibitions.add(permission); return false; }
			if(currentPermissions.contains(tperm2)) { currentPermissions.add(permission); return true; }
		}

		if(currentProhibitions != null && currentProhibitions.contains("*")) { currentProhibitions.add(permission); return false; }
		if(currentPermissions.contains("*")) { currentPermissions.add(permission); return true; }

		if(currentProhibitions == null) {
			currentProhibitions = new HashSet<String>();
			groupProhibitions.put(currentGroupWorld, currentProhibitions);
		}
		currentProhibitions.add(permission);
		return false;
	}

	public boolean has(UUID playerName, String permission) {
		return has(defaultWorld, playerName, permission);
	}

	public String getGroup(UUID uuid) {
		String result = playerGroups.get(uuid.toString());
		if(result == null)
			return "guest";
		return result;
	}

	public void setGroup(UUID uuid, String group) {
		group = group.toLowerCase();
		playerGroups.put(uuid.toString(), group);
		save();
	}

	public boolean inGroup(String world, UUID name, String group) {
		return getGroup(name).equalsIgnoreCase(group);
	}

	public boolean inGroup(UUID name, String group) {
		return inGroup(defaultWorld, name, group);
	}
}