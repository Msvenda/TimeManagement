package com.bwroleplay.bwtime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeManagement extends JavaPlugin{
	
	private int lastHour = 0;
	
	private ServerTime serverTime;
	
	private static final String propertiesFile = "plugins/BWTimeManagement/config.yml";
	private List<UUID> worlds;
	double updateInterval;
	
	private BukkitRunnable updateTime;
	
	@Override
	public void onEnable() {
		 long startingTime = 0;
		 int minutesInDay = 0;
		 int daysInMonth = 0;
		 List<String> months = null;
		
		
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
		
		//load config file
			try {
				startingTime = config.getLong("starting time");
				updateInterval = config.getDouble("update interval (s)");
				minutesInDay = config.getInt("minutes in day");
				daysInMonth = config.getInt("game days in month");
				months = config.getStringList("months");
				worlds = config.getStringList("worlds").stream().map(w -> UUID.fromString(w)).collect(Collectors.toList());
			}catch(Exception e) {
				Tools.logInfo("Error loading properties.");
			}
	
		//stop day/night cycle in worlds
		for(UUID uid : worlds) {
			Bukkit.getWorld(uid).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		}
		
		//initialize ServerTime with settings
		serverTime = new ServerTime(startingTime, months.size(), daysInMonth, minutesInDay, months);
		
		//initialize time management runnable
		InitializeRunnable();
		
		//run runnable based on interval value
		updateTime.runTaskTimer(this, 0, (long) (updateInterval*20L));
		
		Tools.logInfo("Time manager activated.");
	}
	
	/**
	  Getter for ServerTime object
	  @return ServerTime object
	  */
	public ServerTime getServerTime() {
		return serverTime;
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
			Tools.logInfo("Properties file not found, default file generated in: \"" + propertiesFile + "\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Tools.logInfo("Properties file not found, failed to generate default.");
			return;
		}
	}

	@Override
	public void onDisable() {
		//stoop runnable
		updateTime.cancel();
		
		//save properties file
		File propFile = new File(propertiesFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		config.set("starting time", serverTime.getStartingTime());
		config.set("worlds", worlds.stream().map(w -> w.toString()).collect(Collectors.toList()));
		try {
			config.save(propFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Tools.logInfo("Properties file not found. Settings not saved.");
			return;
		}
	}
			
	
	private void InitializeRunnable() {
		updateTime = new BukkitRunnable() {
			@Override
			public void run() {
				//get server time
				serverTime.updateTime();
				//set in-game time and disable daylight cycle if needed
				long ticks = serverTime.dayTimeInTicks();
				for(UUID uid : worlds) {
					World w = Bukkit.getWorld(uid);
					if(w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) {
						w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
					}
					w.setFullTime(ticks);
				}
				//broadcast time 4 times a day
				if(lastHour != serverTime.getHour()) {
					lastHour = serverTime.getHour();
					if(Arrays.asList(0, 6, 12, 18).contains(serverTime.getHour())) {
						Bukkit.broadcastMessage(ChatColor.GRAY + serverTime.toString());
					}
				}
			}
		};
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//takes up to one argument, set's if time should be managed in current world
		if(cmd.getName().equalsIgnoreCase("doTimeHere")) {
			if(args.length > 1) {
				return false;
			}
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players may use this command");
			}
			Player p = (Player) sender;
			boolean doWorld = true;
			if(args.length == 1) {
				doWorld = Boolean.parseBoolean(args[0]);
			}
			if(doWorld && !worlds.contains(p.getWorld().getUID())) {
				worlds.add(p.getWorld().getUID());
				p.getWorld().setTime(serverTime.dayTimeInTicks());
				p.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			}
			else {
				worlds.remove(p.getWorld().getUID());
				p.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
			}
			sender.sendMessage(ChatColor.GRAY + "[Server -> ME]:  Time tracking in world " + p.getWorld().getUID().toString() + ": " + doWorld);
			
			return true;
		}
		
		//takes 3 or 5 arguments, sets date and optionally time
		if(cmd.getName().equalsIgnoreCase("setDate")) {
			if(! (args.length == 3 || args.length == 5)) {
				return false;
			}
			if(args.length == 3) {
				int day = Integer.parseInt(args[0])-1;
				int month = Integer.parseInt(args[1])-1;
				int year = Integer.parseInt(args[2])-1;
				
				serverTime.setDate(day, month, year);
			}
			else {
				int day = Integer.parseInt(args[0])-1;
				int month = Integer.parseInt(args[1])-1;
				int year = Integer.parseInt(args[2])-1;
				int hour = Integer.parseInt(args[3]);
				int minute = Integer.parseInt(args[4]);
				
				serverTime.setDate(day, month, year, hour, minute);
			}
			long ticks = serverTime.dayTimeInTicks();
			for(UUID uid : worlds) {
				Bukkit.getWorld(uid).setTime(ticks);
			}
			
			sender.sendMessage(ChatColor.GRAY + "[Server -> ME]: Time set to:\n" + serverTime.getFullTime());
		}
		
		//returns current time
		if(cmd.getName().equalsIgnoreCase("checkTime")) {
			sender.sendMessage(ChatColor.GRAY + "[Server -> ME]: " +serverTime.getFullTime());
		}
		return true;
	}
}
