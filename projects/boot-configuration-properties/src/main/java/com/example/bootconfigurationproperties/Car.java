package com.example.bootconfigurationproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "car")
public class Car {

    public Car(String name, int range, double battery, int price) {
        this.name = name;
        this.range = range;
        this.battery = battery;
        this.price = price;
    }

    private final String name;
    private final int range;
    private final double battery;
    private final int price;

    @Override
    public String toString() {
        return "Car {" +
                "name=' " + name + '\'' +
                ", range= " + range + "km" +
                ", battery= " + battery + "kWh" +
                ", price= " + price + "₩" +
                " }";
    }

    public String getName() {
        return name;
    }

    public int getRange() {
        return range;
    }

    public double getBattery() {
        return battery;
    }

    public int getPrice() {
        return price;
    }
}
