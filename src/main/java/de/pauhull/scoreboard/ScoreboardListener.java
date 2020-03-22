package de.pauhull.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class ScoreboardListener implements Listener {

    private ScoreboardManager scoreboardManager;

    public ScoreboardListener(ScoreboardManager scoreboardManager, JavaPlugin plugin) {
        this.scoreboardManager = scoreboardManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Player player = event.getPlayer();
        scoreboardManager.updateTeam(player);
        CustomScoreboard scoreboard = scoreboardManager.getScoreboardClass().getConstructor(Player.class).newInstance(player);
        scoreboard.show();
        scoreboardManager.getScoreboards().put(player, scoreboard);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        scoreboardManager.getScoreboards().get(player).delete();
        scoreboardManager.getScoreboards().remove(player);
    }

}
