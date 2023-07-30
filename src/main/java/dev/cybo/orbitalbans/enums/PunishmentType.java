package dev.cybo.orbitalbans.enums;

import dev.cybo.orbitalbans.database.DatabaseColumn;

public enum PunishmentType {

    BAN("bans", true, new DatabaseColumn[]{
            new DatabaseColumn("player_name", "VARCHAR(255)"),
            new DatabaseColumn("administrator", "VARCHAR(255)"),
            new DatabaseColumn("created_at", "BIGINT"),
            new DatabaseColumn("expires_at", "BIGINT"),
            new DatabaseColumn("reason", "VARCHAR(255)")
    }),

    MUTE("mutes", true, new DatabaseColumn[]{
            new DatabaseColumn("player_name", "VARCHAR(255)"),
            new DatabaseColumn("administrator", "VARCHAR(255)"),
            new DatabaseColumn("created_at", "BIGINT"),
            new DatabaseColumn("expires_at", "BIGINT"),
            new DatabaseColumn("reason", "VARCHAR(255)")
    }),

    KICK("kicks", false, new DatabaseColumn[]{
            new DatabaseColumn("player_name", "VARCHAR(255)"),
            new DatabaseColumn("administrator", "VARCHAR(255)"),
            new DatabaseColumn("created_at", "BIGINT"),
            new DatabaseColumn("reason", "VARCHAR(255)")
    }),

    BLACKLIST("blacklists", false, new DatabaseColumn[]{
            new DatabaseColumn("player_name", "VARCHAR(255)"),
            new DatabaseColumn("administrator", "VARCHAR(255)"),
            new DatabaseColumn("created_at", "BIGINT"),
            new DatabaseColumn("reason", "VARCHAR(255)")
    });

    private final String databaseTable;
    private final boolean expireable;
    private final DatabaseColumn[] databaseColumns;

    PunishmentType(String databaseTable, boolean expireable, DatabaseColumn[] databaseColumns) {
        this.databaseTable = databaseTable;
        this.expireable = expireable;
        this.databaseColumns = databaseColumns;
    }

    public String getDatabaseTable() {
        return databaseTable;
    }

    public boolean isExpireable() {
        return expireable;
    }

    public DatabaseColumn[] getDatabaseColumns() {
        return databaseColumns;
    }
}