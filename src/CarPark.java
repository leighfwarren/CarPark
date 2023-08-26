import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CarPark {
    protected static BigDecimal COST_PER_HOUR = new BigDecimal(2);
    protected static long MAX_SPACES = 100;
    private final Set<ParkedCar> parkedCars;
    public CarPark() {
        this.parkedCars = Collections.synchronizedSet(new HashSet<>());
    }
    public CarParkMessage parkCar(Car car) {
        ParkedCar parkedCar = ParkedCar.park(car);
        return placeCarInSpace(parkedCar) ? CarParkMessage.PARKED : CarParkMessage.FULL;
    }
    private boolean placeCarInSpace(ParkedCar parkedCar) {
        synchronized (parkedCars) {
            if (parkedCars.size() < MAX_SPACES) {
                parkedCars.add(parkedCar);
                return true;
            } else {
                return false;
            }
        }
    }
    public Optional<BigDecimal> carLeaves(Car car) {
        synchronized (parkedCars) {
            Optional<BigDecimal> cost = parkedCars.stream()
                    .filter(parkedCar -> parkedCar.getCar().equals(car))
                    .map(CarPark::calculateCost).findFirst();
            return cost;
        }
    }
    public static Car createCarFromRegistration(String registrationNumber) {
        return new Car(registrationNumber);
    }
    protected static BigDecimal calculateCost(ParkedCar parkedCar) {
        Instant now = Instant.now().plus(1, ChronoUnit.HOURS);  // minimum charge of an hour
        Instant parkedTime = parkedCar.getParkedTime();
        long hoursParked = ChronoUnit.HOURS.between(parkedTime, now);
        return COST_PER_HOUR.multiply(new BigDecimal(hoursParked));
    }
}
