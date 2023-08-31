package com.example.carpark.utils;

import com.example.carpark.model.Car;
import com.example.carpark.model.ParkedCar;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CarParkUtils {
    public static Car createCarFromRegistration(String registrationNumber) {
        return new Car(registrationNumber);
    }

    public static BigDecimal calculateCost(ParkedCar parkedCar, BigDecimal costPerHour) {
        Instant now = Instant.now().plus(1, ChronoUnit.HOURS);  // minimum charge of an hour
        Instant parkedTime = parkedCar.getParkedTime();
        long hoursParked = ChronoUnit.HOURS.between(parkedTime, now);
        return costPerHour.multiply(new BigDecimal(hoursParked));
    }


}
