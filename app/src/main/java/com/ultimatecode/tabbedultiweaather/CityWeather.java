package com.ultimatecode.tabbedultiweaather;

/**
 * Created by salah on 07/02/16.
 * This class will hold the data received from OpenWeatherAPI
 */
public class CityWeather {
    private final String name;
    private final String desc;
    private final String temp;
    private final String cloud;
    private final String wind;
    private final String humidity;
    private final String icon;

    public CityWeather(String name, String desc, String temp, String cloud, String wind, String humidity, String icon) {
        this.name = name;
        this.desc = desc;
        this.temp = temp;
        this.cloud = cloud;
        this.wind = wind;
        this.humidity = humidity;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getTemp() {
        return temp;
    }

    public String getCloud() {
        return cloud;
    }

    public String getWind() {
        return wind;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getIconCode() {
        return icon;
    }
}


