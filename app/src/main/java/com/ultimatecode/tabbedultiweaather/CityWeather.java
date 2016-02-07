package com.ultimatecode.tabbedultiweaather;

/**
 * Created by salah on 07/02/16.
 * This class will hold the data received from OpenWeatherAPI
 */
public class CityWeather {
    private final String id;
    private final String name;
    private final String desc;
    private final String temp;
    private final String cloud;
    private final String wind;
    private final String rain;

    public CityWeather(String id, String name, String desc, String temp, String cloud, String wind, String rain) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.temp = temp;
        this.cloud = cloud;
        this.wind = wind;
        this.rain = rain;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
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

    public String getRain() {
        return rain;
    }

    // static array
    public static final CityWeather[] DummyWeatherInfo = new CityWeather[]{
            new CityWeather("1", "New York", "Sunny", "30", "11", "2", "80"),
            new CityWeather("2", "Boston", "rainy", "32", "13", "12", "30"),
            new CityWeather("3", "Moscow", "snow", "-32", "-13", "-12", "-30")
    };
}
