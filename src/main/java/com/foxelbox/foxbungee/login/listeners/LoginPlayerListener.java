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
package com.foxelbox.foxbungee.login.listeners;

import com.foxelbox.foxbungee.main.FoxBungee;
import com.foxelbox.foxbungee.main.FoxBungeeListener;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class LoginPlayerListener extends FoxBungeeListener {
    @EventHandler
    public void onPlayerPreLogin(LoginEvent event) {
        final PendingConnection pendingConnection = event.getConnection();
        final String name = pendingConnection.getName();
        //final String ip = pendingConnection.getAddress().getAddress().getHostAddress();
        final String vHost = pendingConnection.getVirtualHost().getHostName();
        final String redisKey = "foxbungee:prelogin:" + name + ":" + vHost;
        System.out.println(redisKey);
        final String data = FoxBungee.instance.redisManager.get(redisKey);
        if(data != null) {
            FoxBungee.instance.redisManager.del(redisKey);
            UUID uuid = UUID.fromString(data);
            pendingConnection.setOnlineMode(false);
            pendingConnection.setUniqueId(uuid);
        }
    }
}
