package pl.karpik122.ServerIntegrationwithDicord.Bungee.File;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;

public class LanguageLoader {
    private HashMap<String, String> translationMap;
    private Configuration translations;

    public LanguageLoader(MainBungee plugin) throws IOException {
        translationMap = new HashMap<>();
        String locale = Config.getInstance(plugin).getConfig().getString("language");

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File languageFile = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        if (!languageFile.exists()) {

            FileOutputStream outputStream = new FileOutputStream(languageFile);
            InputStream in = plugin.getResourceAsStream("lang/" + locale + ".yml");
            in.transferTo(outputStream);
        }

        translations = ConfigurationProvider.getProvider(YamlConfiguration.class).load(languageFile);
        for (String translationKey : translations.getKeys()) {
            String translationValue = translations.getString(translationKey);
            translationMap.put(translationKey, translationValue);
        }
    }

    public String getTranslation(String key) {
        return translationMap.getOrDefault(key, key);
    }
}