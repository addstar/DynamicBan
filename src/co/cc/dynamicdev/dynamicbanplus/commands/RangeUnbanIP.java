package co.cc.dynamicdev.dynamicbanplus.commands;

import java.io.File;

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
public class RangeUnbanIP implements CommandExecutor {

	private File playerDataFile = null;
	private DynamicBan plugin;

	public RangeUnbanIP(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynunbanrange")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.unban.range") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length == 0) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Unban a rangebanned player from the system.");
				return true;
			}
			playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			if (playerDataFile.exists()) {
				String playerip = playerData.getString("IP-Address").replace(".", "/");
				String[] IP = playerip.split("/");
				if (DynamicBanCache.getRangeBan(IP[0]+ "/" + "*"+"/" + "*" +"/" + "*") != null || DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + "*" +"/" + "*") != null || DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*") != null) {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " has been rangebanned by DynamicBan, unbanning IP-Range.");
					if (DynamicBanCache.getRangeBan(IP[0]+ "/" + "*"+"/" + "*" +"/" + "*") != null) {
						DynamicBanCache.removeRangeBan(IP[0]+ "/" + "*"+"/" + "*" +"/" + "*", playerip);
					}
					if (DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + "*" +"/" + "*") != null) {
						DynamicBanCache.removeRangeBan(IP[0]+ "/" + IP[1] + "/" + "*" +"/" + "*", playerip);
					}
					if (DynamicBanCache.getRangeBan(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*") != null) {
						DynamicBanCache.removeRangeBan(IP[0]+ "/" + IP[1] + "/" + IP[2] +"/" + "*", playerip);
					}
					if (plugin.getConfig().getBoolean("config.broadcast_on_unban")) {
						String broadcastMessage = plugin.getConfig().getString("broadcast_messages.unban_message").replace("{PLAYER}", playerData.getString("DisplayName")).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
						plugin.getServer().broadcastMessage(broadcastMessage);
					}
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "No data exists for the specified player!");
				}
			}
		}
		return true;
	}
}
