package pl.karpik122.serverIntegrationwithDicord.Spigot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Commands.AccountLink;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Commands.DiscordIntegrationAdminCommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Commands.DiscordIntegrationAdminTabCompleter;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Commands.Reports;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.CommandExecutor;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd.Link;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd.Money;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd.PlayTime;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd.PlayTimeTop;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Buttons;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.ChatFlagWords;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Start;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.StatusUpdater;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Logi.CommandLogger;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Timer.Counter;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Util.UpdateChecker;

import java.util.Timer;


public final class MainSpigot extends JavaPlugin implements Listener {

    private final int pluginID = 30145;
    public static JDA jda;
    Metrics metrics;
    FileConfiguration config = this.getConfig();
    String TOKEN = config.getString("TOKEN");
    public static Economy econ = null;

    @Override
    public void onEnable() {
        metrics = new Metrics(this, pluginID);
        config.options().copyDefaults(true);
        saveDefaultConfig();

        LanguageLoader langLoader = new LanguageLoader(this);
        LanguageManager.init(langLoader);

        String there_is_not_a_new_update = langLoader.getTranslation("there_is_not_a_new_update");
        String there_is_a_new_update = langLoader.getTranslation("there_is_a_new_update");
        String loading = langLoader.getTranslation("loading");


        new UpdateChecker(this).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(there_is_not_a_new_update);
            } else {
                Bukkit.getConsoleSender().sendMessage(there_is_a_new_update);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "https://www.curseforge.com/minecraft/bukkit-plugins/server-integration-with-discord");
            }
        });


        getCommand("discordintegration").setExecutor(new DiscordIntegrationAdminCommand(this));
        getCommand("discordintegration").setTabCompleter(new DiscordIntegrationAdminTabCompleter());


        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Server Integration with Dicord - " + loading);
        runBot();


        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░╚═══██╗░██║░░░████╔═████║░░██║░░██║░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage("");

        // Events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CommandLogger(this), this);

        if (Boolean.parseBoolean(config.getString("flag"))) {
            getServer().getPluginManager().registerEvents(new ChatFlagWords(this), this);
        } else {
            return;
        }
        getCommand("report").setExecutor(new Reports(this));
        getCommand("link").setExecutor(new AccountLink());

        if (setupEconomy()) {
            econ.isEnabled();
        }
    }


    public void onDisable() {
        LanguageLoader langLoader = new LanguageLoader(this);
        LanguageManager.init(langLoader);
        String pluginOff = langLoader.getTranslation("Plugin_off");

        Metrics metrics = new Metrics(this, pluginID);
        metrics.shutdown();
        if (MainSpigot.jda != null && MainSpigot.jda.getStatus() == JDA.Status.CONNECTED) {
            jda.shutdown();
        }
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░╚═══██╗░██║░░░████╔═████║░░██║░░██║░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + pluginOff);
        metrics.shutdown();
    }

    public void runBot() {
        if (TOKEN == null || TOKEN.isEmpty() || TOKEN.equals("TOKEN")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "INVALID TOKEN FOR BOT");
            return;
        }

        LanguageLoader langLoader = LanguageManager.getInstance();

        // Uruchamiamy w tle, żeby NIE ZACIĄĆ serwera!
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                CommandExecutor command = new CommandExecutor();
                command.add(new PlayTime(this));
                command.add(new Link(this));
                command.add(new PlayTimeTop(this));
                if (setupEconomy()) {
                    command.add(new Money());
                }

                jda = JDABuilder.createDefault(TOKEN,
                                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_INVITES,
                                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_TYPING,
                                GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES,
                                GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                                GatewayIntent.MESSAGE_CONTENT, GatewayIntent.SCHEDULED_EVENTS,
                                GatewayIntent.GUILD_EXPRESSIONS)
                        .setAutoReconnect(true)
                        .addEventListeners(command, new Start(this)).build();

                jda.awaitReady(); // Czekamy, aż bot się w pełni uruchomi

                jda.addEventListener(new Buttons(this));
                // TERAZ sprawdzamy, czy bot na pewno działa i pobieramy jego dane
                if (jda != null) {
                    Timer timer = new Timer();
                    timer.schedule(new StatusUpdater(this), 0L, 30000L);
                    timer.schedule(new Counter(this), 0L, 60000L);

                    String logInFor = langLoader.getTranslation("Log_in_for");
                    logInFor = logInFor.replace("{botName}", jda.getSelfUser().getName());
                    logInFor = logInFor.replace("{botID}", jda.getSelfUser().getId());

                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Server Integration with Dicord - " + logInFor);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Przerwano łączenie bota Discord.");
            } catch (Exception e) {
                // Przechwytujemy też inne błędy (np. zły token)
                String errorToken = langLoader.getTranslation("error_token");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + errorToken);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Błąd JDA: " + e.getMessage());
            }
        });
    }

    public void stopBot() {
        jda.shutdown();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}