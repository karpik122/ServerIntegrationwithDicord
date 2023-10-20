package pl.karpik122.ServerIntegrationwithDicord.Bungee.File;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Config {
    private static Config instance;
    private final Configuration config;

    private Config(MainBungee pl) throws IOException {
        // Wczytywanie konfiguracji tutaj
        if (!pl.getDataFolder().exists()) {
            pl.getLogger().info("Created config folder: " + pl.getDataFolder().mkdir());
        }

        File configFile = new File(pl.getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = pl.getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

    }

    public static Config getInstance(MainBungee pl) throws IOException {
        if (instance == null) {
            instance = new Config(pl);
        }
        return instance;
    }

    public Configuration getConfig() {
        return config;
    }
}