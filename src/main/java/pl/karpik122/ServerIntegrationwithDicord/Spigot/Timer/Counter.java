package pl.karpik122.ServerIntegrationwithDicord.Spigot.Timer;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

public class Counter extends TimerTask {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;
    public Counter(MainSpigot pl) {
        this.plugin = pl;
        languageLoader = LanguageManager.getInstance();
    }


    @SneakyThrows
    public void run() {
        File f = new File(plugin.getDataFolder(), "playtime.yml");

        YamlConfiguration file = YamlConfiguration.loadConfiguration(f);
        List players = (List)Bukkit.getOnlinePlayers();

        if (f.exists()) {
            try {
                f.createNewFile();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (Object player : players) {
            if (file.get(((Player) player).getName()) == null) {
                file.set(((Player) player).getName(), 0);
            }

            file.save(f);
            file.set(((Player) player).getName(), file.getInt(((Player) player).getName()) + 1);
            file.save(f);
        }

        file.save(f);
    }

}
