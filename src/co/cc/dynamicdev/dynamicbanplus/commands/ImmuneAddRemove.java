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
public class ImmuneAddRemove implements CommandExecutor {

	private DynamicBan plugin;
	
	public ImmuneAddRemove(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (args.length < 2) {
			if (DynamicBan.permission.has(cs, "dynamicban.immune.add") || DynamicBan.permission.has(cs, "dynamicban.immune.remove") || cs.isOp()) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + " [add/remove] [name]");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Add/remove the specified player from the immune list.");
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
			args[1] = plugin.findPlayerName(args[1].substring(0, args[1].length() - 1).toLowerCase(), cs);
			if (args[1] == null) {
				return true;
			}
		}
		if (args[0].equalsIgnoreCase("add") && DynamicBanCache.isImmune(args[1].toLowerCase())) {
			cs.sendMessage(DynamicBan.tag + ChatColor.RED + "That player is already immune!");
			return true;
		}
		if (args[0].equalsIgnoreCase("remove") && !DynamicBanCache.isImmune(args[1].toLowerCase())) {
			cs.sendMessage(DynamicBan.tag + ChatColor.RED + "That player is not immune!");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("dynimmune")) {
			if (cs instanceof Player) {
				if (DynamicBan.permission.has(cs, "dynamicban.immune.add") || cs.isOp()) {
					if (args[0].equalsIgnoreCase("add")) {
						DynamicBanCache.addImmunity(args[1].toLowerCase(), cs.getName().toLowerCase());
						cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been added to the list of immune players!");
						return true;
					} else if (args[0].equalsIgnoreCase("remove")) {
						if(DynamicBan.permission.has(cs, "dynamicban.immune.remove")){
							DynamicBanCache.removeImmunity(args[1].toLowerCase());
							cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been removed from the of immune players!");
							return true;
						} else {
							cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
						}
					}
				} else {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
				}
			} else {
				if (args[0].equalsIgnoreCase("add")) {
					DynamicBanCache.addImmunity(args[1].toLowerCase(), cs.getName().toLowerCase());
					cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been added to the list of immune players!");
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					DynamicBanCache.removeImmunity(args[1].toLowerCase());
					cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + args[1] + " has been removed from the list of immune players!");
					return true;
				}
			}
		}
		return true;
	}
}