package com.bwroleplay.bwtime.commands;

import com.bwroleplay.bwtime.util.DataLayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetTimeInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GRAY + "[Server -> ME]: " + DataLayer.getDataLayer().getServerTime().getFullTime());
        return true;
    }
}