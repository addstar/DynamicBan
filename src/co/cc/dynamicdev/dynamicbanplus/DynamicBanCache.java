package co.cc.dynamicdev.dynamicbanplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DynamicBanCache {
	private static Map<String, String> bannedplayers = new HashMap<String, String>();
	private static File bannedplayerfilepath = new File("plugins" + File.separator + "DynamicBan" + File.separator + "data" + File.separator + "banned-players.dat");
	private static FileConfiguration bannedplayerfile = YamlConfiguration.loadConfiguration(bannedplayerfilepath);

	private static Map<String, String> bannedips = new HashMap<String, String>();
	private static File bannedipfilepath = new File("plugins/DynamicBan/data", "banned-ips.dat");
	private static FileConfiguration bannedipfile = YamlConfiguration.loadConfiguration(bannedipfilepath);

	private static Map<String, String> tempbans = new HashMap<String, String>();
	private static File tempbanfilepath = new File("plugins/DynamicBan/data", "temp-bans.dat");
	private static FileConfiguration tempbanfile = YamlConfiguration.loadConfiguration(tempbanfilepath);

	private static Map<String, String> executors = new HashMap<String, String>();
	private static File executorfilepath = new File("plugins/DynamicBan/data", "banned-by.dat");
	private static FileConfiguration executorfile = YamlConfiguration.loadConfiguration(executorfilepath);

	private static Map<String, String> timestamps = new HashMap<String, String>();
	private static File timestampfilepath = new File("plugins/DynamicBan/data", "ban-time.dat");
	private static FileConfiguration timestampfile = YamlConfiguration.loadConfiguration(timestampfilepath);

	private static Map<String, String> bannedranges = new HashMap<String, String>();
	private static File bannedrangefilepath = new File("plugins/DynamicBan/data", "range-bans.dat");
	private static FileConfiguration bannedrangefile = YamlConfiguration.loadConfiguration(bannedrangefilepath);

	private static ArrayList<String> immuneplayers = new ArrayList<String>();
	private static ArrayList<String> whitelist = new ArrayList<String>();
	private static File immuneplayerfilepath = new File("plugins/DynamicBan/data", "immune-players .dat");
	private static FileConfiguration immuneplayerfile = YamlConfiguration.loadConfiguration(immuneplayerfilepath);

	private static Map<String, String> mutedplayers = new HashMap<String, String>();
	private static File mutedplayerfilepath = new File("plugins/DynamicBan/data", "muted-players.dat");
	private static FileConfiguration mutedplayerfile = YamlConfiguration.loadConfiguration(mutedplayerfilepath);

	private static Map<String, String> lockedips = new HashMap<String, String>();
	private static File lockedipfilepath = new File("plugins/DynamicBan/data/", "locked-ips.dat");
	private static FileConfiguration lockedipfile = YamlConfiguration.loadConfiguration(lockedipfilepath);

	private static Map<String, String> iplogs = new HashMap<String, String>();
	private static Map<String, String> currentips = new HashMap<String, String>();
	private static File iplogfilepath = new File("plugins/DynamicBan/data", "ip-log.dat");
	private static FileConfiguration iplogfile = YamlConfiguration.loadConfiguration(iplogfilepath);

	public static void loadAll() {
		Set<String> bannedplayerlist = bannedplayerfile.getKeys(false);
		for (String s : bannedplayerlist) {
			if (!bannedplayers.containsKey(s)) {
				bannedplayers.put(s, bannedplayerfile.getString(s));
			}
		}
		Set<String> bannediplist = bannedipfile.getKeys(false);
		for (String s : bannediplist) {
			if (!bannedips.containsKey(s)) {
				bannedips.put(s, bannedipfile.getString(s));
			}
		}
		Set<String> tempbanlist = tempbanfile.getKeys(false);
		for (String s : tempbanlist) {
			if (!tempbans.containsKey(s)) {
				tempbans.put(s, tempbanfile.getString(s));
			}
		}
		Set<String> executorlist = executorfile.getKeys(false);
		for (String s : executorlist) {
			if (!executors.containsKey(s)) {
				executors.put(s, executorfile.getString(s));
			}
		}
		Set<String> timestamplist = timestampfile.getKeys(false);
		for (String s : timestamplist) {
			if (!timestamps.containsKey(s)) {
				timestamps.put(s, timestampfile.getString(s));
			}
		}
		Set<String> mutedplayerlist = mutedplayerfile.getKeys(false);
		for (String s : mutedplayerlist) {
			if (!mutedplayers.containsKey(s)) {
				mutedplayers.put(s, mutedplayerfile.getString(s));
			}
		}
		Set<String> lockediplist = lockedipfile.getKeys(false);
		for (String s : lockediplist) {
			if (!lockedips.containsKey(s)) {
				lockedips.put(s, lockedipfile.getString(s));
			}
		}
		Set<String> bannedrangelist = bannedrangefile.getKeys(false);
		for (String s : bannedrangelist) {
			if (!bannedranges.containsKey(s)) {
				bannedranges.put(s, bannedrangefile.getString(s));
			}
		}
		if (!immuneplayerfile.contains("immune")) {
			immuneplayerfile.createSection("immune");
		}
		Set<String> immuneplayerlist = immuneplayerfile.getConfigurationSection("immune").getKeys(false);
		for (String s : immuneplayerlist) {
			if (!immuneplayers.contains(s)) {
				immuneplayers.add(s);
			}
		}
		if (!immuneplayerfile.contains("whitelist")) {
			immuneplayerfile.createSection("whitelist");
		}
		Set<String> whitelistlist = immuneplayerfile.getConfigurationSection("whitelist").getKeys(false);
		for (String s: whitelistlist) {
			if (!whitelist.contains(s)) {
				whitelist.add(s);
			}
		}
		Set<String> iploglist = iplogfile.getKeys(false);
		for (String s : iploglist) {
			if (!iplogs.containsKey(s)) {
				iplogs.put(s, iplogfile.getString(s));
			}
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			currentips.put(p.getName().toLowerCase(), p.getAddress().toString().split("/")[1].split(":")[0].replace(".", "/"));
		}
	}

	public static void reloadAll() {
		bannedplayers.clear();
		bannedips.clear();
		tempbans.clear();
		bannedranges.clear();
		executors.clear();
		timestamps.clear();
		mutedplayers.clear();
		lockedips.clear();
		immuneplayers.clear();
		whitelist.clear();
		iplogs.clear();
		currentips.clear();
		loadAll();
	}

	public static String getPlayerBan(String name) {
		return bannedplayers.get(name);
	}

	public static String getIpBan(String ip) {
		return bannedips.get(ip);
	}

	public static String getTempBan(String name) {
		if (tempbans.containsKey(name)) {
			return tempbans.get(name);
		}
		return null;
	}

	public static String getExecutor(String nameip) {
		return executors.get(nameip);
	}

	public static String getTime(String nameip) {
		return timestamps.get(nameip);
	}

	public static String getMute(String name) {
		if (mutedplayers.containsKey(name)) {
			return mutedplayers.get(name);
		}
		return null;
	}

	public static String getIpLock(String ip) {
		return lockedips.get(ip);
	}

	public static String getRangeBan(String range) {
		return bannedranges.get(range);
	}

	public static String getLoggedIp(String ip) {
		return iplogs.get(ip);
	}

	public static boolean isImmune(String name) {
		if (immuneplayers.contains(name)) {
			return true;
		}
		return false;
	}

	public static boolean isWhitelisted(String name) {
		if (whitelist.contains(name)) {
			return true;
		}
		return false;
	}
	
	public static String getIp(String name) {
		return currentips.get(name.toLowerCase());
	}
	
	public static int getPlayersWithIp(String ip) {
		int count = 0;
		for (String s : currentips.values()) {
			if (s.equals(ip)) {
				count++;
			}
		}
		return count;
	}
	
	public static void setIp(String p, String ip) {
		currentips.put(p, ip);
	}
	
	public static void removeIp(String name) {
		currentips.remove(name.toLowerCase());
	}

	public static void addPlayerBan(String name, String reason, String executor, String date) {
		bannedplayers.remove(name);
		bannedplayers.put(name, reason);
		if (!executors.containsKey(name)) {
			executors.put(name, executor);
		}
		if (!timestamps.containsKey(name)) {
			timestamps.put(name, date);
		}

		bannedplayerfile.set(name, reason);
		saveBans();
		if (!executorfile.contains(name)) {
			executorfile.set(name, executor);
			saveExecutors();
		}
		if (!timestampfile.contains(name)) {
			timestampfile.set(name, date);
			saveTimestamps();
		}
	}

	public static void addIpBan(String ip, String reason, String executor, String date) {
		bannedips.remove(ip);
		bannedips.put(ip, reason);
		if (!executors.containsKey(ip)) {
			executors.put(ip, executor);
		}
		if (!timestamps.containsKey(ip)) {
			timestamps.put(ip, date);
		}

		bannedipfile.set(ip, reason);
		saveIpBans();
		if (!executorfile.contains(ip)) {
			executorfile.set(ip, executor);
			saveExecutors();
		}
		if (!timestampfile.contains(ip)) {
			timestampfile.set(ip, date);
			saveTimestamps();
		}
	}

	public static void addTempBan(String nameip, String time, String executor, String date) {
		tempbans.remove(nameip);
		tempbans.put(nameip, time);
		if (!executors.containsKey(nameip)) {
			executors.put(nameip, executor);
		}
		if (!timestamps.containsKey(nameip)) {
			timestamps.put(nameip, date);
		}

		tempbanfile.set(nameip, time);
		saveTempBans();
		if (!executorfile.contains(nameip)) {
			executorfile.set(nameip, executor);
			saveExecutors();
		}
		if (!timestampfile.contains(nameip)) {
			timestampfile.set(nameip, date);
			saveTimestamps();
		}
	}

	public static void addRangeBan(String range, String reason, String executor, String date, String name) {
		bannedranges.remove(range);
		bannedranges.put(range, reason);
		if (!executors.containsKey(name)) {
			executors.put(name, executor);
		}
		if (!timestamps.containsKey(name)) {
			timestamps.put(name, date);
		}

		bannedrangefile.set(range, reason);
		saveRangeBans();
		if (!executorfile.contains(name)) {
			executorfile.set(name, executor);
			saveExecutors();
		}
		if (!timestampfile.contains(name)) {
			timestampfile.set(name, date);
			saveTimestamps();
		}
	}

	public static void addMute(String name, String time) {
		mutedplayers.remove(name);
		mutedplayers.put(name, time);

		mutedplayerfile.set(name, time);
		saveMutes();
	}

	public static void addIpLock(String ip, String name) {
		lockedips.remove(ip);
		lockedips.put(ip, name);

		lockedipfile.set(ip, name);
		saveIpLocks();
	}

	public static void addImmunity(String name, String executor) {
		if (!immuneplayers.contains(name)) {
			immuneplayers.add(name);
		}

		immuneplayerfile.set("immune." + name, executor);
		saveImmunes();
	}

	public static void addWhitelisted(String name, String executor) {
		if (!whitelist.contains(name)) {
			whitelist.add(name);
		}

		immuneplayerfile.set("whitelist." + name, executor);
		saveImmunes();
	}

	public static void addLoggedIp(String ip, String player) {
		iplogs.remove(ip);
		iplogs.put(ip, player);

		iplogfile.set(ip, player);
		saveIpLogs();
	}

	public static void removePlayerBan(String name) {
		bannedplayers.remove(name);
		executors.remove(name);
		timestamps.remove(name);
		if (bannedplayerfile.contains(name)) {
			bannedplayerfile.set(name, null);
			saveBans();
		}
		if (executorfile.contains(name)) {
			executorfile.set(name, null);
			saveExecutors();
		}
		if (timestampfile.contains(name)) {
			timestampfile.set(name, null);
			saveTimestamps();
		}
	}

	public static void removeIpBan(String ip) {
		bannedips.remove(ip);
		executors.remove(ip);
		timestamps.remove(ip);
		if (bannedipfile.contains(ip)) {
			bannedipfile.set(ip, null);
			saveIpBans();
		}
		if (executorfile.contains(ip)) {
			executorfile.set(ip, null);
			saveExecutors();
		}
		if (timestampfile.contains(ip)) {
			timestampfile.set(ip, null);
			saveTimestamps();
		}
	}

	public static void removeTempBan(String nameip) {
		tempbans.remove(nameip);
		executors.remove(nameip);
		timestamps.remove(nameip);
		if (tempbanfile.contains(nameip)) {
			tempbanfile.set(nameip, null);
			saveTempBans();
		}
		if (executorfile.contains(nameip)) {
			executorfile.set(nameip, null);
			saveExecutors();
		}
		if (timestampfile.contains(nameip)) {
			timestampfile.set(nameip, null);
			saveTimestamps();
		}
	}

	public static void removeRangeBan(String range, String ip) {
		bannedranges.remove(range);
		executors.remove(ip);
		timestamps.remove(ip);
		if (bannedrangefile.contains(range)) {
			bannedrangefile.set(range, null);
			saveRangeBans();
		}
		if (executorfile.contains(ip)) {
			executorfile.set(ip, null);
			saveExecutors();
		}
		if (timestampfile.contains(ip)) {
			timestampfile.set(ip, null);
			saveTimestamps();
		}
	}

	public static void removeMute(String name) {
		mutedplayers.remove(name);
		if (mutedplayerfile.contains(name)) {
			mutedplayerfile.set(name, null);
			saveMutes();
		}
	}

	public static void removeIpLock(String ip) {
		lockedips.remove(ip);
		if (lockedipfile.contains(ip)) {
			lockedipfile.set(ip, null);
			saveIpLocks();
		}
	}

	public static void removeImmunity(String name) {
		immuneplayers.remove(name);
		if (immuneplayerfile.contains("immune." + name)) {
			immuneplayerfile.set("immune." + name, null);
			saveImmunes();
		}
	}

	public static void removeWhitelisted(String name) {
		whitelist.remove(name);
		if (immuneplayerfile.contains("whitelist." + name)) {
			immuneplayerfile.set("whitelist." + name, null);
			saveImmunes();
		}
	}

	public static void saveBans() {
		try {
			bannedplayerfile.save(bannedplayerfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveIpBans() {
		try {
			bannedipfile.save(bannedipfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveTempBans() {
		try {
			tempbanfile.save(tempbanfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveRangeBans() {
		try {
			bannedrangefile.save(bannedrangefilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveExecutors() {
		try {
			executorfile.save(executorfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveTimestamps() {
		try {
			timestampfile.save(timestampfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveMutes() {
		try {
			mutedplayerfile.save(mutedplayerfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveIpLocks() {
		try {
			lockedipfile.save(lockedipfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveImmunes() {
		try {
			immuneplayerfile.save(immuneplayerfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveIpLogs() {
		try {
			iplogfile.save(iplogfilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
