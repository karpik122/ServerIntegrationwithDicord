package pl.karpik122.ServerIntegrationwithDicord.Bungee.Timer;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;

public class bungeeCounter extends TimerTask {
    private final Plugin plugin;

    public bungeeCounter(Plugin pl) {
        this.plugin = pl;
    }

    @Override
    public void run() {
        File f = new File(plugin.getDataFolder(), "playtime.yml");

        // Sprawdź, czy plik istnieje; jeśli nie, utwórz nowy plik
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Configuration file;

        try {
            file = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ProxiedPlayer> players = new ArrayList<>(plugin.getProxy().getPlayers());

        for (ProxiedPlayer player : players) {
            String playerName = player.getName();
            int playtime = file.getInt(playerName, 0);
            file.set(playerName, Optional.of(playtime + 1));
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(file, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}