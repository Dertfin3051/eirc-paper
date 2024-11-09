package ru.dfhub.eirc.eirc_paper.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.dfhub.eirc.eirc_paper.client.DataParser;

public class GameSessionHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        DataParser.handleOutputSession(true, e.getPlayer().getName());
    }

    @EventHandler
    public void onLeave(PlayerJoinEvent e) {
        DataParser.handleOutputSession(false, e.getPlayer().getName());
    }
}
