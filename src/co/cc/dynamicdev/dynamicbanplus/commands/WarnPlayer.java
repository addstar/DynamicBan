package co.cc.dynamicdev.dynamicbanplus.commands;

//Author: xDrapor
//The DynamicDev Team 
//DynamicBan - Comprehensive IP banning.
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import co.cc.dynamicdev.dynamicbanplus.DynamicBan;
import co.cc.dynamicdev.dynamicbanplus.DynamicBanCache;

@SuppressWarnings("unused")
public class WarnPlayer implements CommandExecutor {

	private DynamicBan plugin;
	private File playerDataFile = null;
	private FileConfiguration playerData = null;

	public WarnPlayer(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cs instanceof Player) {
			if (!(DynamicBan.permission.has(cs, "dynamicban.warn") || cs.isOp())) {
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
				return true;
			}
		}
		if (args.length == 0) {
			cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name] (Reason)");
			cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Warn the player specified, with an optional reason");
			return true;
		}
		if (args[0].endsWith("*")) {
			args[0] = plugin.findPlayerName(args[0].substring(0, args[0].length() - 1).toLowerCase(), cs);
			if (args[0] == null) {
				return true;
			}
		}
		if(DynamicBanCache.isImmune(args[0].toLowerCase()) && plugin.getConfig().getBoolean("config.op_immune_bypass") == true && cs.isOp()){
			cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Since you are OP, you bypassed " + args[0] + "'s immunity.");
		} else {
			if (DynamicBanCache.isImmune(args[0].toLowerCase())) {
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, that player is immune to your command!");
				return true;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy '@' HH:mm:ss");
		Calendar now = Calendar.getInstance();
		playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
		if (playerDataFile.exists()) {
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			playerData.options().copyDefaults(true);

			int warnNumber = playerData.getInt("warnedNumber");
			String warnReason;
			int currentWarns = 0;
			for (String s : playerData.getConfigurationSection("warns").getKeys(false)) {
				Date date = null;
				try {
					date = sdf.parse(s);
					Calendar warnendtime = Calendar.getInstance();
					if (date != null) {
						warnendtime.setTime(date);
						warnendtime.add(Calendar.HOUR, plugin.getConfig().getInt("config.warns_timeout"));
					}
					if (warnendtime.before(now) && plugin.getConfig().getInt("config.warns_timeout") != 0) {
						playerData.set("warns." + s, null);
					} else {
						currentWarns++;
					}
				} catch (ParseException e) {
					plugin.getLogger().severe("Date " + s + " could not be parsed.");
					playerData.set("warns." + s, null);
				}
			}
			if (args.length == 1) {
				warnReason = plugin.getConfig().getString("other_messages.default_reason");
			} else {
				warnReason = plugin.combineSplit(1, args, " ");
			}

			playerData.set("warns." + sdf.format(now.getTime()), cs.getName() + " - " + warnReason);
			currentWarns++;
			try {
				playerData.save(playerDataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Player playertowarn = plugin.getServer().getPlayerExact(args[0]);
			String pname;
			if (playertowarn != null) {
				String warnMsg = plugin.getConfig().getString("other_messages.warned_message").replace("{REASON}", warnReason).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				playertowarn.sendMessage(DynamicBan.tag + warnMsg);
				playertowarn.sendMessage(DynamicBan.tag + ChatColor.RED + "You have " + currentWarns + " warnings!");
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "You warned " + args[0] + " for " + warnReason);
				pname = playertowarn.getName();
			} else {
				pname = args[0];
			}

			if (plugin.getConfig().getBoolean("config.broadcast_on_warn") != false) {
				String broadcastMessage = plugin.getConfig().getString("broadcast_messages.warn_message").replace("{PLAYER}", pname).replace("{REASON}", warnReason).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				plugin.getServer().broadcastMessage(broadcastMessage);
			}

			for (String s : plugin.getConfig().getConfigurationSection("config.warn_results").getKeys(false)) {
				int warn = Integer.valueOf(s);
				if (currentWarns == warn) {
					String command = plugin.getConfig().getString("config.warn_results." + s).replace("{PLAYER}", args[0]).replace("{REASON}", warnReason);
					try {
						if (!Bukkit.getServer().dispatchCommand(cs, command)) {
							cs.sendMessage(DynamicBan.tag + ChatColor.RED + "The command " + command + " was not found.");
						}
					} catch (CommandException e) {
						cs.sendMessage(DynamicBan.tag + ChatColor.RED + "The command " + command + " could not be executed.");
					}
				}
			}
		} else {
			cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
		}
		return true;
	}
}