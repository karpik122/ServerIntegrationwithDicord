package pl.karpik122.serverIntegrationwithDicord.Spigot.File;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Economy {
    public final MainSpigot plugin;
    private static File file;

    public Economy(MainSpigot plugin) {
        this.plugin = plugin;

        File folder = new File(plugin.getDataFolder(), "link");
        if(!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(folder, "players.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setPlayersLink(UUID uuid, String discordID) {
        // Zapisuje do pliku gracze.yml
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(uuid.toString(), discordID);
        try {
            config.save(file);
        } catch (IOException e) {
            return;
        }
    }


    public static UUID getUid(String discordID) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Przeszukuje wszystkie zapisane klucze (czyli UUID) w pliku
        for (String key : config.getKeys(false)) {
            // Jeśli ID z Discorda się zgadza, zwraca UUID jako wynik
            if (discordID.equals(config.getString(key))) {
                return UUID.fromString(key);
            }
        }

        // Jeśli nie znajdzie konta, nie zwraca niczego
        return null;
    }
}
