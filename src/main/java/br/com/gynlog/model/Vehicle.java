package br.com.gynlog.model;

public record Vehicle(int id, String plate, String model, String brand, int year, String status) {
    @Override
    public String toString() {
        return plate + " - " + model;
    }
}
