package br.com.gynlog.model;

public record ExpenseType(int id, String name, String description) {
    @Override
    public String toString() {
        return name;
    }
}
