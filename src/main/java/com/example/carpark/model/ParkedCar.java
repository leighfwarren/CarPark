package com.example.carpark.model;

import java.time.Instant;

public class ParkedCar {

    private final Instant parkedTime;
    private final Car car;

    public ParkedCar(Instant parkedTime, Car car) {
        this.parkedTime = parkedTime;
        this.car = car;
    }

    public Instant getParkedTime() {
        return parkedTime;
    }

    public Car getCar() {
        return car;
    }

    public static ParkedCar park(Car car) {
        Instant parkedTime = Instant.now();
        ParkedCar parkedCar = new ParkedCar(parkedTime,car);
        return parkedCar;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ParkedCar)
            return this.getCar().equals(((ParkedCar)obj).getCar());
        else
            return false;
    }


}
