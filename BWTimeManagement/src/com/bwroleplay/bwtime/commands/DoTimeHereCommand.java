package com.bwroleplay.bwtime.commands;

import com.bwroleplay.bwtime.util.TimeDataLayer;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
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
        TimeDataLayer dataLayer = TimeDataLayer.getDataLayer();
        if(doWorld && !dataLayer.getWorlds().contains(p.getWorld().getUID())) {
            dataLayer.getWorlds().add(p.getWorld().getUID());
            p.getWorld().setTime(dataLayer.getServerTime().dayTimeInTicks());
            p.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
        else {
            dataLayer.getWorlds().remove(p.getWorld().getUID());
            p.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
        sender.sendMessage(ChatColor.GRAY + "[Server -> ME]:  Time tracking in world " + p.getWorld().getUID().toString() + ": " + doWorld);

        return true;
    }
}
