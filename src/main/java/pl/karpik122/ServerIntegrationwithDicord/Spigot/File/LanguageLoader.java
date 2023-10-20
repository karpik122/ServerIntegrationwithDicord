package pl.karpik122.ServerIntegrationwithDicord.Spigot.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.util.HashMap;

public class LanguageLoader {
    private final HashMap<String, String> translationMap;

    public LanguageLoader(MainSpigot plugin) {
        translationMap = new HashMap<>();
        String locale = plugin.getConfig().getString("language", "en-US");

        File languageFile = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        if (!languageFile.exists()) {
            plugin.saveResource("lang/" + locale + ".yml", false);
        }

        FileConfiguration translations = YamlConfiguration.loadConfiguration(languageFile);
        for (String translationKey : translations.getKeys(false)) {
            String translationValue = translations.getString(translationKey);
            translationMap.put(translationKey, translationValue);
        }
    }

    public String getTranslation(String key) {
        return translationMap.getOrDefault(key, key);
    }
}