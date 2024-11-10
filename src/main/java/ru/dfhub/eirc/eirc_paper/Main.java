package ru.dfhub.eirc.eirc_paper;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dfhub.eirc.eirc_paper.client.Encryption;
import ru.dfhub.eirc.eirc_paper.server.GameMessageHandler;
import ru.dfhub.eirc.eirc_paper.server.GameSessionHandler;
import ru.dfhub.eirc.eirc_paper.server.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getPluginManager;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;
    private static Server server;
    private static Encryption encryption;
    private static Logger logger;

    @Override
    public void onEnable() {
        INSTANCE = this;
        logger = getLogger();
        saveDefaultConfig();

        try {
            server = new Server(getConfig().getInt("port"));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Error occurred while initializing EnigmaIRC server! ", e);
            getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Encryption.initKey();
        } catch (Encryption.EncryptionException e) {
            getLogger().log(Level.INFO, "Security key is empty or invalid! Generating new one...");
            Encryption.generateNewKeyFile();
            return;
        }

        try {
            Encryption.initEncryption();
        } catch (Exception e) {
            getLogger().log(Level.INFO, "Security key is invalid! Please, generate new one starting plugin with empty key...");
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

    public static Logger logger() {
        return logger;
    }
}
