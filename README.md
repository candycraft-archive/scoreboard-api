## Scoreboard API
First add the repo and dependency:
```
<repository>
    <id>candycraft-repo</id>
    <url>https://repo.morx.me</url>
</repository>

<dependency>
    <groupId>de.pauhull</groupId>
    <artifactId>scoreboard-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

### How to use
First you need to setup the ScoreboardManager in the #onEnable() method.
```
private ScoreboardManager scoreboardManager;

@Override
public void onEnable() {
    this.scoreboardManager = new ScoreboardManager(this, MyScoreboard.class);
}
```

Now every player will automatically see the MyScoreboard scoreboard.

It can look something like this:

```
public class MyScoreboard extends CustomScoreboard {
    
    // Variables are for each player, if you want to make them global use static
    private DisplayScore world;
    private DisplayScore myNumber;
    private int number = 0;
    private long lastNumberUpdate = 0;
    
    public MyScoreboard(Player player) {
        super(player,              // The player who sees the scoreboard
            player.getName(),      // The scoreboard name (only internal)
            "§aMy §bScoreboard");  // The scoreboard title
    }
    
    @Override
    public void show() {
        
        new DisplayScore("Your name:"); // You don't need to set the score, it starts with 0 and decreases the more scores you add
        new DisplayScore(this.player.getName()); // You can still access the player
        new DisplayScore(); // Use no arguments to leave space
        this.myNumber = new DisplayScore("0"); // We will update this later
        new DisplayScore();
        new DisplayScore("Your world:");
        this.world = new DisplayScore(player.getWorld().getName());
        
        // Note: You can't have 2 scores with the same names, but you can add some invisible color codes at the end
        
        super.show(); // Don't remove this
    }
    
    @Override
    public void update() { // Gets called every second
        
        number++;
        number %= 10; // number goes from 0-9
        
        // This will update the score
        this.myScore.setName(Integer.toString(number));
        
        // Only update the score when it's neccessary so it doesn't flicker
        String newWorld = player.getWorld().getName();
        if(!world.getScore().getEntry().equals(newWorld)) {
            this.world.setName(newWorld);
        }
        
        // You don't need to call the super method here
    }
    
}
```

If you want to switch the scoreboard, call this on your ScoreboardManager:

```
this.scoreboardManager.setScoreboard(MySecondScoreboard.class);
```

This will change the scoreboard for all players.