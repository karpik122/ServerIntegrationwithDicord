package pl.karpik122.ServerIntegrationwithDicord.Timer;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

public class Counter extends TimerTask {
    private final Main plugin;
    private final LanguageLoader languageLoader;
    public Counter(Main pl) {
        this.plugin = pl;
        languageLoader = LanguageManager.getInstance();
    }


    @SneakyThrows
    public void run() {
        String Save_game_time = languageLoader.getTranslation("Save_game_time");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + Save_game_time);
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
