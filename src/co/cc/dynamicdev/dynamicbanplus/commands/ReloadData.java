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

public class ReloadData implements CommandExecutor {

	private DynamicBan plugin;
	public ReloadData(DynamicBan plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dynreload")) {
			if (cs instanceof Player) {
				if (!(DynamicBan.permission.has(cs, "dynamicban.reload") || cs.isOp())) {
					cs.sendMessage(DynamicBan.tag + ChatColor.RED + "Sorry, you do not have the permission to use that command!");
					return true;
				}
			}
			if (args.length > 0) {
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Usage: /" + cmd.getAliases().toString() + "");
				cs.sendMessage(DynamicBan.tag + ChatColor.AQUA + "Reloads the DynamicBan data.");
				return true;
			}
			plugin.reloadConfig();
			plugin.loadTag(plugin.getConfig());
			plugin.loadWarnFormat(plugin.getConfig());
			plugin.loadDnsblServices(plugin.getConfig());
			DynamicBanCache.reloadAll();
			cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + "Reload successful!");
			cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + "The following were reloaded:");
			cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + "DynamicBan Database");
			cs.sendMessage(DynamicBan.tag + ChatColor.GREEN + "Configuration");
		}
		return true;
	}
}