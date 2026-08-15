package pl.karpik122.serverIntegrationwithDicord.Spigot.dapenency;

import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class Dependency {
    public static void load(File file, MainSpigot plugin) throws Exception {
        File dependencyFolder = new File(plugin.getDataFolder(), "dependency");

        if (!dependencyFolder.exists()) {
            dependencyFolder.mkdirs();
        }

        URL url = file.toURI().toURL();

        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        Class<?> clazz = URLClassLoader.class;
        var method = clazz.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(sysLoader, url);
    }

}