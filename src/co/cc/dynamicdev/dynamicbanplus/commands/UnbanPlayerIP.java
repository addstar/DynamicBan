package co.cc.dynamicdev.dynamicbanplus.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import co.cc.dynamicdev.dynamicbanplus.DynamicBan;
import co.cc.dynamicdev.dynamicbanplus.DynamicBanCache;

//Author: xDrapor
//The DynamicDev Team 
//DynamicBan - Comprehensive IP banning.
public class UnbanPlayerIP implements CommandExecutor {

	private DynamicBan plugin;
	private File playerDataFile = null;
	public UnbanPlayerIP(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynunbanip")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.unban.ip") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED +"Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length == 0) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Unban's a player's IP from the system.");
				return true;
			}
			playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			boolean wasBanned = false;
			if (playerDataFile.exists()) {
				String playerip = playerData.getString("IP-Address").replace(".", "/");
				if (Bukkit.getIPBans().contains(playerip.replace("/", "."))) {
					wasBanned = true;
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is ip-banned by Bukkit, unbanning.");
					plugin.getServer().unbanIP(playerip.replace("/", "."));
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not ip-banned by Bukkit.");
				}
				if (DynamicBanCache.getIpBan(playerip) != null) {
					wasBanned = true;
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " has been ip-banned by DynamicBan, unbanning.");
					DynamicBanCache.removeIpBan(playerip);
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not ip-banned by DynamicBan.");
				}
				if (DynamicBanCache.getTempBan(playerip) != null) {
					wasBanned = true;
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " has been temporarily ip-banned by DynamicBan, unbanning.");
					DynamicBanCache.removeTempBan(playerip);
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not temporarily ip-banned by DynamicBan.");
				}
				if (wasBanned) {
					if (plugin.getConfig().getBoolean("config.broadcast_on_unban")) {
						String broadcastMessage = plugin.getConfig().getString("broadcast_messages.unban_message").replace("{PLAYER}", playerData.getString("DisplayName")).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
						plugin.getServer().broadcastMessage(broadcastMessage);
					}
				}
			} else {
				if (Bukkit.getIPBans().contains(args[0])) {
					wasBanned = true;
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is ip-banned by Bukkit, unbanning.");
					plugin.getServer().unbanIP(args[0]);
					if (plugin.getConfig().getBoolean("config.broadcast_on_unban")) {
						String broadcastMessage = plugin.getConfig().getString("broadcast_messages.unban_message").replace("{PLAYER}", args[0]).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
						plugin.getServer().broadcastMessage(broadcastMessage);
					}
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not ip-banned by Bukkit.");
				}
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
			}
		}
		return true;
	}
}