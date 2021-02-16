package com.bwroleplay.bwtime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bwroleplay.bwtime.commands.CheckTimeCommand;
import com.bwroleplay.bwtime.commands.DoTimeHereCommand;
import com.bwroleplay.bwtime.commands.GetTimeInfoCommand;
import com.bwroleplay.bwtime.commands.SetDateCommand;
import com.bwroleplay.bwtime.util.DataLayer;
import com.bwroleplay.bwtime.util.LoggingTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeManagement extends JavaPlugin{
	
	private int lastHour = 0;
	
	private static final String propertiesFile = "plugins/BWTimeManagement/config.yml";
	double updateInterval;
	
	private BukkitRunnable updateTime;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginCommand("checkTime").setExecutor(new CheckTimeCommand());
		Bukkit.getPluginCommand("doTimeHere").setExecutor(new DoTimeHereCommand());
		Bukkit.getPluginCommand("getTimeInfo").setExecutor(new GetTimeInfoCommand());
		Bukkit.getPluginCommand("setDate").setExecutor(new SetDateCommand());

		//if the config file doesn't exist create and fill it with defaults.
		File propFile = new File(propertiesFile);
		if(!propFile.exists()) {
			createDefaults(propFile);
			
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		if(!config.isSet("starting time") || !config.isSet("update interval (s)") || 
				!config.isSet("minutes in day") || !config.isSet("game days in month") || 
				!config.isSet("months")) {
			createDefaults(propFile);
		}

		long startingTime = 0;
		int minutesInDay = 0;
		int daysInMonth = 0;
		//load config file
			try {
				startingTime = config.getLong("starting time");
				updateInterval = config.getDouble("update interval (s)");
				minutesInDay = config.getInt("minutes in day");
				daysInMonth = config.getInt("game days in month");
				DataLayer.getDataLayer().setMonths(config.getStringList("months"));
				DataLayer.getDataLayer().setWorlds(
						config.getStringList("worlds").stream().map(UUID::fromString).collect(Collectors.toList())
				);
			}catch(Exception e) {
				LoggingTools.logInfo("Error loading properties.");
			}
	
		//stop day/night cycle in worlds
		for(UUID uid : DataLayer.getDataLayer().getWorlds()) {
			Bukkit.getWorld(uid).setGameRuleValue("doDaylightCycle", "false");
		}
		
		//initialize ServerTime with settings
		DataLayer.getDataLayer().setServerTime(
				new ServerTime(startingTime, DataLayer.getDataLayer().getMonths().size(), daysInMonth, minutesInDay, DataLayer.getDataLayer().getMonths())
		);
		
		//initialize time management runnable
		InitializeRunnable();
		
		//run runnable based on interval value
		updateTime.runTaskTimer(this, 0, (long) (updateInterval*20L));
		
		LoggingTools.logInfo("Time manager activated.");
	}

	private void createDefaults(File propFile) {
		try {
			new File("plugins/BWTimeManagement").mkdirs();
			propFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		config.set("starting time", System.currentTimeMillis());
		config.set("update interval (s)", 10);
		config.set("minutes in day", 240);
		config.set("game days in month", 28);
		config.set("months", Arrays.asList("Granite", "Felsite", "Slate", "Hematite", "Malachite", "Galena",
				"Limestone", "Sandstone", "Timber", "Moonstone", "Opal", "Obsidian"));
		try {
			config.save(propFile);
			LoggingTools.logInfo("Properties file not found, default file generated in: \"" + propertiesFile + "\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LoggingTools.logInfo("Properties file not found, failed to generate default.");
		}
	}

	@Override
	public void onDisable() {
		//stoop runnable
		updateTime.cancel();
		
		//save properties file
		File propFile = new File(propertiesFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		config.set("starting time", DataLayer.getDataLayer().getServerTime().getStartingTime());
		config.set("worlds", DataLayer.getDataLayer().getWorlds().stream().map(UUID::toString).collect(Collectors.toList()));
		try {
			config.save(propFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LoggingTools.logInfo("Properties file not found. Settings not saved.");
		}
	}
			
	
	private void InitializeRunnable() {
		updateTime = new BukkitRunnable() {
			@Override
			public void run() {
				//get server time
				DataLayer.getDataLayer().getServerTime().updateTime();
				//set in-game time and disable daylight cycle if needed
				long ticks = DataLayer.getDataLayer().getServerTime().dayTimeInTicks();
				for(UUID uid : DataLayer.getDataLayer().getWorlds()) {
					World w = Bukkit.getWorld(uid);
					if(Boolean.parseBoolean(w.getGameRuleValue("doDaylightCycle"))) {
						w.setGameRuleValue("doDaylightCycle", "false");
					}
					w.setFullTime(ticks);
				}
				//broadcast time 4 times a day
				if(lastHour != DataLayer.getDataLayer().getServerTime().getHour()) {
					lastHour = DataLayer.getDataLayer().getServerTime().getHour();
					if(Arrays.asList(0, 6, 12, 18).contains(DataLayer.getDataLayer().getServerTime().getHour())) {
						Bukkit.broadcastMessage(ChatColor.GRAY + DataLayer.getDataLayer().getServerTime().toString());
					}
				}
			}
		};
		
	}
}
