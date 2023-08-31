package com.example.carpark.utils;

import com.example.carpark.model.Car;
import com.example.carpark.model.CarParkMessage;
import com.example.carpark.service.CarParkService;
import com.example.carpark.service.CarParkStorage;
import org.junit.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

public class CarParkTestUtils {

    public static String makeRandomRegistrationNumber() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Optional<CarParkMessage> parkACarAndWaitToLeave(int startTime, CarParkService carParkService)  {
        Optional<CarParkMessage> carParkMessage;
        Car car = CarParkUtils.createCarFromRegistration(CarParkTestUtils.makeRandomRegistrationNumber());
        try {
            int sleepTime = (int) Math.floor(1000 * new Random().nextDouble()) + startTime;
            log("car: "+car+" start time: "+(startTime+sleepTime));
            Thread.sleep(startTime+sleepTime);
            carParkMessage = Optional.of(carParkService.parkCar(car));
            log("car: "+car+" parked = "+carParkMessage.get());
            if (carParkMessage.get().equals(CarParkMessage.PARKED)) {
                int leaveSleepTime = 10000+(int) Math.floor(10000 * new Random().nextDouble());
                log("car: "+car+" leave time: "+leaveSleepTime);
                Thread.sleep(leaveSleepTime);
                Optional<BigDecimal> cost = carParkService.carLeaves(car);
                Method getCarParkStorage = CarParkService.class.getDeclaredMethod("getCarParkStorage");
                getCarParkStorage.setAccessible(true);
                CarParkStorage carParkStorage = (CarParkStorage) getCarParkStorage.invoke(carParkService);
                log("cars left = "+carParkStorage.getSpacesUsed());
                Assert.assertFalse(cost.isEmpty());
                BigDecimal costCurrency = cost.get();
            }
        } catch (InterruptedException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return carParkMessage;
    }

    public static Optional<CarParkMessage> parkCar(CarParkService carParkService) {
        Optional<CarParkMessage> carParkMessage;
        Car car = CarParkUtils.createCarFromRegistration(CarParkTestUtils.makeRandomRegistrationNumber());
        int sleepTime = (int) Math.floor(1000 * new Random().nextDouble());
        try {
            Thread.sleep(sleepTime);
            carParkMessage = Optional.of(carParkService.parkCar(car));
            log("car "+car+" = "+carParkMessage.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return carParkMessage;
    }

    public static void log(String message) {
        long time = Instant.now().getEpochSecond();
        System.out.println(new Timestamp(time)+": "+message);
    }
}
