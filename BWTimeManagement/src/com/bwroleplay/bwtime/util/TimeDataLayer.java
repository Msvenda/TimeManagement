package com.bwroleplay.bwtime.util;

import com.bwroleplay.bwtime.ServerTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeDataLayer {
    private static TimeDataLayer singleton;
    public static TimeDataLayer getDataLayer(){
        if(singleton == null){
            singleton = new TimeDataLayer();
        }
        return singleton;
    }

    private List<UUID> worlds;
    private ServerTime serverTime;
    private List<String> months = null;

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    private TimeDataLayer(){
        worlds = new ArrayList<>();
    }

    public void setServerTime(ServerTime serverTime) {
        this.serverTime = serverTime;
    }

    public List<UUID> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<UUID> worlds) {
        this.worlds = worlds;
    }

    public ServerTime getServerTime() {
        return this.serverTime;
    }
}
