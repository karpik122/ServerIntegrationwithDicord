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
        String Log_in_for = langLoader.getTranslation("Log_in_for");

        Timer timer = new Timer();

        new UpdateChecker(this, 111094).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(there_is_not_a_new_update);
            } else {
                Bukkit.getConsoleSender().sendMessage(there_is_a_new_update);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "https://www.spigotmc.org/resources/server-integration-with-discord.111094/");
            }
        });


        getCommand("discordintegration").setExecutor(new DiscordIntegrationAdminCommand(this));
        getCommand("discordintegration").setTabCompleter(new DiscordIntegrationAdminTabCompleter());

        if (TOKEN == null || TOKEN.equals("BOT_TOKEN")) {
            getLogger().warning("Invalid TOKEN in config.yml file. I turn off the plugin...");
            timer.schedule(new Counter(this), 0L, 60000L);
            return;

        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Server Integration with Dicord - " + loading);
        runBot();

        Log_in_for = Log_in_for.replace("{botName}", jda.getSelfUser().getName());
        Log_in_for = Log_in_for.replace("{botID}",  jda.getSelfUser().getId());

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Server Integration with Dicord - " + Log_in_for);

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚═══██╗░██║░░░████╔═████║░░██║░░██║");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage("");


        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CommandLogger(this), this);
        getCommand("link").setExecutor(new AccountLink());

        if (setupEconomy()) {
            econ.isEnabled();
        }

        new Reports(this);

        timer.schedule(new StatusUpdater(this), 0L, 30000L);
        timer.schedule(new Counter(this), 0L, 60000L);
    }


    public void onDisable() {
        LanguageLoader langLoader = new LanguageLoader(this);
        LanguageManager.init(langLoader);
        String pluginOff = langLoader.getTranslation("Plugin_off");

        Metrics metrics = new Metrics(this, pluginID);
        metrics.shutdown();
        if (jda != null) {
            jda.shutdown();
        }
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚═══██╗░██║░░░████╔═████║░░██║░░██║");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + pluginOff);
        metrics.shutdown();
    }

    public void runBot() {
        try {
            CommandExecutor command = new CommandExecutor();
            command.add(new PlayTime(this));
            command.add(new Link(this));
            if (setupEconomy()) {
                command.add(new Money());
            }
            // 1. Przygotuj bota i dodaj podstawowe komendy
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
            // 3. Uruchom bota
             jda.awaitReady();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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