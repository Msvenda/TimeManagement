# BW Time Management

BW Time Management is a Spigot plugin that allows for customization of the day night cycle duration, as well as adding a customizable calendar system.

## 1\. Plugin commands

**doTimeHere:**
- Enables or disables daytime cycle in world
- Required permission: timeManager.manager 
- Usage: "/doTimeHere [boolean]"
    
**setDate:**   
- Set the day, month and year. Optionally set hours and minutes*  
- Required permission: timeManager.manager
- Usage: "/setDate <day> <month> <year> [<hour> <minute>]"
    
**checkTime:**
- Check the current date  
- Required permission: timeManager.manager 
- Usage: "/checkTime"</list>

## 2\. Plugin configuration

The plugin will automatically generate a configuration file if it does not find a valid one located at `...\plugins\BWTimeManagement\config.yml`. The structure of the config file is as follows:

    starting time: #auto generated    
    update interval (s): #update interval in seconds, higher values may cause jerky sun/moon movement. Defaults to 10    
    minutes in day: #number of minutes required for a full day-night cycle. Defaults to 240    
    game days in month: #number of days in one in-game month, defaults to 28    
    months: #list of months in one in-game year    
     - Month 1    
     - Month 2    
    worlds: [] #list of uid's of excluded worlds, populated through doTimeHere command.    


## 3\. Plugin API

The plugin has an API which can be used to either augment it or use it's data for your own plugin. You can access the ServerTime object responsible for tracking the custom game time through the main plugin class by calling `getServerTime()`. the ServerTime object gives you access to the following methods:

- `getStartingTime()`: Gets the starting time in ms
- `setStartingTime(long startingTime)`: Manually sets the starting time
- `dayTimeInTicks()`: Gets the current time in minecraft ticks
- `setDate(int d, int m, int y)`: Sets the current day, month and year
- `setDate(int d, int m, int y, int h, int min)`: Sets the current day, month, year, hour and minute
- `getMoonPhase()`: Gets the current in-game moon phase
- `getYear()`: Gets the current year
- `getMonth()`: Gets the current month
- `getMonthByName()`: Gets the current month by name as defined in config*   `getDay()`: Gets the current day
- `getHour()`: Gets the current hour
- `getMinute()`: Gets the current minute
- `toString()`: Gets the current date and moon phase as a string
- `getFullTime()`: Gets the current date, time and moon phase as a string

For an example of how to use the API you can check out [BWDeathChest](https://github.com/Msvenda/DeathChest/tree/master/BWDeathChest)
