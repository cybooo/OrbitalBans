package dev.cybo.orbitalbans.database.local;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.database.DatabaseColumn;
import dev.cybo.orbitalbans.database.DatabaseRow;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.objects.Punishment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PunishmentStorage {

    private final OrbitalBans plugin;
    private final Map<String, List<Punishment>> punishments;

    public PunishmentStorage(OrbitalBans plugin) {
        this.plugin = plugin;
        this.punishments = new ConcurrentHashMap<>();
    }

    public void cachePunishments(String playerName, PunishmentType punishmentType) {
        plugin.getLogger().info("Caching punishments for " + playerName + "...");
        plugin.getMariaDB().executeQueryAsync("SELECT * FROM " + punishmentType.getDatabaseTable() + " WHERE player_name = ?", playerName)
                .thenAccept(rows -> rows.forEach(databaseRow -> createPunishmentFromRow(punishmentType, databaseRow)));
    }


    public void cacheAllActivePunishments() {
        plugin.getLogger().info("Caching all active punishments...");
        for (PunishmentType punishmentType : PunishmentType.values()) {
            if (punishmentType.isExpireable()) {
                plugin.getMariaDB().executeQueryAsync("SELECT * FROM " + punishmentType.getDatabaseTable() + " WHERE expires_at > ?", System.currentTimeMillis())
                        .thenAccept(rows -> rows.forEach(databaseRow -> createPunishmentFromRow(punishmentType, databaseRow)));
            }
        }
    }

    private void createPunishmentFromRow(PunishmentType punishmentType, DatabaseRow databaseRow) {
        Punishment.Builder punishmentBuilder = new Punishment.Builder(punishmentType);
        for (DatabaseColumn databaseColumn : punishmentType.getDatabaseColumns()) {
            String columnName = databaseColumn.name();
            switch (columnName) {
                case "player_name" -> punishmentBuilder.playerName(databaseRow.getString(columnName));
                case "administrator" -> punishmentBuilder.administrator(databaseRow.getString(columnName));
                case "created_at" -> punishmentBuilder.createdAt(databaseRow.getLong(columnName));
                case "expires_at" -> punishmentBuilder.ends(databaseRow.getLong(columnName));
                case "reason" -> punishmentBuilder.reason(databaseRow.getString(columnName));
            }
        }
        cachePunishment(databaseRow.getString("player_name"), punishmentBuilder.build());
    }

    public void cachePunishment(String playerName, Punishment punishment) {
        punishments.computeIfAbsent(playerName, k -> new ArrayList<>()).add(punishment);
    }

    public boolean hasActivePunishment(String playerName, PunishmentType punishmentType) {
        return punishments.getOrDefault(playerName, Collections.emptyList()).stream().anyMatch(punishment -> punishment.getPunishmentType() == punishmentType && punishment.isActive());
    }

    public List<Punishment> getPunishments(String playerName) {
        return punishments.getOrDefault(playerName, Collections.emptyList());
    }
}