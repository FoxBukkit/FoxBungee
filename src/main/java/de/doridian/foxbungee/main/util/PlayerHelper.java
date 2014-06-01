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
package de.doridian.foxbungee.main.util;

import de.doridian.foxbungee.main.FoxBungee;
import de.doridian.foxbungee.permissions.FoxBungeePermissionHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHelper {
	private FoxBungee plugin;

	public PlayerHelper(FoxBungee plug) {
		plugin = plug;
	}

	public ProxiedPlayer literalMatch(String name) {
		return plugin.getProxy().getPlayer(name);
	}

	public List<ProxiedPlayer> matchPlayer(String subString) {
		subString = subString.toLowerCase();
		List<ProxiedPlayer> ret = new ArrayList<>();
		for(ProxiedPlayer ply : plugin.getProxy().getPlayers())
			if(ply.getName().toLowerCase().contains(subString))
				ret .add(ply);
		return ret;
	}

    public Map<String,String> playerNameToUUID = FoxBungee.instance.redisManager.createCachedRedisMap("playerNameToUUID");
    public Map<String,String> playerUUIDToName = FoxBungee.instance.redisManager.createCachedRedisMap("playerUUIDToName");
    public void refreshUUID(ProxiedPlayer player) {
        playerUUIDToName.put(player.getUniqueId().toString(), player.getName());
        playerNameToUUID.put(player.getName().toLowerCase(), player.getUniqueId().toString());
    }

    private static final Pattern quotePattern = Pattern.compile("^\"(.*)\"$");
	public ProxiedPlayer matchPlayerSingle(String subString, boolean implicitlyLiteral) throws PlayerNotFoundException, MultiplePlayersFoundException {
		if(implicitlyLiteral)
			return literalMatch(subString);

		Matcher matcher = quotePattern.matcher(subString);

		if (matcher.matches())
			return literalMatch(matcher.group(1));

		List<ProxiedPlayer> players = matchPlayer(subString);

		int c = players.size();
		if (c < 1)
				throw new PlayerNotFoundException();

		if (c > 1)
			throw new MultiplePlayersFoundException(players);

		return players.get(0);
	}

	public ProxiedPlayer matchPlayerSingle(String subString) throws PlayerNotFoundException, MultiplePlayersFoundException {
		return matchPlayerSingle(subString, false);
	}

	public String completePlayerName(String subString, boolean implicitlyLiteralNames) {
		Matcher matcher = quotePattern.matcher(subString);

		if (matcher.matches())
			return matcher.group(1);

		List<ProxiedPlayer> otherplys = matchPlayer(subString);
		int c = otherplys.size();

		if (c == 0 && implicitlyLiteralNames)
			return subString;

		if (c == 1)
			return otherplys.get(0).getName();

		return null;
	}

	public String GetFullPlayerName(ProxiedPlayer ply) {
		return getPlayerTag(ply) + ply.getDisplayName();
	}

	//Messaging stuff
	public static void sendServerMessage(String msg) {
		sendServerMessage(msg,'5');
	}
	public static void sendServerMessage(String msg, char colorCode) {
		msg = "\u00a7"+colorCode+"[FB]\u00a7f " + msg;
		FoxBungee.instance.getProxy().broadcast(msg);
	}

	public static void sendServerMessage(String msg, int minLevel) {
		sendServerMessage(msg, minLevel, '5');
	}
	public static void sendServerMessage(String msg, int minLevel, char colorCode) {
		msg = "\u00a7"+colorCode+"[FB]\u00a7f " + msg;

		Collection<ProxiedPlayer> proxiedPlayers = FoxBungee.instance.getProxy().getPlayers();

		for (ProxiedPlayer player : proxiedPlayers) {
			if (getPlayerLevel(player) < minLevel)
				continue;
			player.sendMessage(msg);
		}
	}

	
	/**
	 * Broadcasts a message to all ProxiedPlayers with the given permission, prefixed with [FB] in purple.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 */
	public static void sendServerMessage(String message, String permission) {
		sendServerMessage(message, permission, '5');
	}
	/**
	 * Broadcasts a message to all ProxiedPlayers with the given permission, prefixed with [FB] in the given color.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 * @param colorCode The color code to prefix
	 */
	public static void sendServerMessage(String message, String permission, char colorCode) {
		broadcastMessage("\u00a7"+colorCode+"[FB]\u00a7f " + message, permission);
	}

	/**
	 * Broadcasts a message to all ProxiedPlayers with the given permission.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 */
	public static void broadcastMessage(String message, String permission) {
		Collection<ProxiedPlayer> proxiedPlayers = FoxBungee.instance.getProxy().getPlayers();

		for (ProxiedPlayer player : proxiedPlayers) {
			if (!player.hasPermission(permission))
				continue;

			player.sendMessage(message);
		}
	}

	public static void sendServerMessage(String msg, CommandSender... exceptPlayers) {
		sendServerMessage(msg, '5', exceptPlayers);
	}
	public static void sendServerMessage(String msg, char colorCode, CommandSender... exceptPlayers) {
		msg = "\u00a7"+colorCode+"[FB]\u00a7f " + msg;

		Set<ProxiedPlayer> exceptPlayersSet = new HashSet<>();
		for (CommandSender exceptPlayer : exceptPlayers) {
			if (!(exceptPlayer instanceof ProxiedPlayer))
				continue;

			exceptPlayersSet.add((ProxiedPlayer)exceptPlayer);
		}

		Collection<ProxiedPlayer> proxiedPlayers = FoxBungee.instance.getProxy().getPlayers();

		for (ProxiedPlayer player : proxiedPlayers) {
			if (exceptPlayersSet.contains(player))
				continue;

			player.sendMessage(msg);
		}
	}

	public static void sendDirectedMessage(CommandSender commandSender, String msg, char colorCode) {
		commandSender.sendMessage("\u00a7"+colorCode+"[FB]\u00a7f " + msg);
	}
	public static void sendDirectedMessage(CommandSender commandSender, String msg) {
		sendDirectedMessage(commandSender, msg, '5');
	}

	//Ranks
	public static String getPlayerRank(ProxiedPlayer ply) {
		return getPlayerRank(ply.getUniqueId());
	}
	public static String getPlayerRank(UUID uuid) {
		final String rank = FoxBungeePermissionHandler.instance.getGroup(uuid);
		if (rank == null)
			return "guest";

		return rank;
	}
	public void setPlayerRank(UUID uuid, String rankname) {
		if(getPlayerRank(uuid).equalsIgnoreCase(rankname)) return;
		FoxBungeePermissionHandler.instance.setGroup(uuid, rankname);

		ProxiedPlayer ply = FoxBungee.instance.getProxy().getPlayer(uuid);
		if (ply == null) return;

		setPlayerListName(ply);
	}
	
	public void setPlayerListName(ProxiedPlayer ply) {
		try {
			String listName = formatPlayer(ply);
			if(listName.length() > 16) listName = listName.substring(0, 15);
			ply.setTabListName(listName);
		} catch(Exception ignored) { }
	}

	//Permission levels
	public Map<String,String> ranklevels = FoxBungee.instance.redisManager.createCachedRedisMap("ranklevels");
	public static int getPlayerLevel(CommandSender ply) {
		if(!(ply instanceof ProxiedPlayer))
			return 9999;
		return getPlayerLevel(((ProxiedPlayer) ply).getUniqueId());
	}

	public static int getPlayerLevel(UUID uuid) {
		return FoxBungee.instance.playerHelper.getRankLevel(getPlayerRank(uuid));
	}

	public int getRankLevel(String rankname) {
		rankname = rankname.toLowerCase();
		if (rankname.equals("doridian"))
			return 666;

		final String rankLevelString = ranklevels.get(rankname);
		if (rankLevelString == null)
			return 0;

		return Integer.parseInt(rankLevelString);
	}

	//Tags
	private final Map<String,String> rankTags = FoxBungee.instance.redisManager.createCachedRedisMap("ranktags");
	private final Map<String,String> playerTags = FoxBungee.instance.redisManager.createCachedRedisMap("playerTags");
	private final Map<String,String> playerRankTags = FoxBungee.instance.redisManager.createCachedRedisMap("playerRankTags");

	public String getPlayerTag(ProxiedPlayer commandSender) {
		return getPlayerTag(commandSender.getUniqueId());
	}

	public String getPlayerRankTag(UUID uuid) {
		final String rank = getPlayerRank(uuid).toLowerCase();
		if (playerRankTags.containsKey(uuid.toString()))
			return playerRankTags.get(uuid.toString());

		if (rankTags.containsKey(rank))
			return rankTags.get(rank);

		return "\u00a77";
	}

	public String getPlayerTag(UUID uuid) {
		final String rankTag = getPlayerRankTag(uuid);

		if (playerTags.containsKey(uuid.toString()))
			return playerTags.get(uuid.toString()) + " " + rankTag;

		return rankTag;
	}
	public void setPlayerTag(UUID uuid, String tag, boolean rankTag) {
		final Map<String, String> tags = rankTag ? playerRankTags : playerTags;
		if (tag == null)
			tags.remove(uuid.toString());
		else
			tags.put(uuid.toString(), tag);
	}

	private Map<String,String> playernicks = FoxBungee.instance.redisManager.createCachedRedisMap("playernicks");

	private String getPlayerNick(UUID uuid) {
		if(playernicks.containsKey(uuid.toString()))
			return playernicks.get(uuid.toString());
		else
			return null;
	}

	public void setPlayerDisplayName(ProxiedPlayer player) {
		String nick = getPlayerNick(player.getUniqueId());
		if (nick == null)
			nick = player.getName();
		player.setDisplayName(nick);
	}

	public void setPlayerNick(UUID uuid, String tag) {
		if (tag == null)
			playernicks.remove(uuid.toString());
		else
			playernicks.put(uuid.toString(), tag);
	}

	public String formatPlayerFull(String playerName, UUID uuid) {
		String nick = getPlayerNick(uuid);
		if (nick == null)
			nick = playerName;

		return getPlayerTag(uuid) + nick;
	}

	public String formatPlayer(ProxiedPlayer player) {
		return getPlayerRankTag(player.getUniqueId()) + player.getName();
	}

	private static final Set<String> guestRanks = new HashSet<>(Arrays.asList("guest", "pohr"));
	public boolean isGuest(final ProxiedPlayer player) {
		return isGuestRank(getPlayerRank(player));
	}

	public static boolean isGuestRank(final String rank) {
		return guestRanks.contains(rank);
	}
}
