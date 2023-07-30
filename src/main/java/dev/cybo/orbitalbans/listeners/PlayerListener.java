package dev.cybo.orbitalbans.listeners;

import dev.cybo.orbitalbans.OrbitalBans;
import dev.cybo.orbitalbans.enums.PunishmentType;
import dev.cybo.orbitalbans.utils.FormatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListener implements Listener {

    private final OrbitalBans plugin;

    public PlayerListener(OrbitalBans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();

        plugin.getPunishmentStorage().getPunishments(playerName).forEach(punishment -> {
            if (punishment.isActive()) {
                switch (punishment.getPunishmentType()) {
                    case BLACKLIST -> {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                                FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.blacklisted")
                                        .replace("{REASON}", punishment.getReason())
                                        .replace("{ADMINISTRATOR}", punishment.getAdministrator())).replace("\\n", "\n")
                        );
                    }
                    case BAN -> {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                                FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.banned")
                                        .replace("{REASON}", punishment.getReason())
                                        .replace("{ADMINISTRATOR}", punishment.getAdministrator())
                                        .replace("{ENDS}", FormatUtils.formatRemainingTime(punishment.getEnds() - System.currentTimeMillis()))).replace("\\n", "\n")
                        );
                    }
                }
            }
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();

        plugin.getPunishmentStorage().getPunishments(playerName).forEach(punishment -> {
            if (punishment.isActive()) {
                if (punishment.getPunishmentType() == PunishmentType.MUTE) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(
                            FormatUtils.formatConfigString(plugin.getConfig().getString("punished-messages.muted")
                                    .replace("{REASON}", punishment.getReason())
                                    .replace("{ADMINISTRATOR}", punishment.getAdministrator())
                                    .replace("{ENDS}", FormatUtils.formatRemainingTime(punishment.getEnds() - System.currentTimeMillis())))
                    );
                }
            }
        });
    }

}
