package pl.karpik122.serverIntegrationwithDicord.Spigot.File;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Economy {
    private static final Object LOCK = new Object();
    private static MainSpigot plugin;
    private static File file;

    public Economy(MainSpigot plugin) {
        Economy.plugin = plugin;

        File folder = new File(plugin.getDataFolder(), "link");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().warning("Could not create linked-account directory: " + folder);
        }

        file = new File(folder, "players.yml");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    plugin.getLogger().warning("Could not create linked-account file: " + file);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create linked-account file: " + e.getMessage());
            }
        }
    }

    public static boolean setPlayersLink(UUID uuid, String discordID) {
        if (!isInitialized() || uuid == null || discordID == null || discordID.isBlank()) {
            return false;
        }

        synchronized (LOCK) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String key : config.getKeys(false)) {
                if (discordID.equals(config.getString(key)) && !uuid.toString().equals(key)) {
                    config.set(key, null);
                }
            }

            config.set(uuid.toString(), discordID);
            try {
                config.save(file);
                return true;
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save linked accounts: " + e.getMessage());
                return false;
            }
        }
    }

    public static UUID getUid(String discordID) {
        if (!isInitialized() || discordID == null) {
            return null;
        }

        synchronized (LOCK) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                if (discordID.equals(config.getString(key))) {
                    try {
                        return UUID.fromString(key);
                    } catch (IllegalArgumentException exception) {
                        plugin.getLogger().warning("Invalid UUID in linked-account file: " + key);
                    }
                }
            }
        }
        return null;
    }

    private static boolean isInitialized() {
        return plugin != null && file != null;
    }
}
