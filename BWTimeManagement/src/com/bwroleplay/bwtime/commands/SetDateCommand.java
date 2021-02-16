package com.bwroleplay.bwtime.commands;

import com.bwroleplay.bwtime.util.DataLayer;
import com.bwroleplay.bwtime.util.LoggingTools;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SetDateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(! (args.length == 3 || args.length == 5)) {
            return false;
        }
        if(args.length == 3) {
            int day = Integer.parseInt(args[0])-1;
            int month = Integer.parseInt(args[1])-1;
            int year = Integer.parseInt(args[2])-1;

            DataLayer.getDataLayer().getServerTime().setDate(day, month, year);
        }
        else {
            int day = Integer.parseInt(args[0])-1;
            int month = Integer.parseInt(args[1])-1;
            int year = Integer.parseInt(args[2])-1;
            int hour = Integer.parseInt(args[3]);
            int minute = Integer.parseInt(args[4]);

            DataLayer.getDataLayer().getServerTime().setDate(day, month, year, hour, minute);
        }
        long ticks = DataLayer.getDataLayer().getServerTime().dayTimeInTicks();
        for(UUID uid : DataLayer.getDataLayer().getWorlds()) {
            try{
                Bukkit.getWorld(uid).setTime(ticks);
            }catch(NullPointerException e){
                LoggingTools.logWarning(String.format("World with UUID '%s' not found, please remove the UUID entry from the config file!", uid));
            }
        }

        sender.sendMessage(ChatColor.GRAY + "[Server -> ME]: Time set to:\n" + DataLayer.getDataLayer().getServerTime().getFullTime());
        return true;
    }
}
