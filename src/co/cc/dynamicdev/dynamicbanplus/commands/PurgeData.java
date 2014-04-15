package co.cc.dynamicdev.dynamicbanplus.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import co.cc.dynamicdev.dynamicbanplus.DynamicBan;
import co.cc.dynamicdev.dynamicbanplus.DynamicBanCache;

//Author: xDrapor
//The DynamicDev Team 
//DynamicBan - Comprehensive IP banning.
public class PurgeData implements CommandExecutor {

	private File playerDataFile = null;
	private FileConfiguration playerData = null;
	private File playerDataDir = null;
	public DynamicBan plugin;

	public PurgeData(DynamicBan plugin) {
		this.plugin = plugin;
	}
	String[] args;

	public void reloadPlayerData() {
		if (playerDataFile == null) {
			playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
		}
		YamlConfiguration.loadConfiguration(playerDataFile);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynpurge")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.purge") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length < 2) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name] [Type]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Type values: data, kicks, warns");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Deletes the value of a players data, or all data.");
				return true;
			}
			if (!(args[1].contains("data") || args[1].contains("kicks") || args[1].contains("warns"))) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Invalid type, use /" + alias + " for more information.");
				return true;
			}
			if (args[0].endsWith("*")) {
				args[0] = plugin.findPlayerName(args[0].substring(0, args[0].length() - 1).toLowerCase(), cs);
				if (args[0] == null) {
					return true;
				}
			}
			playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
			playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			String playerip = playerData.getString("IP-Address");
			if (playerDataFile.exists()  && DynamicBanCache.getPlayerBan(args[0].toLowerCase()) != null || DynamicBanCache.getTempBan(args[0].toLowerCase()) != null) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "To prevent errors, please unban this player before you purge!");
				return true;  
			} else {
				if (playerip != null) {
					if (DynamicBanCache.getIpBan(playerip.replace(".", "/")) != null || DynamicBanCache.getTempBan(playerip.replace(".", "/")) != null) {
						cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "To prevent errors, please unban this player before you purge!");
						return true; 
					}
				}
			}
			if (args[1].equalsIgnoreCase("data")) {
				playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
				playerDataDir = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase());
				if (playerDataFile.exists()) {
					playerDataFile.delete();
					playerDataDir.delete();
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Purged player " + args[0] + "'s data!");
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
				}
			} else {
				if (args[1].equalsIgnoreCase("kicks")) {
					playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
					playerData = YamlConfiguration.loadConfiguration(playerDataFile);
					YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
					if (playerDataFile.exists()) {
						cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Purged player " + args[0] + "'s kicks!");
						playerData.set("kickedNumber", "0");
						try {
							playerData.save(playerDataFile);
							reloadPlayerData();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
					}
				} else {
					if (args[1].equalsIgnoreCase("warns")) {
						playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
						YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
						if (playerDataFile.exists()) {
							cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Purged player " + args[0] + "'s warns!");
							playerData.options().copyDefaults(true);
							for (String s : playerData.getConfigurationSection("warns").getKeys(false)) {
								playerData.set("warns." + s, null);
							}
							try {
								playerData.save(playerDataFile);
								reloadPlayerData();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
						}
					}
				}
			}
		}
		return true;
	}
}