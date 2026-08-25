package pl.karpik122.serverIntegrationwithDicord.Spigot.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LanguageLoader {
    // Przechowuje język -> (klucz -> wartość)
    private volatile Map<String, Map<String, String>> allTranslations = Map.of();
    private final MainSpigot plugin;

    public LanguageLoader(MainSpigot plugin) {
        this.plugin = plugin;
        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        if (!new File(langFolder, "en-US.yml").exists()) {
            plugin.saveResource("lang/en-US.yml", false);
        }
        if (!new File(langFolder, "pl-PL.yml").exists()) {
            plugin.saveResource("lang/pl-PL.yml", false);
        }

        load();
    }

    // Zwraca tekst dla wybranego języka i klucza
    public String getTranslation(String key) {
        String selectedLanguage = plugin.getConfig().getString("language", "en-US");
        Map<String, String> langMap = allTranslations.get(selectedLanguage);
        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }

        Map<String, String> fallback = allTranslations.get("en-US");
        if (fallback != null && fallback.containsKey(key)) {
            return fallback.get(key);
        }

        plugin.debugLog("Missing translation key: " + key + " (language: " + selectedLanguage + ")");
        return key;
    }

    private void load() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        Map<String, Map<String, String>> loadedTranslations = new HashMap<>();

        loadBundledLanguage(loadedTranslations, "en-US");
        loadBundledLanguage(loadedTranslations, "pl-PL");

        if (files != null) {
            for (File file : files) {
                String langName = file.getName().replace(".yml", "");
                Map<String, String> langMap = new HashMap<>(
                        loadedTranslations.getOrDefault(langName, Map.of())
                );

                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (String key : config.getKeys(false)) {
                    String value = config.getString(key);
                    if (value != null) {
                        langMap.put(key, value);
                    }
                }

                loadedTranslations.put(langName, Map.copyOf(langMap));
            }
        }
        allTranslations = Map.copyOf(loadedTranslations);
    }

    private void loadBundledLanguage(Map<String, Map<String, String>> target, String language) {
        try (InputStream stream = plugin.getResource("lang/" + language + ".yml")) {
            if (stream == null) {
                plugin.getLogger().warning("Bundled language is missing: " + language);
                return;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8)
            );
            Map<String, String> values = new HashMap<>();
            for (String key : config.getKeys(false)) {
                String value = config.getString(key);
                if (value != null) {
                    values.put(key, value);
                }
            }
            target.put(language, values);
        } catch (Exception exception) {
            plugin.getLogger().warning("Could not load bundled language " + language + ": "
                    + exception.getMessage());
        }
    }

    public void reload() {
        load();
    }
}
