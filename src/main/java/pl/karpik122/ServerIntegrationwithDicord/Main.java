package pl.karpik122.ServerIntegrationwithDicord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karpik122.ServerIntegrationwithDicord.Commands.DiscordIntegrationAdminCommand;
import pl.karpik122.ServerIntegrationwithDicord.Commands.DiscordIntegrationAdminTabCompleter;
import pl.karpik122.ServerIntegrationwithDicord.Commands.Reports;
import pl.karpik122.ServerIntegrationwithDicord.Discord.CommandManager;
import pl.karpik122.ServerIntegrationwithDicord.Events.StatusUpdater;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Logi.CommandLogger;
import pl.karpik122.ServerIntegrationwithDicord.Timer.Counter;
import pl.karpik122.ServerIntegrationwithDicord.Util.UpdateChecker;

import java.util.Timer;

public final class Main extends JavaPlugin implements Listener {
    private final LanguageLoader langLoader;


    public static JDA jda;
    FileConfiguration config = this.getConfig();
    String TOKEN = this.getConfig().getString("TOKEN");

    public Main() {
        langLoader = LanguageManager.getInstance();
    }

    @Override
    public void onEnable() {
        config.options().copyDefaults(true);
        saveDefaultConfig();
        LanguageLoader langLoader = new LanguageLoader(this);
        LanguageManager.init(langLoader);

        String there_is_not_a_new_update = langLoader.getTranslation("there_is_not_a_new_update");
        String there_is_a_new_update = langLoader.getTranslation("there_is_a_new_update");


        new UpdateChecker(this, 111094).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(there_is_not_a_new_update);
            } else {
                Bukkit.getConsoleSender().sendMessage(there_is_a_new_update);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "https://www.spigotmc.org/resources/server-integration-with-dicord.111094/");
            }
        });

        getCommand("discordintegration").setExecutor(new DiscordIntegrationAdminCommand(this));
        getCommand("discordintegration").setTabCompleter(new DiscordIntegrationAdminTabCompleter());

        if (TOKEN == null || TOKEN.equals("BOT_TOKEN")) {
            getLogger().warning("Invalid TOKEN in config.yml file. I turn off the plugin...");
            onDisable();
            return;
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MinecraftDiscordIntegration - " + langLoader.getTranslation("loading"));
        runBot();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MinecraftDiscordIntegration - " + langLoader.getTranslation("Log_in_for") + " " + jda.getSelfUser().getName() + " (" + jda.getSelfUser().getId() + ")");

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(" /$$      /$$  /$$$$$$  /$$$$$$$  /$$$$$$");
        Bukkit.getConsoleSender().sendMessage("| $$$    /$$$ /$$__  $$| $$__  $$ |_  $$_/");
        Bukkit.getConsoleSender().sendMessage("| $$$$  /$$$$| $$  \\__/| $$  \\ $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$ $$/$$ $$| $$      | $$  | $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$  $$$| $$| $$      | $$  | $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$\\  $ | $$| $$    $$| $$  | $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$ \\/  | $$|  $$$$$$/| $$$$$$$/ /$$$$$$");
        Bukkit.getConsoleSender().sendMessage("|__/     |__/ \\______/ |_______/ |______/");
        Bukkit.getConsoleSender().sendMessage("");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CommandLogger(this), this);
        new Reports(this);
        Timer timer = new Timer();
        timer.schedule(new StatusUpdater(this), 0L, 30000L);
        timer.schedule(new Counter(this), 0L, 60000L);
    }


    public void onDisable() {
        String pluginOff = langLoader.getTranslation("Plugin_off");

        jda.shutdown();
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(" /$$      /$$  /$$$$$$  /$$$$$$$    /$$$$$$");
        Bukkit.getConsoleSender().sendMessage("| $$$    /$$$ /$$__  $$| $$__  $$   |_  $$_/");
        Bukkit.getConsoleSender().sendMessage("| $$$$  /$$$$| $$  \\__/| $$  \\ $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$ $$/$$ $$| $$      | $$  | $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$  $$$| $$| $$      | $$  | $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$\\  $ | $$| $$    $$| $$  | $$   | $$");
        Bukkit.getConsoleSender().sendMessage("| $$ \\/  | $$|  $$$$$$/| $$$$$$$/ /$$$$$$");
        Bukkit.getConsoleSender().sendMessage("|__/     |__/ \\______/ |_______/ |______/");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(Color.RED + pluginOff);
    }
    public void runBot() {
        try {
            jda = JDABuilder.createDefault(TOKEN, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_INVITES,
                             GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.MESSAGE_CONTENT, GatewayIntent.SCHEDULED_EVENTS)
                    .addEventListeners(new CommandManager(this))
                    .build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}