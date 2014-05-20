/**
 * This file is part of YiffBungee.
 *
 * YiffBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbungee.permissions.util;

import de.doridian.yiffbungee.main.YiffBungee;
import de.doridian.yiffbungee.permissions.YiffBungeePermissionHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Hashtable;
import java.util.UUID;

public class PermHelper {
	public String getPlayerRank(ProxiedPlayer ply) {
		return getPlayerRank(ply.getUniqueId());
	}
	public String getPlayerRank(UUID name) {
		final String rank = YiffBungeePermissionHandler.instance.getGroup(name);
		if (rank == null)
			return "guest";

		return rank;
	}
	public void setPlayerRank(UUID name, String rankname) {
		if(getPlayerRank(name).equalsIgnoreCase(rankname)) return;
		YiffBungeePermissionHandler.instance.setGroup(name, rankname);

		ProxiedPlayer ply = ProxyServer.getInstance().getPlayer(name);
		if (ply == null) return;

		YiffBungee.instance.playerHelper.setPlayerListName(ply);
	}

	//Permission levels
	public Hashtable<String,Integer> ranklevels = new Hashtable<String,Integer>();
	public int getPlayerLevel(ProxiedPlayer ply) {
		return getPlayerLevel(ply.getUniqueId());
	}

	public int getPlayerLevel(UUID name) {
		return getRankLevel(getPlayerRank(name));
	}

	public int getRankLevel(String rankname) {
		rankname = rankname.toLowerCase();
		final Integer rankLevel = ranklevels.get(rankname);
		if (rankLevel == null)
			return 0;

		return rankLevel;
	}
}
