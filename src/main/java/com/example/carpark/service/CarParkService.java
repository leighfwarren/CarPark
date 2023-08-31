package com.example.carpark.service;

import com.example.carpark.model.Car;
import com.example.carpark.model.CarParkMessage;

import java.math.BigDecimal;
import java.util.Optional;

public interface CarParkService {
    CarParkMessage parkCar(Car car);

    Optional<BigDecimal> carLeaves(Car car);
}
