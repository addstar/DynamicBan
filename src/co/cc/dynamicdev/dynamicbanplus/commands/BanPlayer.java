package co.cc.dynamicdev.dynamicbanplus.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
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

public class BanPlayer implements CommandExecutor {

	private static DynamicBan plugin;
	public static Permission permission = null;
	public BanPlayer(DynamicBan plugin) {
		BanPlayer.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynban")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.ban.player") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length == 0) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name] (Reason)");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Ban the player specified, with an optional reason");
				return true;
			}
			if (args[0].endsWith("*")) {
				args[0] = plugin.findPlayerName(args[0].substring(0, args[0].length() - 1).toLowerCase(), cs);
				if (args[0] == null) {
					return true;
				}
			}
			if (DynamicBanCache.isImmune(args[0].toLowerCase()) && plugin.getConfig().getBoolean("config.op_immune_bypass") == true && cs.isOp()){
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Since you are OP, you bypassed " + args[0] + "'s immunity.");
			} else {
				if (DynamicBanCache.isImmune(args[0].toLowerCase())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, that player is immune to your command!");
					return true;
				}
			}
			Player playertoban = plugin.getServer().getPlayerExact(args[0]);
			String banReason;
			String broadcastReason;
			String afterBanReason;
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy '@' HH:mma");
			String date = sdf.format(today);
			if (args.length == 1) {
				banReason = plugin.getConfig().getString("messages.ban_message").replace("{REASON}", plugin.getConfig().getString("other_messages.default_reason")).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
			} else {
				banReason = plugin.getConfig().getString("messages.ban_message").replace("{REASON}", plugin.combineSplit(1, args, " ")).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
			}

			if (args.length == 1) {
				broadcastReason = plugin.getConfig().getString("other_messages.default_reason");
				afterBanReason = "None";
			} else {
				broadcastReason = plugin.combineSplit(1, args, " ");
				afterBanReason = plugin.combineSplit(1, args, " ");
			}
			
			DynamicBanCache.addPlayerBan(args[0].toLowerCase(), afterBanReason, cs.getName(), date);
			Bukkit.getOfflinePlayer(args[0]).setBanned(true);
			String pname;
			if (playertoban != null) {
				playertoban.kickPlayer(banReason);
				pname = playertoban.getName();
			} else {
				pname = args[0];
			}
			if (plugin.getConfig().getBoolean("config.broadcast_on_ban")) {
				String broadcastMessage = plugin.getConfig().getString("broadcast_messages.ban_message").replace("{PLAYER}", pname).replace("{REASON}", broadcastReason).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				plugin.getServer().broadcastMessage(broadcastMessage);
			}
		}
		return true;
	}
}