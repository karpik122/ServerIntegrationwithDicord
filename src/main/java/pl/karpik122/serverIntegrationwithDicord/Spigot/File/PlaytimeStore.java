package pl.karpik122.serverIntegrationwithDicord.Spigot.File;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

public final class PlaytimeStore {
    private final MainSpigot plugin;
    private final File file;
    private final Object lock = new Object();

    public PlaytimeStore(MainSpigot plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playtime.yml");
        ensureFileExists();
    }

    public void incrementPlayers(Collection<String> playerNames) {
        if (playerNames.isEmpty()) {
            return;
        }

        synchronized (lock) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            for (String playerName : playerNames) {
                data.set(playerName, data.getInt(playerName, 0) + 1);
            }
            save(data);
        }
    }

    public OptionalInt getMinutes(String requestedName) {
        synchronized (lock) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            for (String storedName : data.getKeys(false)) {
                if (storedName.equalsIgnoreCase(requestedName)) {
                    return OptionalInt.of(Math.max(0, data.getInt(storedName, 0)));
                }
            }
            return OptionalInt.empty();
        }
    }

    public List<PlayerPlaytime> getTopPlayers(int limit) {
        synchronized (lock) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            List<PlayerPlaytime> result = new ArrayList<>();
            for (String playerName : data.getKeys(false)) {
                result.add(new PlayerPlaytime(playerName, Math.max(0, data.getInt(playerName, 0))));
            }
            result.sort(Comparator.comparingInt(PlayerPlaytime::minutes).reversed());
            return List.copyOf(result.subList(0, Math.min(Math.max(limit, 0), result.size())));
        }
    }

    private void ensureFileExists() {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data directory: " + parent);
        }
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    plugin.getLogger().warning("Could not create playtime.yml");
                }
            } catch (IOException exception) {
                plugin.getLogger().severe("Could not create playtime.yml: " + exception.getMessage());
            }
        }
    }

    private void save(YamlConfiguration data) {
        try {
            data.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save playtime.yml: " + exception.getMessage());
        }
    }

    public record PlayerPlaytime(String playerName, int minutes) {
    }
}
