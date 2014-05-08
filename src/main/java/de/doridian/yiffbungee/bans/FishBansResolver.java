package de.doridian.yiffbungee.bans;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FishBansResolver {
	public static HashMap<String, Integer> getBanCounts(String username) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("http://api.fishbans.com/bans/" + username).openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(5000);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
			//->bans->service->[key]->bans=[value]
			JSONObject serviceBans = (JSONObject)((JSONObject)jsonObject.get("bans")).get("service");
			HashMap<String, Integer> result = new HashMap<>();
			for(Object banEntry : serviceBans.entrySet()) {
				Map.Entry actualBanEntry = (Map.Entry)banEntry;
				result.put((String)actualBanEntry.getKey(), (int)(long)(Long)(((JSONObject)actualBanEntry.getValue()).get("bans")));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	private static final HttpProfileRepository HTTP_PROFILE_REPOSITORY = new HttpProfileRepository("minecraft");

	private static final LoadingCache<String, UUID> playerUUIDMap = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).softValues().build(new CacheLoader<String, UUID>() {
		@Override
		public UUID load(String username) throws Exception {
			Profile[] profiles = HTTP_PROFILE_REPOSITORY.findProfilesByNames(username);
			if(profiles.length == 1) {
				String uuidStr = profiles[0].getId();
				if(uuidStr.indexOf('-') < 1)
					uuidStr = uuidStr.substring(0, 8) + "-" + uuidStr.substring(8, 12) + "-" + uuidStr.substring(12, 16) + "-" + uuidStr.substring(16, 20) + "-" + uuidStr.substring(20);
				return UUID.fromString(uuidStr);
			}
			return null;
		}
	});

	public static UUID getUUID(String username) {
		try {
			return playerUUIDMap.get(username.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
