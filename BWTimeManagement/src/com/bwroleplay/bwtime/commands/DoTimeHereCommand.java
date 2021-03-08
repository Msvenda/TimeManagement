package com.bwroleplay.bwtime.commands;

import com.bwroleplay.bwtime.util.TimeDataLayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoTimeHereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
        }
        Player p = (Player) sender;
        if(args.length > 1) {
            return false;
        }
        boolean doWorld = true;
        if(args.length == 1) {
            doWorld = Boolean.parseBoolean(args[0]);
        }
        TimeDataLayer timeDataLayer = TimeDataLayer.getDataLayer();
        if(doWorld && !timeDataLayer.getWorlds().contains(p.getWorld().getUID())) {
            timeDataLayer.getWorlds().add(p.getWorld().getUID());
            p.getWorld().setTime(timeDataLayer.getServerTime().dayTimeInTicks());
            p.getWorld().setGameRuleValue("doDaylightCycle", "false");
        }
        else {
            timeDataLayer.getWorlds().remove(p.getWorld().getUID());
            p.getWorld().setGameRuleValue("doDaylightCycle", "true");
        }
        sender.sendMessage(ChatColor.GRAY + "[Server -> ME]:  Time tracking in world " + p.getWorld().getUID().toString() + ": " + doWorld);

        return true;
    }
}
