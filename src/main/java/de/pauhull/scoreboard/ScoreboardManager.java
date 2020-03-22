package de.pauhull.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul
 * on 04.12.2018
 *
 * @author pauhull
 */
public class ScoreboardManager {

    @Getter
    private static ScoreboardManager instance;

    @Getter
    private Class<? extends CustomScoreboard> scoreboardClass;

    @Getter
    private Map<Player, CustomScoreboard> scoreboards;

    @Getter
    private JavaPlugin plugin;

    public ScoreboardManager(JavaPlugin plugin, Class<? extends CustomScoreboard> scoreboardClass) {
        instance = this;
        this.scoreboardClass = scoreboardClass;
        this.scoreboards = new HashMap<>();
        this.plugin = plugin;

        new ScoreboardListener(this, plugin);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (CustomScoreboard scoreboard : scoreboards.values()) {
                scoreboard.update();
            }
        }, 20, 20);
    }

    public void updateTitle(String title) {
        for (CustomScoreboard scoreboard : scoreboards.values()) {
            scoreboard.updateTitle(title);
        }
    }

    public void updateTeam(Player player) {
        for (CustomScoreboard scoreboard : scoreboards.values()) {
            scoreboard.updateTeam(player);
        }
    }

    public void setScoreboard(Class<? extends CustomScoreboard> scoreboardClass) {
        try {
            Map<Player, CustomScoreboard> newScoreboards = new HashMap<>();
            for (Player player : scoreboards.keySet()) {
                scoreboards.get(player).delete();
                newScoreboards.put(player, scoreboardClass.getConstructor(Player.class).newInstance(player));
                newScoreboards.get(player).show();
            }
            scoreboards = newScoreboards;
            this.scoreboardClass = scoreboardClass;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
