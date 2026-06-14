package br.com.gynlog.data;

import br.com.gynlog.model.ExpenseType;
import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório central de dados da aplicação.
 * Mantém as listas em memória durante o uso e
 * sincroniza com os arquivos CSV a cada alteração.
 */
public final class AppData {

    private final List<Vehicle>     vehicles     = new ArrayList<>();
    private final List<ExpenseType> expenseTypes = new ArrayList<>();
    private final List<Movement>    movements    = new ArrayList<>();
    private final List<Runnable>    listeners    = new ArrayList<>();

    private int nextVehicleId     = 1;
    private int nextExpenseTypeId = 1;
    private int nextMovementId    = 1;

    public AppData() {
        CsvStorage.inicializar();

        List<Vehicle>     veiculosSalvos = CsvStorage.lerVeiculos();
        List<ExpenseType> tiposSalvos    = CsvStorage.lerTiposDespesa();
        List<Movement>    movsSalvos     = CsvStorage.lerMovimentacoes(veiculosSalvos, tiposSalvos);

        if (veiculosSalvos.isEmpty() && tiposSalvos.isEmpty()) {
            seed();
        } else {
            vehicles.addAll(veiculosSalvos);
            expenseTypes.addAll(tiposSalvos);
            movements.addAll(movsSalvos);

            vehicles.stream().mapToInt(Vehicle::id).max()
                    .ifPresent(max -> nextVehicleId = max + 1);
            expenseTypes.stream().mapToInt(ExpenseType::id).max()
                    .ifPresent(max -> nextExpenseTypeId = max + 1);
            movements.stream().mapToInt(Movement::id).max()
                    .ifPresent(max -> nextMovementId = max + 1);
        }
    }

    // ─── LEITURA ─────────────────────────────────────────────────────────────

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

    // ─── VEÍCULOS ────────────────────────────────────────────────────────────

    public Vehicle addVehicle(String plate, String model, String brand, int year, String status) {
        // RD001 — verifica se já existe veículo com a mesma placa
        String placaNova = plate.trim().toUpperCase().replace("-", "").replace(" ", "");
        for (Vehicle v : vehicles) {
            String placaExistente = v.plate().toUpperCase().replace("-", "").replace(" ", "");
            if (placaExistente.equals(placaNova)) {
                return null; // placa duplicada
            }
        }
        Vehicle vehicle = new Vehicle(nextVehicleId++, plate, model, brand, year, status);
        vehicles.add(vehicle);
        salvarENotificar();
        return vehicle;
    }

    public void updateVehicle(Vehicle old, String plate, String model, String brand, int year, String status) {
        Vehicle updated = new Vehicle(old.id(), plate, model, brand, year, status);
        vehicles.replaceAll(v -> v.id() == old.id() ? updated : v);
        movements.replaceAll(m -> m.vehicle().id() == old.id()
                ? new Movement(m.id(), updated, m.category(), m.description(), m.date(), m.value(), m.mileage())
                : m);
        salvarENotificar();
    }

    public boolean removeVehicle(Vehicle vehicle) {
        // Não permite remover veículo com movimentações — RD010
        if (movements.stream().anyMatch(m -> m.vehicle().id() == vehicle.id())) {
            return false;
        }
        vehicles.remove(vehicle);
        salvarENotificar();
        return true;
    }

    // ─── TIPOS DE DESPESA ────────────────────────────────────────────────────

    public ExpenseType addExpenseType(String name, String description) {
        ExpenseType type = new ExpenseType(nextExpenseTypeId++, name, description);
        expenseTypes.add(type);
        salvarENotificar();
        return type;
    }

    public void updateExpenseType(ExpenseType old, String name, String description) {
        ExpenseType updated = new ExpenseType(old.id(), name, description);
        expenseTypes.replaceAll(t -> t.id() == old.id() ? updated : t);
        movements.replaceAll(m -> m.category().id() == old.id()
                ? new Movement(m.id(), m.vehicle(), updated, m.description(), m.date(), m.value(), m.mileage())
                : m);
        salvarENotificar();
    }

    public boolean removeExpenseType(ExpenseType type) {
        // Não permite remover tipo com movimentações — RD005
        if (movements.stream().anyMatch(m -> m.category().id() == type.id())) {
            return false;
        }
        expenseTypes.remove(type);
        salvarENotificar();
        return true;
    }

    // ─── MOVIMENTAÇÕES ───────────────────────────────────────────────────────

    public Movement addMovement(Vehicle vehicle, ExpenseType category, String description,
                                LocalDate date, BigDecimal value, double mileage) {
        Movement movement = new Movement(nextMovementId++, vehicle, category, description, date, value, mileage);
        movements.add(movement);
        salvarENotificar();
        return movement;
    }

    public void updateMovement(Movement old, Vehicle vehicle, ExpenseType category,
                               String description, LocalDate date, BigDecimal value, double mileage) {
        Movement updated = new Movement(old.id(), vehicle, category, description, date, value, mileage);
        movements.replaceAll(m -> m.id() == old.id() ? updated : m);
        salvarENotificar();
    }

    public void removeMovement(Movement movement) {
        movements.remove(movement);
        salvarENotificar();
    }

    // ─── CÁLCULOS ────────────────────────────────────────────────────────────

    /** Soma todas as despesas de todos os veículos. */
    public BigDecimal totalExpenses() {
        return movements.stream()
                .map(Movement::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ─── INTERNOS ────────────────────────────────────────────────────────────

    /** Salva tudo nos CSVs e avisa as telas para atualizar. */
    private void salvarENotificar() {
        CsvStorage.gravarVeiculos(vehicles);
        CsvStorage.gravarTiposDespesa(expenseTypes);
        CsvStorage.gravarMovimentacoes(movements);
        listeners.forEach(Runnable::run);
    }

    /** Dados iniciais de demonstração — só roda na primeira vez. */
    private void seed() {
        Vehicle truck = addVehicle("GYN-2040", "Constellation 24.280", "Volkswagen", 2022, "Ativo");
        Vehicle van   = addVehicle("LOG-1010", "Sprinter 417", "Mercedes-Benz", 2023, "Ativo");
        Vehicle car   = addVehicle("FRO-3321", "Strada Endurance", "Fiat", 2021, "Manutencao");

        ExpenseType fuel        = addExpenseType("Combustivel", "Abastecimento da frota");
        ExpenseType maintenance = addExpenseType("Manutencao", "Pecas e servicos mecanicos");
        ExpenseType toll        = addExpenseType("Pedagio", "Tarifas de rodovia");
        addExpenseType("Seguro", "Seguro veicular");

        addMovement(truck, fuel,        "Abastecimento completo",  LocalDate.now().minusDays(2), new BigDecimal("890.50"), 0.0);
        addMovement(van,   toll,        "Rota Goiania - Brasilia", LocalDate.now().minusDays(1), new BigDecimal("76.40"),  0.0);
        addMovement(car,   maintenance, "Troca de oleo e filtros", LocalDate.now(),              new BigDecimal("420.00"), 0.0);
    }
}