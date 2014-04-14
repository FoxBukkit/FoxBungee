package de.doridian.yiffbungee.bans;

import java.util.UUID;

public class BanPlayer {
	public final UUID uuid;
	public final String name;

	public BanPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
}
