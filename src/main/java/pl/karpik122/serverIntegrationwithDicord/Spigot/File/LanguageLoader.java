package pl.karpik122.serverIntegrationwithDicord.Spigot.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageLoader {
    // Przechowuje język -> (klucz -> wartość)
    private final Map<String, Map<String, String>> allTranslations;
    private final MainSpigot plugin;

    public LanguageLoader(MainSpigot plugin) {
        this.plugin = plugin;
        allTranslations = new HashMap<>();
        File langFolder = new File(plugin.getDataFolder(), "lang");

        // Tworzy folder, jeśli nie istnieje
        if (!langFolder.exists()) {
            langFolder.mkdirs();
            // Możesz tu wygenerować domyślny plik, jeśli folder jest pusty
            plugin.saveResource("lang/en-US.yml", false);
            plugin.saveResource("lang/pl-PL.yml", false);
        }

        // Pobiera wszystkie pliki z końcówką .yml z folderu
        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files != null) {
            for (File file : files) {
                // Pobiera nazwę pliku bez ".yml" (np. "en-US")
                String langName = file.getName().replace(".yml", "");
                Map<String, String> langMap = new HashMap<>();

                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (String key : config.getKeys(false)) {
                    langMap.put(key, config.getString(key));
                }

                // Zapisuje załadowany język do głównej listy
                allTranslations.put(langName, langMap);
            }
        }
    }

    // Zwraca tekst dla wybranego języka i klucza
    public String getTranslation(String key) {
        Map<String, String> langMap = allTranslations.get(plugin.getConfig().getString("language", "en-US"));
        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }
        return key;
    }
}