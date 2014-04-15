package co.cc.dynamicdev.dynamicbanplus.commands;

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
public class WhitelistAddRemove implements CommandExecutor {
	private DynamicBan plugin;

	public WhitelistAddRemove(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (args.length < 2) {
			if (DynamicBan.permission.has(cs, "dynamicban.whitelist.add") || DynamicBan.permission.has(cs, "dynamicban.whitelist.remove") || cs.isOp()) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [add/remove] [Name/IP/IP-Range]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Add/remove the specified player from the whitelist.");
				return true;
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
				return true;
			}
		}
		if (!(args[0].contains("add") || args[0].contains("remove"))) {
			cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Invalid arguments, use /" + alias + " for more information.");
			return true;
		}
		if (args[1].endsWith("*")) {
			args[1] = plugin.findPlayerName(args[0].substring(0, args[1].length() - 1).toLowerCase(), cs);
			if (args[1] == null) {
				return true;
			}
		}
		if (args[0].equalsIgnoreCase("add") && DynamicBanCache.isWhitelisted(args[1].toLowerCase())) {
			cs.sendMessage(DynamicBan.tag + ChatColor.RED + "That player is already whitelisted!");
			return true;
		}
		if (args[0].equalsIgnoreCase("remove") && !DynamicBanCache.isWhitelisted(args[1].toLowerCase())) {
			cs.sendMessage(DynamicBan.tag + ChatColor.RED + "That player is not whitelisted!");
			return true;
		}

		if (cs instanceof Player) {
			if (DynamicBan.permission.has(cs, "dynamicban.whitelist.add") || cs.isOp()) {
				if (args[0].equalsIgnoreCase("add")) {
					DynamicBanCache.addWhitelisted(args[1].toLowerCase().replace(".", "/"), cs.getName().toLowerCase());
					cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been added to the whitelist!");
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					if(DynamicBan.permission.has(cs, "dynamicban.whitelist.remove")){
						DynamicBanCache.removeWhitelisted(args[1].toLowerCase().replace(".", "/"));
						cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been removed from the whitelist!");
						return true;
					}else{
						cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					}
				}
			} else {
				cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
			}
		} else {
			if (args[0].equalsIgnoreCase("add")) {
				DynamicBanCache.addWhitelisted(args[1].toLowerCase().replace(".", "/"), cs.getName().toLowerCase());
				cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been added to the whitelist!");
				return true;
			} else if (args[0].equalsIgnoreCase("remove")) {
				DynamicBanCache.removeWhitelisted(args[1].toLowerCase().replace(".", "/"));
				cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been removed from the whitelist!");
				return true;
			}
		}
		return true;
	}
}