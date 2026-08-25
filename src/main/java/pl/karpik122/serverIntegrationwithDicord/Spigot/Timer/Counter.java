package pl.karpik122.serverIntegrationwithDicord.Spigot.Timer;

import org.bukkit.Bukkit;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.PlaytimeStore;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.util.List;

public class Counter implements Runnable {
    private final MainSpigot plugin;
    private final PlaytimeStore playtimeStore;

    public Counter(MainSpigot pl, PlaytimeStore playtimeStore) {
        this.plugin = pl;
        this.playtimeStore = playtimeStore;
    }

    @Override
    public void run() {
        List<String> onlinePlayerNames = Bukkit.getOnlinePlayers().stream()
                .map(player -> player.getName())
                .toList();
        playtimeStore.incrementPlayers(onlinePlayerNames);
        plugin.debugLog("Updated playtime for " + onlinePlayerNames.size() + " online players");
    }
}
