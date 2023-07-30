package dev.cybo.orbitalbans.manager;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.objects.Punishment;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.entity.Player;

public class PunishmentManager {

    private final OrbitalBans plugin;

    public PunishmentManager(OrbitalBans plugin) {
        this.plugin = plugin;
    }

    public Punishment issuePunishment(PunishmentType punishmentType, String player, String administrator, long ends, String reason) {
        Punishment punishment = new Punishment.Builder(punishmentType)
                .playerName(player)
                .administrator(administrator)
                .createdAt(System.currentTimeMillis())
                .ends(ends)
                .reason(reason)
                .build();
        plugin.getPunishmentStorage().cachePunishment(player, punishment);

        switch (punishmentType) {
            case BAN ->
                    plugin.getMariaDB().executeQueryAsync("INSERT INTO `bans` (`player_name`, `administrator`, `created_at`, `expires_at`, `reason`) VALUES (?, ?, ?, ?, ?)",
                            player, administrator, System.currentTimeMillis(), ends, reason);
            case BLACKLIST ->
                    plugin.getMariaDB().executeQueryAsync("INSERT INTO `blacklists` (`player_name`, `administrator`, `created_at`, `reason`) VALUES (?, ?, ?, ?)",
                            player, administrator, System.currentTimeMillis(), reason);
            case MUTE ->
                    plugin.getMariaDB().executeQueryAsync("INSERT INTO `mutes` (`player_name`, `administrator`, `created_at`, `expires_at`, `reason`) VALUES (?, ?, ?, ?, ?)",
                            player, administrator, System.currentTimeMillis(), ends, reason);
            case KICK ->
                    plugin.getMariaDB().executeQueryAsync("INSERT INTO `kicks` (`player_name`, `administrator`, `created_at`, `reason`) VALUES (?, ?, ?, ?)",
                            player, administrator, System.currentTimeMillis(), reason);
        }

        Player target = plugin.getServer().getPlayer(player);
        if (target != null) {
            switch (punishmentType) {
                case BAN ->
                        target.kickPlayer(FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.banned")
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator())
                                .replace("{ENDS}", FormatUtils.formatRemainingTime(punishment.getEnds() - System.currentTimeMillis()))).replace("\\n", "\n"));
                case BLACKLIST ->
                        target.kickPlayer(FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.blacklisted")
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator())).replace("\\n", "\n"));
                case MUTE ->
                        target.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.muted"))
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator()).replace("\\n", "\n")
                                .replace("{ENDS}", FormatUtils.formatRemainingTime(punishment.getEnds() - System.currentTimeMillis())));
                case KICK ->
                        target.kickPlayer(FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.kicked"))
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator()).replace("\\n", "\n"));
            }
        }

        return punishment;
    }

    public void removeActivePunishment(String playerName, PunishmentType punishmentType) {
        plugin.getPunishmentStorage().getPunishments(playerName).stream()
                .filter(punishment -> punishment.getPunishmentType() == punishmentType)
                .filter(Punishment::isActive)
                .forEach(punishment -> {
                    plugin.getMariaDB().executeQueryAsync("UPDATE " + punishmentType.getDatabaseTable() + " SET `expires_at` = ? WHERE `player_name` = ? AND `expires_at` = ?",
                            System.currentTimeMillis(), playerName, punishment.getEnds());
                    punishment.setEnds(System.currentTimeMillis());
                });
    }

}
