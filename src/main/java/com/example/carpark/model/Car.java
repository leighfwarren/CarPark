package com.example.carpark.model;

public class Car {
    private final String registrationNumber;
    public Car(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Car)
            return this.registrationNumber.equals(((Car)obj).getRegistrationNumber());
        else
            return false;
    }

    @Override
    public String toString() {
        return registrationNumber;
    }
}
