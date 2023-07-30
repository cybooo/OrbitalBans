package dev.cybo.orbitalbans.commands;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

    private final OrbitalBans plugin;

    public UnmuteCommand(OrbitalBans plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.unmute.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.unmute.example")));
        } else if (args.length == 1) {
            String target = args[0];
            if (plugin.getPunishmentStorage().hasActivePunishment(target, PunishmentType.MUTE)) {
                plugin.getPunishmentManager().removeActivePunishment(player.getName(), PunishmentType.MUTE);
                player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.unmute.success").replace("{PLAYER}", target)));
            } else {
                player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.unmute.not-muted").replace("{PLAYER}", target)));
            }
        } else {
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.unmute.usage")));
            player.sendMessage(FormatUtils.formatConfigString(plugin.getConfig().getString("commands.unmute.example")));
        }
        return true;
    }
}
