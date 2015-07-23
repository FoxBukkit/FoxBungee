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
package com.foxelbox.foxbungee.login;

import com.foxelbox.foxbungee.main.FoxBungee;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import net.md_5.bungee.connection.LoginResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class SkinFetcher {
    public static final String TEXTURES_NAME = "textures";

    private static final Queue<UUID> texturesToFetch;
    public static void addFetcher(UUID uuid) {
        synchronized (texturesToFetch) {
            texturesToFetch.add(uuid);
            texturesToFetch.notify();
        }
    }

    private static final File TEXTURES_FOLDER;
    static {
        texturesToFetch = new LinkedBlockingQueue<>();
        TEXTURES_FOLDER = new File(FoxBungee.instance.getDataFolder(), "textures");
        TEXTURES_FOLDER.mkdirs();
        Thread t = new GroupedThreadFactory(FoxBungee.instance, "TextureFetcher").newThread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        final UUID uuid;
                        synchronized (texturesToFetch) {
                            uuid = texturesToFetch.poll();
                        }
                        if (uuid == null) {
                            synchronized (texturesToFetch) {
                                texturesToFetch.wait();
                            }
                            continue;
                        }
                        getTextures(uuid);
                    } catch (InterruptedException e) { }
                }
            }
        });
        t.setName("TextureFetcher-Queue");
        t.setDaemon(true);
        t.start();
    }

    private static LoginResult.Property fromJSON(JSONObject object) {
        return new LoginResult.Property((String)object.get("name"), (String)object.get("value"), (String)object.get("signature"));
    }

    public static LoginResult.Property getTextures(UUID uuid) {
        synchronized (TEXTURES_FOLDER) {
            File skinFile = new File(TEXTURES_FOLDER, uuid.toString() + ".json");
            if (skinFile.exists()) {
                try {
                    return fromJSON((JSONObject)new JSONParser().parse(new FileReader(skinFile)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "")).openConnection();
                if(httpURLConnection.getResponseCode() != 200) {
                    return null;
                }
                JSONObject object = (JSONObject)new JSONParser().parse(new InputStreamReader(httpURLConnection.getInputStream()));
                JSONArray properties = (JSONArray)object.get("properties");
                for(Object obj : properties) {
                    JSONObject property = (JSONObject)obj;
                    if(property.get("name").equals(TEXTURES_NAME)) {
                        LoginResult.Property textureProperty = fromJSON(property);
                        saveTextures(uuid, textureProperty);
                        return textureProperty;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void saveTextures(UUID uuid, LoginResult.Property property) {
        synchronized (TEXTURES_FOLDER) {
            File skinFile = new File(TEXTURES_FOLDER, uuid.toString() + ".json");
            JSONObject texturesObject = new JSONObject();
            texturesObject.put("name", property.getName());
            texturesObject.put("value", property.getValue());
            texturesObject.put("signature", property.getSignature());
            try {
                texturesObject.writeJSONString(new FileWriter(skinFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
