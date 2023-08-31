package com.example.carpark;

import com.example.carpark.model.Car;
import com.example.carpark.model.CarParkMessage;
import com.example.carpark.service.impl.BasicCarPark;
import com.example.carpark.service.impl.CarParkStorage;
import com.example.carpark.utils.CarParkUtils;
import com.example.carpark.utils.CarParkTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class CarParkTest {

    @org.junit.Test
    public void park2Car() {
        BasicCarPark carParkService = new BasicCarPark();
        Car car = CarParkUtils.createCarFromRegistration("LA71KSE");
        CarParkMessage carParkMessage1 = carParkService.parkCar(car);
        assertEquals(CarParkMessage.PARKED,carParkMessage1);
        Car car2 = CarParkUtils.createCarFromRegistration("MA22FRE");
        CarParkMessage carParkMessage2 = carParkService.parkCar(car2);
        assertEquals(CarParkMessage.PARKED,carParkMessage2);
    }

    @org.junit.Test
    public void parkFullNoThreads() {
        BasicCarPark carParkService = new BasicCarPark();
        int parkedCount = 0;
        int fullCount = 0;
        for (int i=0;i<120;i++) {
            Car car = CarParkUtils.createCarFromRegistration("NUMBER"+i);
            CarParkMessage carParkMessage1 = carParkService.parkCar(car);
            if (carParkMessage1 == CarParkMessage.PARKED) parkedCount++;
            if (carParkMessage1 == CarParkMessage.FULL) fullCount++;
        }
        assertEquals(100,parkedCount);
        assertEquals(20,fullCount);
    }

    @org.junit.Test
    public void testPark200CarsWithThreads() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        BasicCarPark carParkService = new BasicCarPark();
        List<Future<Optional<CarParkMessage>>> carFutureList = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 200; i++) {
            CompletableFuture<Optional<CarParkMessage>> completableFuture = CompletableFuture.supplyAsync(() -> CarParkTestUtils.parkCar(carParkService), executor);
            carFutureList.add(completableFuture);
        }

        CompletableFuture.allOf(carFutureList.toArray(new CompletableFuture[0]));
        Thread.sleep(1000);

        Method getCarParkStorage = BasicCarPark.class.getDeclaredMethod("getCarParkStorage");
        getCarParkStorage.setAccessible(true);
        CarParkStorage carParkStorage = (CarParkStorage) getCarParkStorage.invoke(carParkService);
        Assert.assertEquals(100,carParkStorage.getSpacesUsed());
    }

    @org.junit.Test
    public void testParkManyCarsUntilFullWithThreads() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BasicCarPark carParkService = new BasicCarPark();
        Method getCarParkStorage = BasicCarPark.class.getDeclaredMethod("getCarParkStorage");
        getCarParkStorage.setAccessible(true);
        CarParkStorage carParkStorage = (CarParkStorage) getCarParkStorage.invoke(carParkService);
        List<CompletableFuture<Optional<CarParkMessage>>> carThreadList = new ArrayList<>();
        CarParkTestUtils.log("Filling car park with 200 cars");

        int startTime = 0;
        ExecutorService executor = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 200; i++) {
            int finalStartTime = startTime;
            CompletableFuture<Optional<CarParkMessage>> completableFuture = CompletableFuture.supplyAsync(() -> CarParkTestUtils.parkACarAndWaitToLeave(finalStartTime, carParkService), executor);
            carThreadList.add(completableFuture);
            startTime+=(int)(Math.random()*100);
        }

        CompletableFuture.allOf(carThreadList.toArray(new CompletableFuture[0]));
        AtomicInteger carParkFull = new AtomicInteger();
        AtomicInteger carWasParked = new AtomicInteger();
        carThreadList.forEach(future -> {
            try {
                Optional<CarParkMessage> optionalCarParkMessage = future.get();
                optionalCarParkMessage.ifPresent(carParkMessage -> {
                    switch (carParkMessage) {
                        case FULL -> carParkFull.getAndIncrement();
                        case PARKED -> carWasParked.getAndIncrement();
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

        });

        System.out.println("Managed to park "+carWasParked.get()+" cars");
        System.out.println("Failed to park "+carParkFull.get()+" cars");

        Assert.assertEquals(0,carParkStorage.getSpacesUsed());
        System.out.println("Car Park is now empty");
    }

    @Test
    public void parkMultipleCarsThatLeave() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BasicCarPark carParkService = new BasicCarPark();
        Method getCarParkStorage = carParkService.getClass().getDeclaredMethod("getCarParkStorage");
        getCarParkStorage.setAccessible(true);
        CarParkStorage carParkStorage = (CarParkStorage) getCarParkStorage.invoke(carParkService);

        Car car1 = CarParkUtils.createCarFromRegistration("LK10XMM");
        Car car2 = CarParkUtils.createCarFromRegistration("KP20CVB");
        Car car3 = CarParkUtils.createCarFromRegistration("JK30MBN");
        carParkService.parkCar(car1);
        carParkService.parkCar(car2);
        carParkService.parkCar(car3);
        Assert.assertEquals(3,carParkStorage.getSpacesUsed());

        carParkService.carLeaves(car1);
        carParkService.carLeaves(car3);
        carParkService.carLeaves(car2);
        Assert.assertEquals(0,carParkStorage.getSpacesUsed());
    }
}