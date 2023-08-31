package com.example.carpark.service;

import com.example.carpark.model.CarParkMessage;
import com.example.carpark.model.Car;
import com.example.carpark.model.ParkedCar;

import java.math.BigDecimal;
import java.util.*;

public class CarParkService {
    protected static BigDecimal COST_PER_HOUR = new BigDecimal(2);
    protected static long MAX_SPACES = 100;
    private CarParkStorage carParkStorage;

    public CarParkService() {
    }
    public CarParkService(CarParkStorage carParkStorage) {
        this.carParkStorage = carParkStorage;
    }
    public void init() {
        this.carParkStorage = new CarParkStorage(COST_PER_HOUR, MAX_SPACES);
    }
    public CarParkMessage parkCar(Car car) {
        ParkedCar parkedCar = ParkedCar.park(car);
        return placeCarInSpace(parkedCar) ? CarParkMessage.PARKED : CarParkMessage.FULL;
    }
    private boolean placeCarInSpace(ParkedCar parkedCar) {
        return getCarParkStorage().placeCarInStorage(parkedCar);
    }
    public Optional<BigDecimal> carLeaves(Car car) {
        return getCarParkStorage().carLeaves(car);
    }
    protected CarParkStorage getCarParkStorage() {
        if (this.carParkStorage == null) {
            init();
        }
        return this.carParkStorage;
    }

}
