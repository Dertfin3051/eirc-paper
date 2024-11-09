package ru.dfhub.eirc.eirc_paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dfhub.eirc.eirc_paper.server.Server;

import java.io.IOException;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;
    private static Server server;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();

        try {
            server = new Server(getConfig().getInt("port"));
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

    }

    @Override
    public void onDisable() {

    }

    public static Server getEircServer() {
        return server;
    }

    public static Main getInstance() {
        return INSTANCE;
    }


}
