import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;

public class CarParkTest {

    @org.junit.Test
    public void parkCar() {
        CarPark carPark = new CarPark();
        Car car = CarPark.createCarFromRegistration("LA71KSE");
        CarParkMessage carParkMessage1 = carPark.parkCar(car);
        assertEquals(CarParkMessage.PARKED,carParkMessage1);
        Car car2 = CarPark.createCarFromRegistration("MA22FRE");
        CarParkMessage carParkMessage2 = carPark.parkCar(car2);
        assertEquals(CarParkMessage.PARKED,carParkMessage2);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Car leaveCar1 = CarPark.createCarFromRegistration("LA71KSE");
        Optional<BigDecimal> parkingCost = carPark.carLeaves(leaveCar1);
        Car leaveCar2 = CarPark.createCarFromRegistration("MA22FRE");
        Optional<BigDecimal> parkingCost2 = carPark.carLeaves(leaveCar2);
        Assert.assertEquals(0,parkingCost.get().compareTo(new BigDecimal(2)));
        Assert.assertEquals(0,parkingCost2.get().compareTo(new BigDecimal(2)));
        System.out.println(parkingCost.get().toPlainString());
        System.out.println(parkingCost2.get().toPlainString());
    }

    @org.junit.Test
    public void parkFull() {
        CarPark carPark = new CarPark();
        int parkedCount = 0;
        int fullCount = 0;
        for (int i=0;i<120;i++) {
            Car car = CarPark.createCarFromRegistration("NUMBER"+i);
            CarParkMessage carParkMessage1 = carPark.parkCar(car);
            if (carParkMessage1 == CarParkMessage.PARKED) parkedCount++;
            if (carParkMessage1 == CarParkMessage.FULL) fullCount++;
        }
        assertEquals(100,parkedCount);
        assertEquals(20,fullCount);
    }
}