package ru.dfhub.eirc.eirc_paper;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dfhub.eirc.eirc_paper.client.Encryption;
import ru.dfhub.eirc.eirc_paper.server.GameMessageHandler;
import ru.dfhub.eirc.eirc_paper.server.GameSessionHandler;
import ru.dfhub.eirc.eirc_paper.server.Server;

import java.io.IOException;

import static org.bukkit.Bukkit.getPluginManager;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;
    private static Server server;
    private static Encryption encryption;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();

        try {
            server = new Server(getConfig().getInt("port"));
        } catch (IOException e) {
            e.printStackTrace();
            getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Encryption.initKey();
        } catch (Encryption.EncryptionException e) {
            Encryption.generateNewKeyFile();
            return;
        }

        try {
            Encryption.initEncryption();
        } catch (Exception e) {
            e.printStackTrace();
            getPluginManager().disablePlugin(this);
            return;
        }

        getPluginManager().registerEvents(new GameMessageHandler(), this);
        getPluginManager().registerEvents(new GameSessionHandler(), this);
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

    public static void showInGameMessage(Component component) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(component));
    }
}
