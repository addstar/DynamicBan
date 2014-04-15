package co.cc.dynamicdev.dynamicbanplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import co.cc.dynamicdev.dynamicbanplus.commands.BanPlayer;
import co.cc.dynamicdev.dynamicbanplus.commands.BanPlayerIP;
import co.cc.dynamicdev.dynamicbanplus.commands.CompareIP;
import co.cc.dynamicdev.dynamicbanplus.commands.IPList;
import co.cc.dynamicdev.dynamicbanplus.commands.ImmuneAddRemove;
import co.cc.dynamicdev.dynamicbanplus.commands.KickPlayer;
import co.cc.dynamicdev.dynamicbanplus.commands.LockIP;
import co.cc.dynamicdev.dynamicbanplus.commands.Mute;
import co.cc.dynamicdev.dynamicbanplus.commands.PlayerDetails;
import co.cc.dynamicdev.dynamicbanplus.commands.PlayerStanding;
import co.cc.dynamicdev.dynamicbanplus.commands.PurgeData;
import co.cc.dynamicdev.dynamicbanplus.commands.RangeBanIP;
import co.cc.dynamicdev.dynamicbanplus.commands.RangeUnbanIP;
import co.cc.dynamicdev.dynamicbanplus.commands.ReloadData;
import co.cc.dynamicdev.dynamicbanplus.commands.TempBan;
import co.cc.dynamicdev.dynamicbanplus.commands.TempBanIP;
import co.cc.dynamicdev.dynamicbanplus.commands.UnbanPlayer;
import co.cc.dynamicdev.dynamicbanplus.commands.UnbanPlayerIP;
import co.cc.dynamicdev.dynamicbanplus.commands.UnlockIP;
import co.cc.dynamicdev.dynamicbanplus.commands.Unmute;
import co.cc.dynamicdev.dynamicbanplus.commands.WarnPlayer;
import co.cc.dynamicdev.dynamicbanplus.commands.WhitelistAddRemove;

//Author: xDrapor
//The DynamicDev Team 
//DynamicBan - Comprehensive IP banning.
public class DynamicBan extends JavaPlugin implements Listener {
	public static Permission permission = null;
	public boolean bankicked = false;
	public String version;
	protected DynamicLogger log;
	protected DNSBL dnsbl;
	private static File configfile = new File("plugins/DynamicBan/config.yml");

