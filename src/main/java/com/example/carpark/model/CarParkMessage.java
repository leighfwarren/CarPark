package com.example.carpark.model;

public enum CarParkMessage {
    FULL("Full"),PARKED("Parked");
    CarParkMessage(String name) {
        this.name = name;
    }
    private final String name;
    @Override
    public String toString() {
        return name;
    }
}
