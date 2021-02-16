package com.bwroleplay.bwtime;

import java.util.List;

/**
 * Model class which holds info about the server time
 * 
 * @author MX26
 *
 */
public class ServerTime {
	private static ServerTime singleton;

	private double minSec;
	private double hSec;
	private long dSec;
	private long mSec;
	private long ySec;
	
	private long startingTime;
	private int maxMonths;
	private int maxDays;
	private int minutesPerDay;
	private List<String> months;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	
	protected  ServerTime(long st, int mm, int md, int mpd, List<String> m) {
		maxMonths = mm;
		maxDays = md;
		minutesPerDay = mpd;
		months = m;
		
		startingTime = st;
		dSec = (long) (minutesPerDay*60.0);
		hSec = (dSec/24.0);
		minSec = (hSec/60.0);
		mSec = (long) (28.0*dSec);
		ySec = (long) (12.0*mSec);
	}

	void updateTime() {
		
		//convert from milis to sec
		long runningTime = (System.currentTimeMillis() - startingTime)/1000;
		//update values
		minute = (int) (runningTime / minSec % 60);
		hour = (int) (runningTime / hSec % 24);
		day = (int) (((runningTime / dSec) % maxDays) + 1);
		month = (int) ((runningTime / mSec) % maxMonths);
		year = (int) ((runningTime / ySec) + 1);
		
		
	}


	/**
	 * Gets the starting time in ms
	 * 
	 * @return long value representing starting time in milliseconds
	 */
	public long getStartingTime() {
		return startingTime;
	}

	/**
	 * Gets the current time in minecraft ticks
	 * 
	 * @return long value representing current time in ticks
	 */
	public long dayTimeInTicks() {
		long ticks = 0;
		//get hour+min seconds
		double time = hour*hSec + minute*minSec;
		//get %of day passed
		time/=dSec;
		if(hour > 6) {
			ticks += (long) (time*24000-6000);
		}
		else {
			ticks += (long) (time*24000-6000+24000);
		}
		
		ticks+= getMoonPhaseTicks();
		
		return ticks;
	}
	
	private long getMoonPhaseTicks() {
		//split month into 8ths and switch on that
		int phase = (int) (day/(maxDays*1.0)*8);
		switch(phase) {
		case 1: return 24000;
		case 2: return 48000;
		case 3: return 72000;
		case 4: return 96000;
		case 5: return 120000;
		case 6: return 144000;
		case 7: return 168000;
		default: return 0;
		}
	}
	
	/**
	 * Sets the current day, month and year
	 * 
	 * @param d requested day
	 * @param m requested month
	 * @param y requested year
	 */
	public void setDate(int d, int m, int y) {
		long yearSeconds = (y) * ySec;
		long monthSeconds = (m) * mSec;
		long daySeconds = (d) * dSec;
		long totalMilis = yearSeconds + monthSeconds + daySeconds;
		totalMilis*=1000;
		startingTime = System.currentTimeMillis() - totalMilis;
		updateTime();
	}
	
	/**
	 * Sets the current day, month, year, hour and minute
	 * 
	 * @param d requested day
	 * @param m requested month
	 * @param y requested year
	 * @param h requested hour
	 * @param min requested minute
	 */
	public void setDate(int d, int m, int y, int h, int min) {
		long time = (long) (min*minSec);
		time += (long) (h*hSec);
		time += m*mSec;
		time += d*dSec ;
		time += y*ySec;
		time *= 1000;
		startingTime= System.currentTimeMillis() - time;
		updateTime();
	}

	/**
	 * Gets the current in-game moon phase 
	 * 
	 * @return current moon phase as string
	 */
	public String getMoonPhase() {
		//split month into 8ths and switch on that
		int phase = (int) (day/(maxDays*1.0)*8);
		switch(phase) {
		case 0: return "Full";
		case 1: return "Waning Gibbous";
		case 2: return "Last Quarter";
		case 3: return "Waning Crescent";
		case 4: return "New";
		case 5: return "Waxing Crescent";
		case 6: return "First Quarter";
		case 7: return "Waxing Gibbous";
		default: return "Full";
		}
	}
	
	/**
	 * Manually sets the starting time
	 * 
	 * @param startingTime starting time in miliseconds
	 */
	public void setStartingTime(long startingTime) {
		this.startingTime = startingTime;
	}

	/**
	 * Gets the current year
	 * 
	 * @return current year as int
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Gets the current month
	 * 
	 * @return current month as int
	 */
	public int getMonth() {
		return month;
	}
	
	/**
	 * Gets the current month by name as defined in config
	 * 
	 * @return current month as string
	 */
	public String getMonthByName() {
		return months.get(month);
	}

	/**
	 * Gets the current day
	 * 
	 * @return current day as int
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Gets the current hour
	 * 
	 * @return current hour as int
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Gets the current minute
	 * 
	 * @return current minute as int
	 */
	public int getMinute() {
		return minute;
	}


	private String getSeason() {
		int season = (int) (month/(maxMonths*1.0)*4);
		switch(season) {
		case 1: return "Summer";
		case 2: return "Autumn";
		case 3: return "Winter";
		default: return "Spring";
		}
	}
	
	/**
	 * Gets the current date and moon phase as a string
	 * 
	 * @return current date as string
	 */
	public String toString() {
		//It is %s %s of year %s.\nThe season is %s, under a %s Moon\"
		return String.format("It is %s %d of year %s.\nThe season is %s, under a %s Moon", 
				months.get(month), day, year, getSeason(), getMoonPhase());
	}
	
	/**
	 * Gets the current date, time and moon phase as a string
	 * 
	 * @return current date and time as string
	 */
	public String getFullTime() {
		return String.format("It is %s %d of year %s.\nThe season is %s, under a %s Moon\n%d:%d", 
				getMonthByName(), day, year, getSeason(), getMoonPhase(), hour, minute);
	}


	
}
