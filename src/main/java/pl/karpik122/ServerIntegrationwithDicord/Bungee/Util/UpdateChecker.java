package pl.karpik122.ServerIntegrationwithDicord.Bungee.Util;

import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final MainBungee plugin;
    private final int resourceId;
    private final LanguageLoader languageLoader;


    public UpdateChecker(MainBungee plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.languageLoader = LanguageManager.getInstance();
    }

    public void getVersion(final Consumer<String> consumer) {
        String unable_to_check_for_update = languageLoader.getTranslation("unable_to_check_for_update");
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info(unable_to_check_for_update + " " + e.getMessage());
            }
        });
    }
}
