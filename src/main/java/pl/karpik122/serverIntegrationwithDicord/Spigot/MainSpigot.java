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
    public static Economy econ = null;
    private boolean economyEnabled;
    private Timer statusTimer;
    private Timer counterTimer;

    @Override
    public void onEnable() {
        metrics = new Metrics(this, pluginID);
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

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

        if (getConfig().getBoolean("flag")) {
            getServer().getPluginManager().registerEvents(new ChatFlagWords(this), this);
        }

        getCommand("report").setExecutor(new Reports(this));
        getCommand("link").setExecutor(new AccountLink());

        economyEnabled = setupEconomy();
    }


    public void onDisable() {
        LanguageLoader langLoader = new LanguageLoader(this);
        LanguageManager.init(langLoader);
        String pluginOff = langLoader.getTranslation("Plugin_off");

        if (metrics != null) {
            metrics.shutdown();
        }
        stopBot();

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
    }

    public void runBot() {
        String token = getConfig().getString("TOKEN");
        if (token == null || token.isBlank() || token.equals("TOKEN")) {
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
                if (economyEnabled) {
                    command.add(new Money());
                }

                jda = JDABuilder.createDefault(token,
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
                    startTimers();

                    String logInFor = langLoader.getTranslation("Log_in_for");
                    logInFor = logInFor.replace("{botName}", jda.getSelfUser().getName());
                    logInFor = logInFor.replace("{botID}", jda.getSelfUser().getId());

                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Server Integration with Dicord - " + logInFor);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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
        cancelTimers();
        if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN) {
            jda.shutdown();
        }
        jda = null;
    }

    private void startTimers() {
        cancelTimers();
        statusTimer = new Timer("siwd-status-updater", true);
        counterTimer = new Timer("siwd-counter", true);
        statusTimer.schedule(new StatusUpdater(this), 0L, 30000L);
        counterTimer.schedule(new Counter(this), 0L, 60000L);
    }

    private void cancelTimers() {
        if (statusTimer != null) {
            statusTimer.cancel();
            statusTimer = null;
        }
        if (counterTimer != null) {
            counterTimer.cancel();
            counterTimer = null;
        }
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