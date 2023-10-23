package pl.karpik122.ServerIntegrationwithDicord.Bungee.File;

import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;

import java.io.IOException;

public class bungeeLanguageManager {
    private static bungeeLanguageLoader instance;

    public static void initialize(MainBungee plugin) {
        if (instance == null) {
            try {
                instance = new bungeeLanguageLoader(plugin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static bungeeLanguageLoader getInstance() {
        return instance;
    }
}
