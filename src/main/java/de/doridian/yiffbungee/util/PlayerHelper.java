package de.doridian.yiffbungee.util;

import de.doridian.yiffbungee.YiffBungee;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.tab.TabListAdapter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHelper {
	private YiffBungee plugin;
	public Map<String, String> conversations = new HashMap<>();

	public PlayerHelper(YiffBungee plug) {
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
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;
		YiffBungee.instance.getProxy().broadcast(msg);
	}

	public static void sendServerMessage(String msg, int minLevel) {
		sendServerMessage(msg, minLevel, '5');
	}
	public static void sendServerMessage(String msg, int minLevel, char colorCode) {
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;

		Collection<ProxiedPlayer> proxiedPlayers = YiffBungee.instance.getProxy().getPlayers();

		for (ProxiedPlayer player : proxiedPlayers) {
			if (getPlayerLevel(player) < minLevel)
				continue;
			player.sendMessage(msg);
		}
	}

	
	/**
	 * Broadcasts a message to all ProxiedPlayers with the given permission, prefixed with [YB] in purple.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 */
	public static void sendServerMessage(String message, String permission) {
		sendServerMessage(message, permission, '5');
	}
	/**
	 * Broadcasts a message to all ProxiedPlayers with the given permission, prefixed with [YB] in the given color.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 * @param colorCode The color code to prefix
	 */
	public static void sendServerMessage(String message, String permission, char colorCode) {
		broadcastMessage("\u00a7"+colorCode+"[YB]\u00a7f " + message, permission);
	}

	/**
	 * Broadcasts a message to all ProxiedPlayers with the given permission.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 */
	public static void broadcastMessage(String message, String permission) {
		Collection<ProxiedPlayer> proxiedPlayers = YiffBungee.instance.getProxy().getPlayers();

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
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;

		Set<ProxiedPlayer> exceptPlayersSet = new HashSet<>();
		for (CommandSender exceptPlayer : exceptPlayers) {
			if (!(exceptPlayer instanceof ProxiedPlayer))
				continue;

			exceptPlayersSet.add((ProxiedPlayer)exceptPlayer);
		}

		Collection<ProxiedPlayer> proxiedPlayers = YiffBungee.instance.getProxy().getPlayers();

		for (ProxiedPlayer player : proxiedPlayers) {
			if (exceptPlayersSet.contains(player))
				continue;

			player.sendMessage(msg);
		}
	}

	public static void sendDirectedMessage(CommandSender commandSender, String msg, char colorCode) {
		commandSender.sendMessage("\u00a7"+colorCode+"[YB]\u00a7f " + msg);
	}
	public static void sendDirectedMessage(CommandSender commandSender, String msg) {
		sendDirectedMessage(commandSender, msg, '5');
	}

	//Ranks
	public static String getPlayerRank(ProxiedPlayer ply) {
		return getPlayerRank(ply.getName());
	}
	public static String getPlayerRank(String name) {
		final String rank = YiffBungeePermissionHandler.instance.getGroup(name);
		if (rank == null)
			return "guest";

		return rank;
	}
	public void setPlayerRank(String name, String rankname) {
		if(getPlayerRank(name).equalsIgnoreCase(rankname)) return;
		YiffBungeePermissionHandler.instance.setGroup(name, rankname);

		ProxiedPlayer ply = YiffBungee.instance.getProxy().getPlayer(name);
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
	public Map<String,String> ranklevels = RedisManager.createKeptMap("ranklevels");
	public static int getPlayerLevel(CommandSender ply) {
		if(!(ply instanceof ProxiedPlayer))
			return 9999;
		return getPlayerLevel(ply.getName());
	}

	public static int getPlayerLevel(String name) {
		if(name.equals("[CONSOLE]"))
			return 9999;

		return YiffBungee.instance.playerHelper.getRankLevel(getPlayerRank(name));
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
	private final Map<String,String> rankTags = RedisManager.createKeptMap("ranktags");
	private final Map<String,String> playerTags = RedisManager.createKeptMap("playerTags");
	private final Map<String,String> playerRankTags = RedisManager.createKeptMap("playerRankTags");

	public String getPlayerTag(CommandSender commandSender) {
		return getPlayerTag(commandSender.getName());
	}

	public String getPlayerRankTag(String name) {
		name = name.toLowerCase();
		final String rank = getPlayerRank(name).toLowerCase();
		if (playerRankTags.containsKey(name))
			return playerRankTags.get(name);

		if (rankTags.containsKey(rank))
			return rankTags.get(rank);

		return "\u00a77";
	}

	public String getPlayerTag(String name) {
		name = name.toLowerCase();
		final String rankTag = getPlayerRankTag(name);

		if (playerTags.containsKey(name))
			return playerTags.get(name) + " " + rankTag;

		return rankTag;
	}
	public void setPlayerTag(String name, String tag, boolean rankTag) {
		name = name.toLowerCase();
		final Map<String, String> tags = rankTag ? playerRankTags : playerTags;
		if (tag == null)
			tags.remove(name);
		else
			tags.put(name, tag);
	}

	private Map<String,String> playernicks = RedisManager.createKeptMap("playernicks");

	private String getPlayerNick(String name) {
		name = name.toLowerCase();
		if(playernicks.containsKey(name))
			return playernicks.get(name);
		else
			return null;
	}

	public void setPlayerDisplayName(ProxiedPlayer player) {
		String nick = getPlayerNick(player.getName());
		if (nick == null)
			nick = player.getName();
		player.setDisplayName(nick);
	}

	public void setPlayerNick(String name, String tag) {
		name = name.toLowerCase();
		if (tag == null)
		{
			playernicks.remove(name);
		}
		else
			playernicks.put(name, tag);
	}

	public String formatPlayerFull(String playerName) {
		String nick = getPlayerNick(playerName);
		if (nick == null)
			nick = playerName;

		return getPlayerTag(playerName) + nick;
	}

	public String getPlayerNameByIP(String ip) {
		for (ProxiedPlayer onlinePlayer : plugin.getProxy().getPlayers()) {
			final String address = onlinePlayer.getAddress().getAddress().getHostAddress();
			if (!address.equals(ip))
				continue;

			return onlinePlayer.getName();
		}

		/*String offlinePlayerName = plugin.playerListener.offlinePlayers.get(ip);
		if (offlinePlayerName != null)
			return "\u00a77"+offlinePlayerName+"\u00a7f";*/

		return ip;
	}

	public String formatPlayer(ProxiedPlayer player) {
		final String playerName = player.getName();
		return getPlayerRankTag(playerName) + playerName;
	}

	private static final Set<String> guestRanks = new HashSet<>(Arrays.asList("guest", "pohr"));
	public boolean isGuest(final ProxiedPlayer player) {
		return isGuestRank(getPlayerRank(player));
	}

	public static boolean isGuestRank(final String rank) {
		return guestRanks.contains(rank);
	}

    public static final HashMap<String, String> playerHosts = new HashMap<>();
    public static final HashMap<String, String> playerIPs = new HashMap<>();

    public static String getPlayerIP(CommandSender player) {
        return getPlayerIP(player.getName());
    }

    public static String getPlayerIP(String player) {
        synchronized(playerIPs) {
            return playerIPs.get(player.toLowerCase());
        }
    }

    public static String getPlayerHost(CommandSender player) {
        return getPlayerHost(player.getName());
    }

    public static String getPlayerHost(String player) {
        synchronized(playerIPs) {
            return playerHosts.get(player.toLowerCase());
        }
    }
}
