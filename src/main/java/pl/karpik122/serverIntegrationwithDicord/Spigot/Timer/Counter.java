package pl.karpik122.serverIntegrationwithDicord.Spigot.Timer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

public class Counter extends TimerTask {
    private final MainSpigot plugin;

    public Counter(MainSpigot pl) {
        this.plugin = pl;
    }

    public void run() {
        File f = new File(plugin.getDataFolder(), "playtime.yml");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        YamlConfiguration file = YamlConfiguration.loadConfiguration(f);

        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();

            // Pobiera obecny czas lub 0, jeśli gracza jeszcze nie ma w pliku
            int currentTime = file.getInt(name, 0);

            // Zapisuje samą liczbę (bez Optional)
            file.set(name, currentTime + 1);
        }

        // Zapisuje plik tylko raz na koniec pętli
        try {
            file.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
