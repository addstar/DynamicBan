package co.cc.dynamicdev.dynamicbanplus.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.milkbowl.vault.permission.Permission;

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
public class RangeBanIP implements CommandExecutor {

	private DynamicBan plugin;
	private File playerDataFile = null;
	public static Permission permission;

	public RangeBanIP(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynrangeban")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.ban.range")|| cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length < 2) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name] level:[1/2/3] (Reason)");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Range-Ban the player specified, with an optional reason");
				return true;
			}
			if (!(args[1].contains("level:1") || args[1].contains("level:2") || args[1].contains("level:3"))) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Invalid level, use /" + alias + " for more information.");
				return true;
			}
			if (args[0].endsWith("*")) {
				args[0] = plugin.findPlayerName(args[0].substring(0, args[0].length() - 1).toLowerCase(), cs);
				if (args[0] == null) {
					return true;
				}
			}
			if (DynamicBanCache.isImmune(args[0].toLowerCase()) && plugin.getConfig().getBoolean("config.op_immune_bypass") == true && cs.isOp()) {
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Since you are OP, you bypassed " + args[0] + "'s immunity.");
			} else {
				if (DynamicBanCache.isImmune(args[0].toLowerCase())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, that player is immune to your command!");
					return true;
				}
			}
			playerDataFile = new File("plugins/DynamicBan/playerdata/" + args[0].toLowerCase() + "/", "player.dat");
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
			if (playerDataFile.exists()) {
				String iptoban = playerData.getString("IP-Address").replace(".", "/");
				Player playertoban = plugin.getServer().getPlayerExact(args[0]);
				String banReason;
				String broadcastReason;
				String afterBanReason;
				Date today = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy '@' HH:mma");
				String date = sdf.format(today);
				if (args.length < 3) {
					afterBanReason = "None";
				} else {
					afterBanReason = plugin.combineSplit(2, args, " ");
				}
				if (args.length < 3) {
					broadcastReason = plugin.getConfig().getString("other_messages.default_reason");
				} else {
					broadcastReason = afterBanReason;
				}
				if (args.length < 3) {
					banReason = plugin.getConfig().getString("messages.rangeban_message").replace("{REASON}", plugin.getConfig().getString("other_messages.default_reason")).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				} else {
					banReason = plugin.getConfig().getString("messages.rangeban_message").replace("{REASON}", afterBanReason).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
				}
				String[] RBIP = iptoban.split("/");
				if(args[1].contains("level:1")){
					DynamicBanCache.addRangeBan(RBIP[0] + "/" + RBIP[1] + "/" + RBIP[2] + "/" + "*", afterBanReason, cs.getName(), date, iptoban);
				}
				if(args[1].contains("level:2")){
					DynamicBanCache.addRangeBan(RBIP[0] + "/" + RBIP[1] + "/" + "*" + "/" + "*", afterBanReason, cs.getName(), date, iptoban);
				}
				if(args[1].contains("level:3")){
					DynamicBanCache.addRangeBan(RBIP[0] + "/" + "*" + "/" + "*" + "/" + "*", afterBanReason, cs.getName(), date, iptoban);
				}
				String pname;
				if (playertoban != null) {
					playertoban.kickPlayer(banReason);
					pname = playertoban.getName();
				} else {
					pname = args[0];
				}
				if (plugin.getConfig().getBoolean("config.broadcast_on_rangeban") != false) {
					String broadcastMessage = plugin.getConfig().getString("broadcast_messages.rangeban_message").replace("{PLAYER}", pname).replace("{SENDER}", cs.getName()).replace("{REASON}", broadcastReason).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
					plugin.getServer().broadcastMessage(broadcastMessage);
					return true;
				}
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + args[0] + " has no data stored!");
			}
		}
		return true;
	}
}