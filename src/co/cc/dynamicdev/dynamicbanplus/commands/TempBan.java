package co.cc.dynamicdev.dynamicbanplus.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
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
public class TempBan implements CommandExecutor {

	private DynamicBan plugin;
	boolean valid = true;
	public TempBan(DynamicBan plugin) {
		this.plugin = plugin;
	}

	public long parseTimeSpec(String time, String unit) {
		long sec;
		try {
			sec = Integer.parseInt(time) * 60;
			valid = true;
		} catch (NumberFormatException ex) {
			valid = false;
			return 0;
		}
		if (unit.endsWith("h")) {
			sec *= 60;
		} else if (unit.endsWith("d")) {
			sec *= (60 * 24);
		} else if (unit.endsWith("w")) {
			sec *= (7 * 60 * 24);
		} else if (unit.endsWith("t")) {
			sec *= (30 * 60 * 24);
		} else if (unit.endsWith("m")) {
			sec *= 1;
		} else if (unit.endsWith("s")) {
			sec /= 60;
		}
		return sec;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dyntempban")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.tempban.player") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length < 2) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [Name] [Amount][Unit]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Unit values: s, m, h, d, w, mt");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Split multiple amounts and units with :");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Temporarily ban the specified player with the specified time.");
				return true;
			}
			if (!(args[1].contains("s") || args[1].contains("m") || args[1].contains("h") || args[1].contains("d") || args[1].contains("w") || args[1].contains("t"))) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Invalid unit, use /" + alias + " for more information.");
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
			String banReason;
			String broadcastReason;
			if (args.length > 2) {
				banReason = plugin.combineSplit(2, args, " ");
				if (banReason.contains("::")) {
					cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Please don't use \"::\" in the reason.");
					return true;
				}
				broadcastReason = banReason;
			} else {
				banReason = "None";
				broadcastReason = plugin.getConfig().getString("other_messages.default_reason");
			}
			Player playertoban = plugin.getServer().getPlayerExact(args[0]);
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy '@' HH:mma");
			String date = sdf.format(today);
			String[] Unit= args[1].split(":");
			String pname = args[0].toLowerCase();
			if(Unit.length == 1){
				String value1 = Unit[0].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				long tempTime = parseTimeSpec(value1, Unit[0]);
				long tempTimeFinal;
				tempTimeFinal = System.currentTimeMillis() / 1000 + tempTime;
				plugin.getServer().getOfflinePlayer(args[0].toLowerCase()).setBanned(true);
				DynamicBanCache.addTempBan(pname, tempTimeFinal + "::" + banReason, cs.getName(), date);
			}
			if(Unit.length == 2){
				String value1 = Unit[0].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value2 = Unit[1].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				long tempTime = parseTimeSpec(value1, Unit[0]);
				long tempTime2 = parseTimeSpec(value2, Unit[1]);
				long tempTimeFinal;
				tempTimeFinal = (System.currentTimeMillis() / 1000) + tempTime + tempTime2;
				plugin.getServer().getOfflinePlayer(args[0].toLowerCase()).setBanned(true);
				DynamicBanCache.addTempBan(pname, tempTimeFinal + "::" + banReason, cs.getName(), date);
			}
			if(Unit.length == 3){
				String value1 = Unit[0].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value2 = Unit[1].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value3 = Unit[2].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				Unit[3].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				long tempTime = parseTimeSpec(value1, Unit[0]);
				long tempTime2 = parseTimeSpec(value2, Unit[1]);
				long tempTime3 = parseTimeSpec(value3, Unit[2]);;
				long tempTimeFinal;
				tempTimeFinal = System.currentTimeMillis() / 1000 + tempTime + tempTime2 + tempTime3;
				plugin.getServer().getOfflinePlayer(args[0].toLowerCase()).setBanned(true);
				DynamicBanCache.addTempBan(pname, tempTimeFinal + "::" + banReason, cs.getName(), date);
			}
			if(Unit.length == 4){
				String value1 = Unit[0].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value2 = Unit[1].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value3 = Unit[2].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value4 = Unit[3].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				long tempTime = parseTimeSpec(value1, Unit[0]);
				long tempTime2 = parseTimeSpec(value2, Unit[1]);
				long tempTime3 = parseTimeSpec(value3, Unit[2]);
				long tempTime4 = parseTimeSpec(value4, Unit[3]);
				long tempTimeFinal;
				tempTimeFinal = System.currentTimeMillis() / 1000 + tempTime +  + tempTime2 + tempTime3 + tempTime4;
				plugin.getServer().getOfflinePlayer(args[0].toLowerCase()).setBanned(true);
				DynamicBanCache.addTempBan(pname, tempTimeFinal + "::" + banReason, cs.getName(), date);
			}
			if(Unit.length == 5){
				String value1 = Unit[0].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value2 = Unit[1].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value3 = Unit[2].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value4 = Unit[3].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value5 = Unit[4].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				long tempTime = parseTimeSpec(value1, Unit[0]);
				long tempTime2 = parseTimeSpec(value2, Unit[1]);
				long tempTime3 = parseTimeSpec(value3, Unit[2]);
				long tempTime4 = parseTimeSpec(value4, Unit[3]);
				long tempTime5= parseTimeSpec(value5, Unit[4]);
				long tempTimeFinal;
				tempTimeFinal = System.currentTimeMillis() / 1000 + tempTime + tempTime2 + tempTime3 + tempTime4 + tempTime5;
				plugin.getServer().getOfflinePlayer(args[0].toLowerCase()).setBanned(true);
				DynamicBanCache.addTempBan(pname, tempTimeFinal + "::" + banReason, cs.getName(), date);
			}
			if(Unit.length == 6){
				String value1 = Unit[0].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value2 = Unit[1].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value3 = Unit[2].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value4 = Unit[3].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value5 = Unit[4].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				String value6 = Unit[5].replace("m", "").replace("h", "").replace("d", "").replace("w", "").replace("t", "").replace("s", "");
				long tempTime = parseTimeSpec(value1, Unit[0]);
				long tempTime2 = parseTimeSpec(value2, Unit[1]);
				long tempTime3 = parseTimeSpec(value3, Unit[2]);
				long tempTime4 = parseTimeSpec(value4, Unit[3]);
				long tempTime5= parseTimeSpec(value5, Unit[4]);
				long tempTime6 = parseTimeSpec(value6, Unit[5]);
				long tempTimeFinal;
				tempTimeFinal = System.currentTimeMillis() / 1000 + tempTime + tempTime2 + tempTime3 + tempTime4 + tempTime5 + tempTime6;
				plugin.getServer().getOfflinePlayer(args[0].toLowerCase()).setBanned(true);
				DynamicBanCache.addTempBan(pname, tempTimeFinal + "::" + banReason, cs.getName(), date);
			}
			
			String timeBanned = args[1].replace(":", " ");
			String banMessage = plugin.getConfig().getString("messages.tempban_message").replace("{TIME}", timeBanned).replace("{REASON}", broadcastReason).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
			if (playertoban != null) {
				playertoban.kickPlayer(banMessage);
				pname = playertoban.getName();
			} else {
				pname = args[0];
			}
			if (valid) {
				if (plugin.getConfig().getBoolean("config.broadcast_on_tempban") != false) {
					String broadcastMessage = plugin.getConfig().getString("broadcast_messages.tempban_message").replace("{PLAYER}", pname).replace("{TIME}", timeBanned).replace("{REASON}", broadcastReason).replace("{SENDER}", cs.getName()).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");
					plugin.getServer().broadcastMessage(broadcastMessage);
				}
				return true;
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Invalid time format, use /" + alias + " for more information.");
			}
		}
		return true;
	}
}