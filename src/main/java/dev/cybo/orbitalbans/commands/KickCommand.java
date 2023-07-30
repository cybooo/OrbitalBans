package dev.cybo.orbitalbans.commands;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private final OrbitalBans plugin;

    public KickCommand(OrbitalBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }
        if (args.length == 0 || args.length == 1) {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.kick.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.kick.example")));
            return false;
        } else if (args.length == 2) {
            String target = args[0];
            String reason = String.join(" ", args).replace(target, "").trim();

            // check if online
            if (Bukkit.getPlayer(target) == null || !Bukkit.getPlayer(target).isOnline()) {
                player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.kick.player-not-found").replace("{PLAYER}", target)));
                return true;
            }

            plugin.getPunishmentManager().issuePunishment(PunishmentType.KICK, target, player.getName(), 0L, reason);
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.kick.success").replace("{PLAYER}", target)));

        }
        return true;
    }
}
