<h1>BW Time Management</h1>
<p>BW Time Management is a Spigot plugin that allows for customization of the day night cycle duration, as well as adding a customizable calendar system.</p>

<h2>1. Plugin commands</h2>
<b>doTimeHere:</b>
<list>
    <li>Enables or disables daytime cycle in world</li>
    <li>Required permission: timeManager.manager</li>
    <li>Usage: "/doTimeHere [boolean]"</li>
</list>
<b>setDate:</b>
<list>
    <li>Set the day, month and year. Optionally set hours and minutes</li>
    <li>Required permission: timeManager.manager</li>
    <li>Usage: "/setDate &lt;day> &lt;month> &lt;year> [&lt;hour> &lt;minute>]"</li>
</list>
<b>checkTime:</b>
<list>
    <li>Check the current date</li>
    <li>Required permission: timeManager.manager</li>
    <li>Usage: "/checkTime"</li>
</list>

<h2>2. Plugin configuration</h2>

<p>The plugin will automatically generate a configuration file if it does not find a valid one located at <code>...\plugins\BWTimeManagement\config.yml</code>. The structure of the config file is as follows:</p>

<p>
<code>
starting time: #auto generated </br>
update interval (s): #update interval in seconds, higher values may cause jerky sun/moon movement. Defaults to 10</br>
minutes in day: #number of minutes required for a full day-night cycle. Defaults to 240</br>
game days in month: #number of days in one in-game month, defaults to 28</br>
months: #list of months in one in-game year</br>
- Month 1</br>
- Month 2</br>
worlds: [] #list of uid's of excluded worlds, populated through doTimeHere command.</br>
</code>
</p>

<h2>3. Plugin API</h2>

<p>The plugin has an API which can be used to either augment it or use it's data for your own plugin.
You can access the ServerTime object responsible for tracking the custom game time through the main plugin class by calling <code>getServerTime()</code>. the ServerTime object gives you access to the following methods:</p>
<list>
<li><code>getStartingTime()</code>: Gets the starting time in ms</li>
<li><code>setStartingTime(long startingTime)</code>: Manually sets the starting time</li>
<li><code>dayTimeInTicks()</code>: Gets the current time in minecraft ticks</li>
<li><code>setDate(int d, int m, int y)</code>: Sets the current day, month and year</li>
<li><code>setDate(int d, int m, int y, int h, int min)</code>: Sets the current day, month, year, hour and minute</li>
<li><code>getMoonPhase()</code>: Gets the current in-game moon phase</li>
<li><code>getYear()</code>: Gets the current year</li>
<li><code>getMonth()</code>: Gets the current month</li>
<li><code>getMonthByName()</code>: Gets the current month by name as defined in config</li>
<li><code>getDay()</code>: Gets the current day</li>
<li><code>getHour()</code>: Gets the current hour</li>
<li><code>getMinute()</code>: Gets the current minute</li>
<li><code>toString()</code>: Gets the current date and moon phase as a string</li>
<li><code>getFullTime()</code>: Gets the current date, time and moon phase as a string</li>
</list>

<p>For an example of how to use the API you can check out <a href = "https://github.com/Msvenda/DeathChest/tree/master/BWDeathChest">BWDeathChest</a></p>