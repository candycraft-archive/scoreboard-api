package de.pauhull.scoreboard;

import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.spigot.SpigotFriends;
import lombok.Getter;
import net.mcstats2.bridge.server.bukkit.MCPerms;
import net.mcstats2.permissions.manager.data.MCSGroupData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Created by Paul
 * on 04.12.2018
 *
 * @author pauhull
 */
public abstract class CustomScoreboard {

    protected Player player;
    protected Scoreboard scoreboard;
    protected Objective objective;
    protected String objectiveName;
    protected int nextScoreID = 0;
    protected int nextSpacerLength = 0;
    protected boolean descending = true;

    public CustomScoreboard(Player player, String objectiveName, String title) {
        if (objectiveName.length() >= 16) {
            objectiveName = objectiveName.substring(0, 16);
        }

        this.player = player;
        this.objectiveName = objectiveName;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.getObjective(objectiveName) != null ? scoreboard.getObjective(objectiveName) : scoreboard.registerNewObjective(objectiveName, "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.updateTitle(title);
    }

    public void show() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTeam(player);
        }

        player.setScoreboard(scoreboard);
    }

    public void delete() {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    public void update() {
    }

    protected void updateTitle(String title) {
        objective.setDisplayName(title);
    }

    public void updateTeam(Player player) {
        if (Bukkit.getServerName().equals("Gingerbread")) {
            Bukkit.getScheduler().runTask(ScoreboardManager.getInstance().getPlugin(), () -> {
                String prefix, suffix, rank;
                if (player.getDisplayName().equals(player.getName())) {
                    MCSGroupData group = MCPerms.getInstance().getManager().getHighestGroup(player.getUniqueId());
                    rank = group.tagID + "";
                    prefix = group.prefix;
                    suffix = group.suffix;
                } else {
                    rank = "65";
                    prefix = "§7";
                    suffix = "§7";
                }

                String teamName = rank + player.getName();

                if (teamName.length() > 16) {
                    teamName = teamName.substring(0, 16);
                }

                if (scoreboard.getTeam(teamName) != null) {
                    scoreboard.getTeam(teamName).unregister();
                }

                Team team = scoreboard.registerNewTeam(teamName);
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                team.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));

                team.addEntry(player.getName());
            });
        } else {
            SpigotFriends.getInstance().getPartyManager().getAllParties(parties -> {
                Bukkit.getScheduler().runTask(ScoreboardManager.getInstance().getPlugin(), () -> {
                    String prefix, suffix, rank;
                    if (player.getDisplayName().equals(player.getName())) {
                        MCSGroupData group = MCPerms.getInstance().getManager().getHighestGroup(player.getUniqueId());
                        rank = group.tagID + "";
                        prefix = group.prefix;
                        suffix = group.suffix;
                    } else {
                        rank = "65";
                        prefix = "§7";
                        suffix = "§7";
                    }

                    for (Party party : parties) {
                        if (party.getMembers().contains(player.getDisplayName()) && party.getMembers().contains(this.player.getDisplayName())) {
                            suffix += "§7 [§5Party§7]";
                        }
                    }

                    String teamName = rank + player.getName();

                    if (teamName.length() > 16) {
                        teamName = teamName.substring(0, 16);
                    }

                    if (scoreboard.getTeam(teamName) != null) {
                        scoreboard.getTeam(teamName).unregister();
                    }

                    Team team = scoreboard.registerNewTeam(teamName);
                    team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                    team.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));

                    team.addEntry(player.getName());
                });
            });
        }

    }

    private String getNewEmptyString() {
        StringBuilder name = new StringBuilder();
        final int spaces = ++nextSpacerLength;
        for (int i = 0; i < spaces; i++)
            name.append(" ");
        return name.toString();
    }

    public class DisplayScore {

        @Getter
        private Score score;

        public DisplayScore(Object name, int score) {
            this.score = objective.getScore(name.toString());
            this.score.setScore(score);
        }

        public DisplayScore(Object name) {
            this(name, descending ? nextScoreID-- : nextScoreID++);
        }

        public DisplayScore(int score) {
            this(getNewEmptyString(), score);
        }

        public DisplayScore() {
            this(getNewEmptyString());
        }

        public void setName(String name) {
            final int score = this.score.getScore();
            scoreboard.resetScores(this.score.getEntry());
            Score newScore = objective.getScore(name);
            newScore.setScore(score);
            this.score = newScore;
        }

        public void setScore(int score) {
            this.score.setScore(score);
        }

    }

}
