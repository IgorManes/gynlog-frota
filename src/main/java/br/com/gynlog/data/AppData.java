package br.com.gynlog.data;

import br.com.gynlog.model.ExpenseType;
import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class AppData {
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<ExpenseType> expenseTypes = new ArrayList<>();
    private final List<Movement> movements = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private int nextVehicleId = 1;
    private int nextExpenseTypeId = 1;
    private int nextMovementId = 1;

    public AppData() {
        seed();
    }

    public List<Vehicle> vehicles() {
        return List.copyOf(vehicles);
    }

    public List<ExpenseType> expenseTypes() {
        return List.copyOf(expenseTypes);
    }

    public List<Movement> movements() {
        return List.copyOf(movements);
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public Vehicle addVehicle(String plate, String model, String brand, int year, String status) {
        Vehicle vehicle = new Vehicle(nextVehicleId++, plate, model, brand, year, status);
        vehicles.add(vehicle);
        notifyListeners();
        return vehicle;
    }

    public void updateVehicle(Vehicle oldVehicle, String plate, String model, String brand, int year, String status) {
        Vehicle updated = new Vehicle(oldVehicle.id(), plate, model, brand, year, status);
        vehicles.replaceAll(vehicle -> vehicle.id() == oldVehicle.id() ? updated : vehicle);
        movements.replaceAll(movement -> movement.vehicle().id() == oldVehicle.id()
                ? new Movement(movement.id(), updated, movement.category(), movement.description(), movement.date(), movement.value())
                : movement);
        notifyListeners();
    }

    public boolean removeVehicle(Vehicle vehicle) {
        if (movements.stream().anyMatch(movement -> movement.vehicle().id() == vehicle.id())) {
            return false;
        }
        vehicles.remove(vehicle);
        notifyListeners();
        return true;
    }

    public ExpenseType addExpenseType(String name, String description) {
        ExpenseType type = new ExpenseType(nextExpenseTypeId++, name, description);
        expenseTypes.add(type);
        notifyListeners();
        return type;
    }

    public void updateExpenseType(ExpenseType oldType, String name, String description) {
        ExpenseType updated = new ExpenseType(oldType.id(), name, description);
        expenseTypes.replaceAll(type -> type.id() == oldType.id() ? updated : type);
        movements.replaceAll(movement -> movement.category().id() == oldType.id()
                ? new Movement(movement.id(), movement.vehicle(), updated, movement.description(), movement.date(), movement.value())
                : movement);
        notifyListeners();
    }

    public boolean removeExpenseType(ExpenseType type) {
        if (movements.stream().anyMatch(movement -> movement.category().id() == type.id())) {
            return false;
        }
        expenseTypes.remove(type);
        notifyListeners();
        return true;
    }

    public Movement addMovement(Vehicle vehicle, ExpenseType category, String description,
                                LocalDate date, BigDecimal value) {
        Movement movement = new Movement(nextMovementId++, vehicle, category, description, date, value);
        movements.add(movement);
        notifyListeners();
        return movement;
    }

    public void updateMovement(Movement oldMovement, Vehicle vehicle, ExpenseType category,
                               String description, LocalDate date, BigDecimal value) {
        Movement updated = new Movement(oldMovement.id(), vehicle, category, description, date, value);
        movements.replaceAll(movement -> movement.id() == oldMovement.id() ? updated : movement);
        notifyListeners();
    }

    public void removeMovement(Movement movement) {
        movements.remove(movement);
        notifyListeners();
    }

    public BigDecimal totalExpenses() {
        return movements.stream().map(Movement::value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void notifyListeners() {
        listeners.forEach(Runnable::run);
    }

    private void seed() {
        Vehicle truck = addVehicle("GYN-2040", "Constellation 24.280", "Volkswagen", 2022, "Ativo");
        Vehicle van = addVehicle("LOG-1010", "Sprinter 417", "Mercedes-Benz", 2023, "Ativo");
        Vehicle car = addVehicle("FRO-3321", "Strada Endurance", "Fiat", 2021, "Manutencao");

        ExpenseType fuel = addExpenseType("Combustivel", "Abastecimento da frota");
        ExpenseType maintenance = addExpenseType("Manutencao", "Pecas e servicos mecanicos");
        ExpenseType toll = addExpenseType("Pedagio", "Tarifas de rodovia");
        addExpenseType("Seguro", "Seguro veicular");

        addMovement(truck, fuel, "Abastecimento completo", LocalDate.now().minusDays(2), new BigDecimal("890.50"));
        addMovement(van, toll, "Rota Goiania - Brasilia", LocalDate.now().minusDays(1), new BigDecimal("76.40"));
        addMovement(car, maintenance, "Troca de oleo e filtros", LocalDate.now(), new BigDecimal("420.00"));
    }
}
