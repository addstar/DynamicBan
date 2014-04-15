package co.cc.dynamicdev.dynamicbanplus.commands;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.cc.dynamicdev.dynamicbanplus.DynamicBan;
import co.cc.dynamicdev.dynamicbanplus.DynamicBanCache;

//Author: xDrapor
//The DynamicDev Team 
//DynamicBan - Comprehensive IP banning.
public class Unmute implements CommandExecutor {

	private static DynamicBan plugin;
	public static Permission permission = null;

	public Unmute(DynamicBan plugin) {
		Unmute.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynunmute")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.unmute") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length < 1) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Unmute the player specified.");
				return true;
			}
			if (args[0].endsWith("*")) {
				args[0] = plugin.findPlayerName(args[0].substring(0, args[0].length() - 1).toLowerCase(), cs);
				if (args[0] == null) {
					return true;
				}
			}
			
			Player playertomute = plugin.getServer().getPlayerExact(args[0]);
			String playermuted = args[0].toLowerCase();
			if (DynamicBanCache.getMute(playermuted) != null) {
				DynamicBanCache.removeMute(playermuted);
				if (playertomute != null) {
					playertomute.sendMessage(DynamicBan.tag + plugin.getConfig().getString("messages.unmute_message").replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
					if (plugin.getConfig().getBoolean("config.broadcast_on_unmute")) {
						plugin.getServer().broadcastMessage(plugin.getConfig().getString("broadcast_messages.unmute_message").replace("{PLAYER}", playertomute.getName()).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
					}
				} else {
					if (plugin.getConfig().getBoolean("config.broadcast_on_unmute")) {
						plugin.getServer().broadcastMessage(plugin.getConfig().getString("broadcast_messages.unmute_message").replace("{PLAYER}", playermuted).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
					}
				}
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "That player is not muted");
			}
		}
		return true;
	}
}
