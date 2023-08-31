package com.example.carpark.service;

import com.example.carpark.model.Car;
import com.example.carpark.model.ParkedCar;
import com.example.carpark.utils.CarParkUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CarParkStorage {
    private final Set<ParkedCar> parkedCars;
    private final long maxSpaces;
    private final BigDecimal costPerHour;

    public CarParkStorage(BigDecimal costPerHour, long maxSpaces) {
        this.parkedCars = Collections.synchronizedSet(new HashSet<>());
        this.maxSpaces = maxSpaces;
        this.costPerHour = costPerHour;
    }

    protected Set<ParkedCar> getParkedCars() {
        return parkedCars;
    }

    public boolean placeCarInStorage(ParkedCar parkedCar) {
        synchronized (getParkedCars()) {
            if (parkedCars.size() < maxSpaces) {
                parkedCars.add(parkedCar);
                return true;
            } else {
                return false;
            }
        }
    }

    public Optional<BigDecimal> carLeaves(Car car) {
        synchronized (this.parkedCars) {
            Optional<BigDecimal> cost = getParkedCars().stream()
                    .filter(parkedCar -> parkedCar.getCar().equals(car))
                    .collect(Collectors.toSet())
                    .stream()
                    .findFirst().map(parkedCar1 -> {
                        parkedCars.remove(parkedCar1);
                        return CarParkUtils.calculateCost(parkedCar1, costPerHour);
                    });
            return cost;
        }
    }

    public long getSpacesUsed() {
        return parkedCars.size();
    }
}