	public static String tag = "";
	private String warnFormat = "";

	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	
	private void updateCheck() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		if(config.getBoolean("config.check_for_updates") == false) {
			System.out.println("[DynamicBan] Update checks disabled in the config.");
			return;
		}
		Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, "[DynamicBan] Checking the server for update info...");
		try {
			version = this.getDescription().getVersion();

			int updateVer;
			int curVer;
			int updateHot = 0;
			int curHot = 0;
			int updateBuild;
			int curBuild;

			URLConnection yc = new URL("https://raw.github.com/DJ4ddi/DynamicBan/master/version.txt").openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));


			String updateVersion = in.readLine().replace(".", "");

			if (Character.isLetter(updateVersion.charAt(updateVersion.length() - 1))) {
				updateHot = Character.getNumericValue(updateVersion.charAt(updateVersion.length() - 1));
				updateVer = Integer.parseInt(updateVersion.substring(0, updateVersion.length() - 1));
			} else {
				updateVer = Integer.parseInt(updateVersion);
			}

			if (Character.isLetter(version.charAt(version.length() - 1))) {
				String tversion = version.replace(".", "");
				curHot = Character.getNumericValue(tversion.charAt(tversion.length() - 1));
				curVer = Integer.parseInt(tversion.substring(0, tversion.length() - 1));
			} else {
				curVer = Integer.parseInt(version.replace(".", ""));
			}

			boolean updateAvailable = false;
			if (updateVer > curVer || updateVer == curVer && updateHot > curHot) {
				Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, "[DynamicBan] Update available! Check BukkitDev.");
				updateAvailable = true;
			} else {
				Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, "[DynamicBan] No update available.");
			}

			if (updateAvailable) {
				Pattern pattern = Pattern.compile("-b(\\d*?)jnks", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());
				if (!matcher.find() || matcher.group(1) == null) {
					curBuild = Integer.parseInt(matcher.group(1));
					updateBuild = Integer.parseInt(in.readLine());
					if (updateBuild > curBuild) {
						Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, "[DynamicBan] It is recommended to update Bukkit to version " + updateBuild);
					} else {
						Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, "[DynamicBan] The update should be compatible.");
					}
				} else {
					Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "[DynamicBan] The server version couldn't be parsed.");
				}
			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "[DynamicBan] Error performing update check!");
		}
	}

	@Override
	public void onEnable() {
		this.log = new DynamicLogger(this);
		DynamicBanCache.loadAll();
		FileConfiguration config = getConfig();
		config.options().copyHeader(true);
		config.options().copyDefaults(true);
		if (!config.contains("config.warn_results")) {
			config.set("config.warn_results.3", "dk {PLAYER} {REASON}");
			config.set("config.warn_results.5", "dtb {PLAYER} 15m {REASON}");
			config.set("config.warn_results.7", "dtb {PLAYER} 30m {REASON}");
			config.set("config.warn_results.9", "dtb {PLAYER} 60m {REASON}");
			config.set("config.warn_results.10", "db {PLAYER} {REASON}");
		}
		saveConfig();
		loadTag(config);
		loadWarnFormat(config);
		getServer().getScheduler().runTask(this, new Runnable() {
			@Override
			public void run() {
				updateCheck();
			}
		});
		getCommand("dynplayer").setExecutor(new PlayerDetails(this));
		getCommand("dynkick").setExecutor(new KickPlayer(this));
		getCommand("dynban").setExecutor(new BanPlayer(this));
		getCommand("dynbanip").setExecutor(new BanPlayerIP(this));
		getCommand("dynunban").setExecutor(new UnbanPlayer(this));
		getCommand("dynunbanip").setExecutor(new UnbanPlayerIP(this));
		getCommand("dynstanding").setExecutor(new PlayerStanding(this));
		getCommand("dyntempban").setExecutor(new TempBan(this));
		getCommand("dyntempbanip").setExecutor(new TempBanIP(this));
		getCommand("dynimmune").setExecutor(new ImmuneAddRemove(this));
		getCommand("dynwarn").setExecutor(new WarnPlayer(this));
		getCommand("dynpurge").setExecutor(new PurgeData(this));
		getCommand("dynlist").setExecutor(new IPList(this));
		getCommand("dynreload").setExecutor(new ReloadData(this));
		getCommand("dyncompare").setExecutor(new CompareIP(this));
		getCommand("dynmute").setExecutor(new Mute(this));
		getCommand("dynunmute").setExecutor(new Unmute(this));
		getCommand("dynlockip").setExecutor(new LockIP(this));
		getCommand("dynunlockip").setExecutor(new UnlockIP());
		getCommand("dynrangeban").setExecutor(new RangeBanIP(this));
		getCommand("dynunbanrange").setExecutor(new RangeUnbanIP(this));
		getCommand("dynwhitelist").setExecutor(new WhitelistAddRemove(this));
		getServer().getPluginManager().registerEvents(this, this);
		setupPermissions();
		if (!config.getString("config.dnsbl_result").equalsIgnoreCase("none")) {
			loadDnsblServices(config);
		}
		System.out.println("[DynaminBan] has been enabled (v" + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		System.out.println("[DynaminBan] has been disabled (v" + getDescription().getVersion() + ")");
	}

	public void loadTag(FileConfiguration config) {
		String t = config.getString("config.plugin_tag");
		tag = t.replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
	}
	
	public void loadWarnFormat(FileConfiguration config) {
		Matcher findFormat = Pattern.compile("(&([0-9a-fk-or])){1,2}(?=\\{WARNS\\})").matcher(config.getString("other_messages.warnings_message"));
		if (findFormat.find()) {
			warnFormat = findFormat.group().replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
		} else {
			warnFormat = "";
		}
	}
	
	public void loadDnsblServices(FileConfiguration config) {
		if (dnsbl == null) {
			try {
				dnsbl = new DNSBL();
			} catch (NamingException e) {
				System.out.println("[DynamicBan] Error initializing DNSBL lookups!");
			}
		}
		dnsbl.clearServices();
		for (String service : config.getStringList("config.dnsbl_services")) {
			dnsbl.addService(service);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(final PlayerJoinEvent event) throws IOException {
		String player = event.getPlayer().getName().toLowerCase();
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy '@' HH:mm");
		String date = sdf.format(today);
		String displayname = event.getPlayer().getDisplayName();
		File playerLoggerFile = new File("plugins/DynamicBan/playerdata/" + player.toLowerCase() + "/", "player.dat");
		FileConfiguration playerLogger = YamlConfiguration.loadConfiguration(playerLoggerFile);
		String ip = DynamicBanCache.getIp(player);
		if (ip != null) {
			ip.replace("/", ".");
		} else {
			event.setJoinMessage(null);
			if (event.getPlayer() != null) {
				event.getPlayer().kickPlayer("Login failed for an unknown reason. Please try again.");
			}
		}
		if (playerLogger.getString("Initial-IP-Address") == null) {
			playerLogger.set("DisplayName", displayname);
			playerLogger.set("Initial-IP-Address", ip);
			playerLogger.set("IP-Address", ip);
			playerLogger.set("Last-Joined", date);
			playerLogger.set("kickedNumber", 0);
			playerLogger.createSection("warns");
			playerLogger.save(playerLoggerFile);
		} else {
			playerLogger.set("DisplayName", displayname);
			playerLogger.set("IP-Address", ip);
			playerLogger.set("Last-Joined", date);
			if (!playerLogger.contains("warns")) {
				playerLogger.createSection("warns");
			}
			playerLogger.save(playerLoggerFile);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void iplimitLoginCheck(PlayerJoinEvent event) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		if (config.getInt("config.messages_per_ip") > 0) {
			if (DynamicBanCache.getPlayersWithIp(DynamicBanCache.getIp(event.getPlayer().getName())) > config.getInt("config.messages_per_ip")) {
				event.setJoinMessage(null);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void warnMessage(PlayerJoinEvent event) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		if (config.getBoolean("config.warns_on_login")) {
			final Player p = event.getPlayer();
			if (config.getInt("config.warns_on_login_delay") != 0) {
				getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					@Override
					public void run() {
						sendWarnings(p);
					}
				}, config.getInt("config.warns_on_login_delay") * 20);
			} else {
				sendWarnings(p);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void iplimitQuitCheck(PlayerQuitEvent event) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		if (config.getInt("config.messages_per_ip") > 0) {
			if (DynamicBanCache.getPlayersWithIp(DynamicBanCache.getIp(event.getPlayer().getName())) > config.getInt("config.messages_per_ip")) {
				event.setQuitMessage(null);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		final String pname = event.getPlayer().getName();
		getServer().getScheduler().runTask(this, new Runnable() {
			@Override
			public void run() {
				Player p = getServer().getPlayerExact(pname);
				if (p == null || !p.isOnline()) {
					DynamicBanCache.removeIp(pname);
				}
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		DynamicBanCache.setIp(event.getPlayer().getName().toLowerCase(), event.getAddress().toString().split("/")[1].split(":")[0].replace(".", "/"));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatMuteCheck(AsyncPlayerChatEvent event) {
		if (event.getMessage() != null) {
			Player player = event.getPlayer();
			String pname = player.getName().toLowerCase();
			String mute = DynamicBanCache.getMute(pname);
			if (mute != null) {
				String[] muteField = mute.split("::");
				long tempTime = Long.valueOf(muteField[0]);
				long now = System.currentTimeMillis() / 1000;
				long diff = tempTime - now;
				if (DynamicBanCache.isImmune(pname)) {
					return;
				}
				if (diff <= 0 ){
					DynamicBanCache.removeMute(pname);
				} else {
					event.setCancelled(true);
					event.setMessage(null);
					FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
					String muteReason = muteField[2];
					if (muteReason.equals("None")) {
						muteReason = config.getString("other_messages.default_reason");
					}
					String muteMsg = config.getString("messages.muted_message").replace("{REASON}", muteReason).replace("{SENDER}", muteField[1]).replace("{TIME}", diff + " seconds").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
					player.sendMessage(muteMsg);
				}
			}
		}
	}   

	@EventHandler(priority = EventPriority.HIGHEST)
	public void commandMuteCheck(PlayerCommandPreprocessEvent event){
		String pname = event.getPlayer().getName().toLowerCase();
		if (DynamicBanCache.isImmune(pname)) {
			return;
		}
		String mute = DynamicBanCache.getMute(pname);
		if (mute != null) {
			String[] muteField = mute.split("::");
			long tempTime = Long.valueOf(muteField[0]);
			long now = System.currentTimeMillis() / 1000;
			long diff = tempTime - now;
			FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
			List<String> command = config.getStringList("config.mute.blocked_commands");
			if (diff > 0) {
				for (String i : command) {
					if (event.getMessage().startsWith("/"+i)) {
						String muteReason = muteField[2];
						if (muteReason.equals("None")) {
							muteReason = config.getString("other_messages.default_reason");
						}
						String muteMsg = config.getString("messages.muted_command_blocked").replace("{REASON}", muteReason).replace("{SENDER}", muteField[1]).replace("{TIME}", diff + " seconds").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
						event.getPlayer().sendMessage(muteMsg);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void ipLimitCheck(PlayerLoginEvent event) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		if (config.getInt("config.connections_per_ip") > 0) {
			if (!DynamicBanCache.isWhitelisted(event.getPlayer().getName().toLowerCase()) && !DynamicBanCache.isWhitelisted(DynamicBanCache.getIp(event.getPlayer().getName()))) {
				if (DynamicBanCache.getPlayersWithIp(DynamicBanCache.getIp(event.getPlayer().getName())) > config.getInt("config.connections_per_ip")) {
					event.disallow(PlayerLoginEvent.Result.KICK_OTHER, config.getString("messages.ip_connections_message").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void banCheck(PlayerLoginEvent event) {
		String pname = event.getPlayer().getName().toLowerCase();
		String banReason = DynamicBanCache.getPlayerBan(pname);
		if (banReason != null && !DynamicBanCache.isWhitelisted(pname)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
			if (banReason.equals("None")) {
				banReason = config.getString("other_messages.default_reason");
			}
			String banMsg = config.getString("messages.ban_message").replace("{REASON}", banReason).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMsg);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void ipbanCheck(final PlayerLoginEvent event) {
		String pname = event.getPlayer().getName().toLowerCase();
		String iptoban = DynamicBanCache.getIp(pname);
		String banReason = DynamicBanCache.getIpBan(iptoban);
		if (banReason != null  && !DynamicBanCache.isWhitelisted(pname)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
			if (banReason.equals("None")) {
				banReason = config.getString("other_messages.default_reason");
			}
			String banMsg = config.getString("messages.ip_ban_message").replace("{REASON}", banReason).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMsg);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void tempbanCheck(PlayerLoginEvent event) {
		String pname = event.getPlayer().getName().toLowerCase();
		String tempBan = DynamicBanCache.getTempBan(pname);
		if (tempBan != null) {
			long tempTime = Long.valueOf(tempBan.split("::")[0]);
			long now = System.currentTimeMillis() / 1000;
			long diff = tempTime - now;
			if (diff <= 0) {
				event.getPlayer().setBanned(false);
				DynamicBanCache.removeTempBan(pname);
				event.allow();
			} else {
				if(!DynamicBanCache.isWhitelisted(pname)) {
					FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
					String banReason = tempBan.split("::")[1];
					if (banReason.equals("None")) {
						banReason = config.getString("other_messages.default_reason");
					}
					String banMsg = config.getString("messages.tempban_message").replace("{REASON}", banReason).replace("{TIME}", diff + " seconds").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
					event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMsg);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void iptempbanCheck(PlayerLoginEvent event) {
		String pname = event.getPlayer().getName().toLowerCase();
		String iptoban = DynamicBanCache.getIp(pname);
		String tempBan = DynamicBanCache.getTempBan(iptoban);
		if (tempBan != null) {
			long tempTime = Long.valueOf(tempBan.split("::")[0]);
			long now = System.currentTimeMillis() / 1000;
			long diff = tempTime - now;
			if (diff <= 0) {
				Bukkit.unbanIP(iptoban.replace("/", "."));
				DynamicBanCache.removeTempBan(iptoban);
				event.allow();
			} else {
				if (!DynamicBanCache.isWhitelisted(pname)) {
					FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
					String banReason = tempBan.split("::")[1];
					if (banReason.equals("None")) {
						banReason = config.getString("other_messages.default_reason");
					}
					String banMsg = config.getString("messages.ip_tempban_message").replace("{REASON}", banReason).replace("{TIME}", diff + " seconds").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
					event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMsg);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void rangebanCheck(PlayerLoginEvent event) {
		String pname = event.getPlayer().getName().toLowerCase();
		if (!DynamicBanCache.isWhitelisted(pname) && !DynamicBanCache.isWhitelisted(DynamicBanCache.getIp(pname))) {
			String[] IP = DynamicBanCache.getIp(pname).split("/");
			String banReason = null;
			if (DynamicBanCache.getRangeBan(IP[0]+ "/" + "*"+"/" + "*" +"/" + "*") != null && !DynamicBanCache.isWhitelisted(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*") && !DynamicBanCache.isWhitelisted(IP[0]+ "/" + IP[1] + "/" + "*" +"/" + "*")) {
				banReason = DynamicBanCache.getRangeBan(IP[0]+ "/" + "*"+"/" + "*" +"/" + "*");
			} 
			if (DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + "*" +"/" + "*") != null && !DynamicBanCache.isWhitelisted(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*")) {
				banReason = DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + "*" +"/" + "*");
			}
			if (DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*") != null) {
				banReason = DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*");
			} 
			if (banReason != null) {
				FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
				if (banReason.equals("None")) {
					banReason = config.getString("other_messages.default_reason");
				}
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, config.getString("messages.rangeban_message").replace("{REASON}", banReason).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void dnsblCheck(PlayerLoginEvent event) {
		final FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		final String result = config.getString("config.dnsbl_result");
		if (!result.equalsIgnoreCase("none")) {
			final String pname = event.getPlayer().getName();
			getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
				@Override
				public void run() {
					dnsblLookup(pname, result, config);
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void lockedIpCheck(PlayerJoinEvent event) throws IOException {
		Player player = event.getPlayer();
		String pname = player.getName();
		String iptocheck = DynamicBanCache.getIp(pname);
		String lockedplayer = DynamicBanCache.getIpLock(iptocheck);
		if (lockedplayer != null) {
			if (!lockedplayer.equals(pname)) {
				FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
				String lockedipmsg = config.getString("messages.locked_ip_message").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				player.kickPlayer(lockedipmsg);
				event.setJoinMessage(null);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void sameIpCheck(PlayerJoinEvent event) throws IOException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		if (config.getBoolean("config.broadcast_on_same_ip") == true) {
			String player = event.getPlayer().getName();
			String iptocheck = DynamicBanCache.getIp(player);
			if (iptocheck != null) {
				if (DynamicBanCache.getLoggedIp(iptocheck) == null) {
					DynamicBanCache.addLoggedIp(iptocheck, player);
				} else {
					String olderPlayer = DynamicBanCache.getLoggedIp(iptocheck);
					if (!(player.equalsIgnoreCase(olderPlayer))) {
						for (Player broadcastto: Bukkit.getServer().getOnlinePlayers()) {
							if (permission.has(broadcastto, "dynamicban.check") || broadcastto.isOp()) {
								String sameIPMsg = config.getString("other_messages.same_ip_message").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2").replace("{PLAYER}", player).replace("{IP}", iptocheck.replace("/", ".")).replace("{OLDERPLAYER}", olderPlayer);
								broadcastto.sendMessage(tag + sameIPMsg);
							}
						}  
						Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, tag + player + " logged in with the same IP (" + iptocheck.replace("/", ".") + ") as " + olderPlayer);
					}
				}
			}
		}
	}
	
	public void sendWarnings(Player p) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy '@' HH:mm:ss");
		File playerDataFile = new File("plugins/DynamicBan/playerdata/" + p.getName().toLowerCase() + "/", "player.dat");
		if (playerDataFile.exists()) {
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			int currentWarns = 0;
			List<String> warns = new ArrayList<String>();
			for (String s : playerData.getConfigurationSection("warns").getKeys(false)) {
				Date date = null;
				try {
					date = sdf.parse(s);
					Calendar warnendtime = Calendar.getInstance();
					if (date != null) {
						warnendtime.setTime(date);
						warnendtime.add(Calendar.HOUR, config.getInt("config.warns_timeout"));
					}
					if (!warnendtime.before(Calendar.getInstance()) || config.getInt("config.warns_timeout") == 0) {
						currentWarns++;
						warns.add(s + " - " + playerData.getString("warns." + s));
					}
				} catch (ParseException e) {
					getLogger().severe("Date " + s + " could not be parsed.");
				}
			}
			if (currentWarns != 0) {
				String message = config.getString("other_messages.warnings_message").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				if (!message.matches("(\u00A7([0-9a-fk-or]))*\\{WARNS\\}.*$")) {
					p.sendMessage(tag + message.split("(\u00A7([0-9a-fk-or])){0,2}\\{WARNS\\}")[0].replace("{AMOUNT}", String.valueOf(currentWarns)));
				}
				for (String s : warns) {
					p.sendMessage(warnFormat + s);
				}
				if (!message.endsWith("{WARNS}")) {
					p.sendMessage(message.split("\\{WARNS\\}")[1].replace("{AMOUNT}", String.valueOf(currentWarns)));
				}
			}
		}
	}
	
	public void dnsblLookup(String pname, String result, FileConfiguration config) {
		String ip = DynamicBanCache.getIp(pname.toLowerCase()).replace("/", ".");
		if (dnsbl.isBlacklisted(ip)) {
			if (result.equals("kick") || result.equals("ban") || result.equals("ipban")) {
				getServer().dispatchCommand(getServer().getConsoleSender(), result + " " + pname + " " + config.getString("other_messages.dnsbl_reason"));
			} else {
				if (result.equals("notify")) {
					for (Player broadcastto: Bukkit.getServer().getOnlinePlayers()) {
						if (permission.has(broadcastto, "dynamicban.check") || broadcastto.isOp()) {
							broadcastto.sendMessage(tag + config.getString("other_messages.dnsbl_ip_message").replace("{PLAYER}", pname).replace("{IP}", ip).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
						}
					}
					Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, tag + pname + "'s ip (" + ip + ") is blacklisted.");
				}
			}
		}
	}

	/**
	 * Added by Koolsource
	 */
	public String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();

		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}

		builder.deleteCharAt(builder.length() - seperator.length());
		return builder.toString();
	}
	
	public String findPlayerName(String input, CommandSender cs) {
		List<String> results = new ArrayList<String>();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			String pname = p.getName();
			if (pname.toLowerCase().startsWith(input)) {
				if (!results.contains(pname)) {
					results.add(pname);
				}
			}
		}
		if (results.size() == 1) {
			return results.get(0);
		} else {
			if (results.size() == 0) {
				cs.sendMessage(tag + ChatColor.AQUA + "There is no player using that name.");
			} else {
				if (results.size() > 1) {
					String resultString = results.toString();
					cs.sendMessage(tag + ChatColor.AQUA + "There are multiple players using that name: " + resultString.substring(1, resultString.length() - 1));
				}
			}
		}
		return null;
	}
}
