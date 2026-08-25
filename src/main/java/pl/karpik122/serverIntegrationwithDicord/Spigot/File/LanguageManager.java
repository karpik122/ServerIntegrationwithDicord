package pl.karpik122.serverIntegrationwithDicord.Spigot.File;

public class LanguageManager {
    private static volatile LanguageLoader instance;

    public static void init(LanguageLoader languageLoader) {
        instance = languageLoader;
    }

    public static LanguageLoader getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LanguageManager has not been initialized");
        }
        return instance;
    }
}
