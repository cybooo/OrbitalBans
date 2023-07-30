package dev.cybo.orbitalbans;

import dev.cybo.orbitalbans.commands.*;
import dev.cybo.orbitalbans.database.MariaDB;
import dev.cybo.orbitalbans.database.local.PunishmentStorage;
import dev.cybo.orbitalbans.listeners.PlayerListener;
import dev.cybo.orbitalbans.manager.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class OrbitalBans extends JavaPlugin {

    private MariaDB mariaDB;
    private PunishmentManager punishmentManager;
    private PunishmentStorage punishmentStorage;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        mariaDB = new MariaDB(
                getConfig().getString("database.host"),
                getConfig().getInt("database.port"),
                getConfig().getString("database.username"),
                getConfig().getString("database.password"),
                getConfig().getString("database.database")
        );
        punishmentManager = new PunishmentManager(this);
        punishmentStorage = new PunishmentStorage(this);

        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("pban").setExecutor(new PermanentBanCommand(this));
        getCommand("history").setExecutor(new PunishmentHistoryCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Bukkit.getScheduler().runTaskLater(this, () -> punishmentStorage.cacheAllActivePunishments(), 20L);

    }

    public MariaDB getMariaDB() {
        return mariaDB;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public PunishmentStorage getPunishmentStorage() {
        return punishmentStorage;
    }

    public void runSync(Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }
}
