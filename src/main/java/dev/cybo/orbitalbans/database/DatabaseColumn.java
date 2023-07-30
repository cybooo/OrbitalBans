package dev.cybo.orbitalbans.database;

public record DatabaseColumn(String name, String type) {

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }
}
