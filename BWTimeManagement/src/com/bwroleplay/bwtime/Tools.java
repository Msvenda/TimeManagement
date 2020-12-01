package com.bwroleplay.bwtime;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Tools {
	protected static void logInfo(String log) {
		Bukkit.getLogger().log(Level.INFO, "[BWTimeManagement] " + log);
	}
}
