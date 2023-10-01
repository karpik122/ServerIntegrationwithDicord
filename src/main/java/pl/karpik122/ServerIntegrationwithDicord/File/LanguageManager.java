package pl.karpik122.ServerIntegrationwithDicord.File;



public class LanguageManager {

    private static LanguageLoader instance;

    public static void init(LanguageLoader languageLoader) {
        if (instance == null) {
            instance = languageLoader;
        }
    }
    public static LanguageLoader getInstance() {
        return instance;
    }
}
