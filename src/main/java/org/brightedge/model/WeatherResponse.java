package org.brightedge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    private Main main;
    private Wind wind;
    private int visibility;
    private List<Weather> weather;
    private Sys sys;
    private String name; // City name

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Main {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private double humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Wind {
        private double speed;
        private double deg;
        private double gust;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Weather {
        private String description;
        private String main;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Sys {
        private String country;
    }
}