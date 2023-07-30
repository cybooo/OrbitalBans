package dev.cybo.orbitalbans.commands;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

    private final OrbitalBans plugin;

    public MuteCommand(OrbitalBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }
        if (args.length == 0 || args.length == 1) {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.mute.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.mute.example")));
            return false;
        } else if (args.length == 3) {
            String target = args[0];
            String duration = args[1];
            String reason = String.join(" ", args).replace(target, "").replace(duration, "").trim();

            if (plugin.getPunishmentStorage().hasActivePunishment(target, PunishmentType.MUTE)) {
                player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.mute.already-muted").replace("{PLAYER}", target)));
                return true;
            }

            long ends = System.currentTimeMillis() + FormatUtils.convertToMillis(duration);
            plugin.getPunishmentManager().issuePunishment(PunishmentType.MUTE, target, player.getName(), ends, reason);
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.mute.success").replace("{PLAYER}", target)));

        }
        return true;
    }

}
