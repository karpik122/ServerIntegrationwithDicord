package pl.karpik122.serverIntegrationwithDicord.Spigot.Util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;


    public UpdateChecker(MainSpigot plugin) {
        this.plugin = plugin;
        languageLoader = LanguageManager.getInstance();

    }

    public void getVersion(final Consumer<String> consumer) {
        String unable_to_check_for_update = languageLoader.getTranslation("unable_to_check_for_update");

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL("https://api.cfwidget.com/minecraft/bukkit-plugins/server-integration-with-discord");

                try (InputStream is = url.openStream(); Scanner scann = new Scanner(is)) {
                    StringBuilder jsonText = new StringBuilder();
                    while (scann.hasNext()) {
                        jsonText.append(scann.nextLine());
                    }

                    // Odczytanie pełnej nazwy z CurseForge
                    JsonObject json = JsonParser.parseString(jsonText.toString()).getAsJsonObject();
                    String rawVersion = json.getAsJsonObject("download").get("version").getAsString();

                    // Wyciąganie samego numeru wersji (np. z "Plugin-1.1.jar" robi "1.1")
                    Matcher matcher = Pattern.compile("(\\d+\\.\\d+(?:\\.\\d+)?)").matcher(rawVersion);
                    String latestVersion = rawVersion;
                    if (matcher.find()) {
                        latestVersion = matcher.group(1);
                    }

                    consumer.accept(latestVersion);
                }
            } catch (Exception e) {
                plugin.getLogger().info(unable_to_check_for_update + " " + e.getMessage());
            }
        });
    }
}