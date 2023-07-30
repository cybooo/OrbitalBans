package dev.cybo.orbitalbans.commands;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {

    private final OrbitalBans plugin;

    public BanCommand(OrbitalBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }
        if (args.length == 0 || args.length == 1 || args.length == 2) {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.ban.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.ban.example")));
            return false;
        } else if (args.length == 3) {
            String target = args[0];
            String duration = args[1];
            String reason = String.join(" ", args).replace(target, "").replace(duration, "").trim();

            if (plugin.getPunishmentStorage().hasActivePunishment(target, PunishmentType.BAN)) {
                player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.ban.already-banned").replace("{PLAYER}", target)));
                return true;
            }

            long ends = System.currentTimeMillis() + FormatUtils.convertToMillis(duration);
            plugin.getPunishmentManager().issuePunishment(PunishmentType.BAN, target, player.getName(), ends, reason);
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.ban.success").replace("{PLAYER}", target)));

        }
        return true;
    }
}
