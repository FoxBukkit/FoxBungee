package de.doridian.yiffbungee.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class MultiplePlayersFoundException extends PlayerFindException {
	private static final long serialVersionUID = 1L;
	private List<ProxiedPlayer> players;

	public MultiplePlayersFoundException(List<ProxiedPlayer> players) {
		super("Sorry, multiple players found!");
		this.players = players;
	}

	public List<ProxiedPlayer> getPlayers() {
		return players;
	}
}
