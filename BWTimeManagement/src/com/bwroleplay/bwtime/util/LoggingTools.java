package com.bwroleplay.bwtime.util;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class LoggingTools {
	public static void logInfo(String log) {
		Bukkit.getLogger().log(Level.INFO, "[BWTimeManagement] " + log);
	}
	public static void logWarning(String log) {
		Bukkit.getLogger().log(Level.WARNING, "[BWTimeManagement] " + log);
	}
}
