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
public class UnbanPlayer implements CommandExecutor {

	private DynamicBan plugin;
	public UnbanPlayer(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynunban")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.unban.player") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED +"Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length == 0 || args.length > 1) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Unbans a player's name from the system.");
				return true;
			}
			File playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			String pname = args[0].toLowerCase();
			boolean wasBanned = false;
			if (plugin.getServer().getOfflinePlayer(pname).isBanned()) {
				wasBanned = true;
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is banned by Bukkit, unbanning.");
				Bukkit.getOfflinePlayer(args[0]).setBanned(false);
				plugin.getServer().getOfflinePlayer(pname).setBanned(false);
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not banned by Bukkit.");
			}
			if (playerDataFile.exists()) {
				if (DynamicBanCache.getPlayerBan(pname) != null) {
					wasBanned = true;
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " has been banned by DynamicBan, unbanning.");
					DynamicBanCache.removePlayerBan(pname);
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not banned by DynamicBan.");
				}
				if (DynamicBanCache.getTempBan(pname) != null) {
					wasBanned = true;
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " has been temp-banned by DynamicBan, unbanning.");
					DynamicBanCache.removeTempBan(pname);
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " is not temp-banned by DynamicBan!");
				}
				if (wasBanned) {
					if (plugin.getConfig().getBoolean("config.broadcast_on_unban")) {
						String broadcastMessage = plugin.getConfig().getString("broadcast_messages.unban_message").replace("{PLAYER}", playerData.getString("DisplayName")).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
						plugin.getServer().broadcastMessage(broadcastMessage);
					}
				}
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
			}
		}
		return true;
	}
}