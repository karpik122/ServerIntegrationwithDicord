package pl.karpik122.ServerIntegrationwithDicord.Util;

import org.bukkit.Bukkit;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final Main plugin;
    private final int resourceId;
    private final LanguageLoader languageLoader;


    public UpdateChecker(Main plugin, int resourceId) {
        this.plugin = plugin;
        languageLoader = LanguageManager.getInstance();
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        String unable_to_check_for_update = languageLoader.getTranslation("unable_to_check_for_update");
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
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