package br.com.gynlog.ui.components;

import br.com.gynlog.model.ExpenseType;
import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class FormDialogs {

    public record VehicleForm(String plate, String model, String brand, int year, String status) {}
    public record ExpenseTypeForm(String name, String description) {}

    public record MovementForm(Vehicle vehicle, ExpenseType category, String description,
                               LocalDate date, BigDecimal value, double mileage) {}

    private FormDialogs() {}

    public static VehicleForm vehicle(Component parent, Vehicle current) {
        JTextField plate  = UiFactory.field();
        JTextField model  = UiFactory.field();
        JTextField brand  = UiFactory.field();
        JTextField year   = UiFactory.field();
        JComboBox<String> status = new JComboBox<>(new String[]{"Ativo", "Manutencao", "Inativo"});

        if (current != null) {
            plate.setText(current.plate());
            model.setText(current.model());
            brand.setText(current.brand());
            year.setText(String.valueOf(current.year()));
            status.setSelectedItem(current.status());
        }

        if (!show(parent, current == null ? "Novo veiculo" : "Editar veiculo",
                new String[]{"Placa", "Modelo", "Marca", "Ano", "Status"},
                new JComponent[]{plate, model, brand, year, status})) {
            return null;
        }

        try {
            require(plate.getText(), "Informe a placa.");
            require(model.getText(), "Informe o modelo.");
            require(brand.getText(), "Informe a marca.");
            int parsedYear = Integer.parseInt(year.getText().trim());
            return new VehicleForm(plate.getText().trim().toUpperCase(), model.getText().trim(),
                    brand.getText().trim(), parsedYear, String.valueOf(status.getSelectedItem()));
        } catch (IllegalArgumentException e) {
            error(parent, e.getMessage());
            return vehicle(parent, current);
        }
    }

    public static ExpenseTypeForm expenseType(Component parent, ExpenseType current) {
        JTextField name        = UiFactory.field();
        JTextField description = UiFactory.field();

        if (current != null) {
            name.setText(current.name());
            description.setText(current.description());
        }

        if (!show(parent, current == null ? "Novo tipo de despesa" : "Editar tipo de despesa",
                new String[]{"Nome", "Descricao"}, new JComponent[]{name, description})) {
            return null;
        }

        try {
            require(name.getText(), "Informe o nome.");
            require(description.getText(), "Informe a descricao.");
            return new ExpenseTypeForm(name.getText().trim(), description.getText().trim());
        } catch (IllegalArgumentException e) {
            error(parent, e.getMessage());
            return expenseType(parent, current);
        }
    }

    public static MovementForm movement(Component parent, List<Vehicle> vehicles,
                                        List<ExpenseType> expenseTypes, Movement current) {
        if (vehicles.isEmpty() || expenseTypes.isEmpty()) {
            error(parent, "Cadastre pelo menos um veiculo e um tipo de despesa.");
            return null;
        }

        JComboBox<Vehicle>     vehicle     = new JComboBox<>(vehicles.toArray(Vehicle[]::new));
        JComboBox<ExpenseType> category    = new JComboBox<>(expenseTypes.toArray(ExpenseType[]::new));
        JTextField             description = UiFactory.field();
        JTextField             date        = UiFactory.field();
        JTextField             value       = UiFactory.field();
        JTextField             mileage     = UiFactory.field(); // campo de quilometragem

        date.setText(LocalDate.now().toString());
        mileage.setText("0"); // valor padrão

        if (current != null) {
            vehicle.setSelectedItem(current.vehicle());
            category.setSelectedItem(current.category());
            description.setText(current.description());
            date.setText(current.date().toString());
            value.setText(current.value().toPlainString());
            // Exibe quilometragem existente, se houver
            mileage.setText(current.mileage() > 0 ? String.valueOf(current.mileage()) : "0");
        }

        if (!show(parent, current == null ? "Nova movimentacao" : "Editar movimentacao",
                new String[]{"Veiculo", "Categoria", "Descricao", "Data (AAAA-MM-DD)", "Valor", "Quilometragem (km)"},
                new JComponent[]{vehicle, category, description, date, value, mileage})) {
            return null;
        }

        try {
            require(description.getText(), "Informe a descricao.");

            LocalDate  parsedDate    = LocalDate.parse(date.getText().trim());
            BigDecimal parsedValue   = new BigDecimal(value.getText().trim().replace(",", "."));

            // Quilometragem é opcional — se vazio ou inválido, usa 0.0
            double parsedMileage = 0.0;
            String mileageText = mileage.getText().trim();
            if (!mileageText.isEmpty() && !mileageText.equals("0")) {
                parsedMileage = Double.parseDouble(mileageText.replace(",", "."));
            }

            if (parsedValue.signum() <= 0) {
                throw new IllegalArgumentException("O valor deve ser maior que zero.");
            }

            return new MovementForm(
                    (Vehicle)     vehicle.getSelectedItem(),
                    (ExpenseType) category.getSelectedItem(),
                    description.getText().trim(),
                    parsedDate,
                    parsedValue,
                    parsedMileage
            );

        } catch (DateTimeParseException e) {
            error(parent, "Data invalida. Use o formato AAAA-MM-DD.");
            return movement(parent, vehicles, expenseTypes, current);
        } catch (NumberFormatException e) {
            error(parent, "Valor ou quilometragem invalido.");
            return movement(parent, vehicles, expenseTypes, current);
        } catch (IllegalArgumentException e) {
            error(parent, e.getMessage());
            return movement(parent, vehicles, expenseTypes, current);
        }
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "GynLog", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Atencao", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmacao",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static boolean show(Component parent, String title, String[] labels, JComponent[] fields) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(5, 5, 5, 5);
        c.anchor  = GridBagConstraints.WEST;
        c.fill    = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i; c.weightx = 0;
            form.add(new JLabel(labels[i] + ":"), c);
            c.gridx = 1; c.weightx = 1;
            form.add(fields[i], c);
        }
        return JOptionPane.showConfirmDialog(parent, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private static void require(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}