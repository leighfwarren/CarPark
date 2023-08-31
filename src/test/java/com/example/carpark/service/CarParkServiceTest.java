package com.example.carpark.service;

import com.example.carpark.model.Car;
import com.example.carpark.model.CarParkMessage;
import com.example.carpark.model.ParkedCar;
import com.example.carpark.utils.CarParkUtils;
import org.junit.Assert;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CarParkServiceTest {

    @org.junit.Test
    public void parkACar() {
        CarParkService carParkService = new CarParkService();
        String REGISTRATION_NUMBER = "LA71KSE";
        Car car = CarParkUtils.createCarFromRegistration(REGISTRATION_NUMBER);
        CarParkMessage carParkMessage = carParkService.parkCar(car);
        CarParkStorage carParkStorage = carParkService.getCarParkStorage();
        Set<ParkedCar> parkedCars = carParkStorage.getParkedCars();
        Assert.assertEquals(1,parkedCars.size());
        ParkedCar parkedCar = parkedCars.stream().findFirst().get();
        Assert.assertEquals(REGISTRATION_NUMBER,parkedCar.getCar().getRegistrationNumber());
    }

    @org.junit.Test
    public void carParkFull() {
        CarParkStorage mockCarParkStorage = mock(CarParkStorage.class);
        when(mockCarParkStorage.placeCarInStorage(any())).thenReturn(false);
        CarParkService carParkService = new CarParkService(mockCarParkStorage);
        String REGISTRATION_NUMBER = "LA71KSE";
        Car car = CarParkUtils.createCarFromRegistration(REGISTRATION_NUMBER);
        CarParkMessage carParkMessage = carParkService.parkCar(car);
        Assert.assertEquals(CarParkMessage.FULL,carParkMessage);
    }

    @org.junit.Test
    public void carParkSpaces() {
        CarParkStorage mockCarParkStorage = mock(CarParkStorage.class);
        when(mockCarParkStorage.placeCarInStorage(any())).thenReturn(true);
        CarParkService carParkService = new CarParkService(mockCarParkStorage);
        String REGISTRATION_NUMBER = "LA71KSE";
        Car car = CarParkUtils.createCarFromRegistration(REGISTRATION_NUMBER);
        CarParkMessage carParkMessage = carParkService.parkCar(car);
        Assert.assertEquals(CarParkMessage.PARKED,carParkMessage);
    }
}
