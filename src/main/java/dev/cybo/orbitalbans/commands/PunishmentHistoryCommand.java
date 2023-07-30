package dev.cybo.orbitalbans.commands;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.objects.Punishment;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PunishmentHistoryCommand implements CommandExecutor {

    private final OrbitalBans plugin;

    public PunishmentHistoryCommand(OrbitalBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishmenthistory.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishmenthistory.example")));
            return false;
        } else if (args.length == 1) {
            List<Punishment> punishments = plugin.getPunishmentStorage().getPunishments(player.getName());
            if (punishments == null || punishments.isEmpty()) {
                player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishment-history.caching")));
                for (PunishmentType punishmentType : PunishmentType.values()) {
                    plugin.getPunishmentStorage().cachePunishments(player.getName(), punishmentType);
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> sendPunishments(player, player.getName()), 25L);
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, () -> sendPunishments(player, player.getName()), 25L);
            }
        } else {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishmen-thistory.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishmen-thistory.example")));
        }
        return true;
    }

    public void sendPunishments(Player player, String target) {
        player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishment-history.punishments-for").replace("{PLAYER}", target)));
        plugin.getPunishmentStorage().getPunishments(target).forEach(punishment -> {
            switch (punishment.getPunishmentType()) {
                case BAN ->
                        player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishment-history.ban")
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator())
                                .replace("{ISSUED}", FormatUtils.formatIntoDate(punishment.getCreatedAt()))
                                .replace("{ENDS}", punishment.getEnds() == 0 ? "Never" : FormatUtils.formatIntoDate(punishment.getEnds()))
                                .replace("{ACTIVE}", String.valueOf(punishment.isActive()))));
                case KICK ->
                        player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishment-history.kick")
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator())
                                .replace("{ISSUED}", FormatUtils.formatIntoDate(punishment.getCreatedAt()))
                                .replace("{ACTIVE}", String.valueOf(punishment.isActive()))));
                case MUTE ->
                        player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.punishment-history.mute")
                                .replace("{REASON}", punishment.getReason())
                                .replace("{ADMINISTRATOR}", punishment.getAdministrator())
                                .replace("{ISSUED}", FormatUtils.formatIntoDate(punishment.getCreatedAt()))
                                .replace("{ENDS}", FormatUtils.formatIntoDate(punishment.getEnds()))
                                .replace("{ACTIVE}", String.valueOf(punishment.isActive()))));
            }
        });
    }

}
