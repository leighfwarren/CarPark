import java.time.Instant;

public class Car {
    private final String registrationNumber;
    public Car(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    protected String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Car)
            return this.registrationNumber.equals(((Car)obj).getRegistrationNumber());
        else
            return false;
    }
}
