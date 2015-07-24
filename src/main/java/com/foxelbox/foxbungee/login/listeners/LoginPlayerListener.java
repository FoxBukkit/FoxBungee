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

import com.foxelbox.foxbungee.login.SkinFetcher;
import com.foxelbox.foxbungee.main.FoxBungee;
import com.foxelbox.foxbungee.main.FoxBungeeListener;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;
import java.util.UUID;

public class LoginPlayerListener extends FoxBungeeListener {
    @EventHandler
    public void onPlayerPreLogin(PreLoginEvent event) {
        final PendingConnection pendingConnection = event.getConnection();
        final String name = pendingConnection.getName();

        final String vHost = pendingConnection.getVirtualHost().getHostName().split("\\.")[0];

        final String redisKey = "foxbungee:prelogin:" + name;

        final String data = FoxBungee.instance.redisManager.get(redisKey);

        if(data != null) {
            String[] dataSplit = data.split("\\|");
            if(dataSplit[0].equalsIgnoreCase(vHost)) {
                UUID uuid = UUID.fromString(dataSplit[1]);
                pendingConnection.setOnlineMode(false);
                pendingConnection.setUniqueId(uuid);

                SkinFetcher.addFetcher(uuid);
            }
        }
    }

    private final Field LOGIN_PROFILE_FIELD;

    public LoginPlayerListener() {
        Field loginProfileField = null;
        try {
            loginProfileField = InitialHandler.class.getDeclaredField("loginProfile");
            loginProfileField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            loginProfileField = null;
        }
        LOGIN_PROFILE_FIELD = loginProfileField;
    }

    @EventHandler
    public void onPlayerPostLogin(PostLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        InitialHandler initialHandler = (InitialHandler)event.getPlayer().getPendingConnection();
        LoginResult profile = initialHandler.getLoginProfile();
        if(profile == null) {
            if(LOGIN_PROFILE_FIELD == null) {
                return;
            }
            profile = new LoginResult(uuid.toString(), new LoginResult.Property[0]);
            try {
                LOGIN_PROFILE_FIELD.set(initialHandler, profile);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }

        LoginResult.Property[] properties = profile.getProperties();
        boolean hasTextures = false;
        for(LoginResult.Property property : properties) {
            if(property.getName().equals(SkinFetcher.TEXTURES_NAME)) {
                if(property.getSignature() == null) {
                    break;
                }
                hasTextures = true;
                SkinFetcher.saveTextures(uuid, property);
                break;
            }
        }

        if(hasTextures) {
            return;
        }

        LoginResult.Property texturesProperty = SkinFetcher.getTextures(uuid);
        if(texturesProperty == null) {
            return;
        }

        LoginResult.Property[] newProperties = new LoginResult.Property[properties.length + 1];
        System.arraycopy(properties, 0, newProperties, 0, properties.length);
        newProperties[properties.length] = texturesProperty;
        profile.setProperties(newProperties);
    }
}
