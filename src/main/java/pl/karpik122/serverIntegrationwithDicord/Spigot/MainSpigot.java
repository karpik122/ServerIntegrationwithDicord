package pl.karpik122.serverIntegrationwithDicord.Spigot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
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
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Logi.CommandLogger;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Start;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.StatusUpdater;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.PlaytimeStore;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Timer.Counter;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Util.UpdateChecker;

public final class MainSpigot extends JavaPlugin {
    public static volatile JDA jda;
    public static volatile Economy econ;

    private final Object botLifecycleLock = new Object();
    private long botGeneration;

    private boolean economyEnabled;
    private BukkitTask statusTask;
    private BukkitTask playtimeTask;
    private PlaytimeStore playtimeStore;
    private LanguageLoader languageLoader;
    private ChatFlagWords chatFlagWords;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        languageLoader = new LanguageLoader(this);
        LanguageManager.init(languageLoader);

        new pl.karpik122.serverIntegrationwithDicord.Spigot.File.Economy(this);
        playtimeStore = new PlaytimeStore(this);
        economyEnabled = setupEconomy();

        registerCommands();
        registerEvents();
        startPlaytimeCounter();

        if (getConfig().getBoolean("check_for_updates", false)) {
            checkForUpdates();
        }

        getLogger().info("Server Integration with Discord is starting...");
        runBot();
    }

    @Override
    public void onDisable() {
        if (playtimeTask != null) {
            playtimeTask.cancel();
            playtimeTask = null;
        }

        stopBot();
        AccountLink.clearAllCodes();

        String pluginOff = languageLoader == null
                ? "Plugin disabled."
                : languageLoader.getTranslation("Plugin_off");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + pluginOff);
    }

    public void runBot() {
        String token = normalizedConfigValue("TOKEN");
        if (token.isBlank() || "TOKEN".equalsIgnoreCase(token)) {
            getLogger().warning("Discord bot token is not configured. Minecraft commands and playtime tracking remain active.");
            return;
        }

        String guildId = normalizedConfigValue("guildID");
        String logChannelId = normalizedConfigValue("id_log_channel");
        long generation;
        synchronized (botLifecycleLock) {
            generation = ++botGeneration;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> connectBot(
                generation, token, guildId, logChannelId
        ));
    }

    private void connectBot(long generation, String token, String guildId, String logChannelId) {
        JDA candidate = null;
        try {
            debugLog("Starting Discord bot initialization");

            CommandExecutor commandExecutor = new CommandExecutor(this, guildId);
            commandExecutor.add(new PlayTime(this));
            commandExecutor.add(new Link());
            commandExecutor.add(new PlayTimeTop(this));
            if (economyEnabled) {
                commandExecutor.add(new Money(this));
            }

            candidate = JDABuilder.createDefault(token)
                    .setAutoReconnect(true)
                    .addEventListeners(
                            commandExecutor,
                            new Start(this, guildId, logChannelId),
                            new Buttons(this)
                    )
                    .build();

            synchronized (botLifecycleLock) {
                if (generation != botGeneration || !isEnabled()) {
                    candidate.shutdownNow();
                    return;
                }
                jda = candidate;
            }

            candidate.awaitReady();

            synchronized (botLifecycleLock) {
                if (generation != botGeneration || jda != candidate || !isEnabled()) {
                    candidate.shutdownNow();
                    return;
                }
            }

            startStatusUpdater(candidate);
            String loggedIn = languageLoader.getTranslation("Log_in_for")
                    .replace("{botName}", candidate.getSelfUser().getName())
                    .replace("{botID}", candidate.getSelfUser().getId());
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN
                    + "Server Integration with Discord - " + loggedIn);
            debugLog("Discord bot connected and ready");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            debugLog("Discord bot initialization was interrupted");
            clearFailedCandidate(generation, candidate);
        } catch (Exception exception) {
            boolean isCurrent;
            synchronized (botLifecycleLock) {
                isCurrent = generation == botGeneration;
            }
            clearFailedCandidate(generation, candidate);
            if (isCurrent && isEnabled()) {
                getLogger().severe(languageLoader.getTranslation("error_token"));
                getLogger().severe("JDA: " + exception.getClass().getSimpleName() + ": " + exception.getMessage());
                if (isDebugEnabled()) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void clearFailedCandidate(long generation, JDA candidate) {
        synchronized (botLifecycleLock) {
            if (generation == botGeneration && jda == candidate) {
                jda = null;
            }
        }
        if (candidate != null) {
            candidate.shutdownNow();
        }
    }

    public void stopBot() {
        JDA botToStop;
        synchronized (botLifecycleLock) {
            botGeneration++;
            botToStop = jda;
            jda = null;
        }

        cancelStatusUpdater();
        if (botToStop != null && botToStop.getStatus() != JDA.Status.SHUTDOWN) {
            debugLog("Shutting down Discord bot");
            botToStop.shutdownNow();
        }
    }

    public void reloadIntegration() {
        stopBot();
        reloadConfig();
        languageLoader.reload();
        chatFlagWords.reloadWords();
        economyEnabled = setupEconomy();
        runBot();
    }

    private void startPlaytimeCounter() {
        if (playtimeTask != null) {
            playtimeTask.cancel();
        }
        playtimeTask = Bukkit.getScheduler().runTaskTimer(
                this,
                new Counter(this, playtimeStore),
                20L * 60L,
                20L * 60L
        );
    }

    private void startStatusUpdater(JDA connectedJda) {
        Bukkit.getScheduler().runTask(this, () -> {
            if (!isEnabled() || jda != connectedJda) {
                return;
            }
            cancelStatusUpdater();
            statusTask = Bukkit.getScheduler().runTaskTimer(
                    this,
                    new StatusUpdater(this, connectedJda),
                    0L,
                    20L * 30L
            );
        });
    }

    private void cancelStatusUpdater() {
        if (statusTask != null) {
            statusTask.cancel();
            statusTask = null;
        }
    }

    private boolean setupEconomy() {
        econ = null;
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("Vault not found; Discord /money will not be registered.");
            return false;
        }

        RegisteredServiceProvider<Economy> provider = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            getLogger().warning("Vault is installed, but no economy provider is registered.");
            return false;
        }

        econ = provider.getProvider();
        if (econ != null) {
            getLogger().info("Vault economy integration enabled: " + econ.getName());
            return true;
        }
        return false;
    }

    private void registerCommands() {
        PluginCommand adminCommand = getCommand("discordintegration");
        if (adminCommand == null) {
            getLogger().severe("Command 'discordintegration' is missing in plugin.yml");
        } else {
            adminCommand.setExecutor(new DiscordIntegrationAdminCommand(this));
            adminCommand.setTabCompleter(new DiscordIntegrationAdminTabCompleter());
        }

        PluginCommand reportCommand = getCommand("report");
        if (reportCommand == null) {
            getLogger().severe("Command 'report' is missing in plugin.yml");
        } else {
            reportCommand.setExecutor(new Reports(this));
        }

        PluginCommand linkCommand = getCommand("link");
        if (linkCommand == null) {
            getLogger().severe("Command 'link' is missing in plugin.yml");
        } else {
            linkCommand.setExecutor(new AccountLink(this));
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new CommandLogger(this), this);
        chatFlagWords = new ChatFlagWords(this);
        getServer().getPluginManager().registerEvents(chatFlagWords, this);
    }

    private void checkForUpdates() {
        new UpdateChecker(this).getVersion(version -> {
            if (getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(languageLoader.getTranslation("there_is_not_a_new_update"));
            } else {
                Bukkit.getConsoleSender().sendMessage(languageLoader.getTranslation("there_is_a_new_update"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN
                        + "https://www.curseforge.com/minecraft/bukkit-plugins/server-integration-with-discord");
            }
        });
    }

    public PlaytimeStore getPlaytimeStore() {
        return playtimeStore;
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("debug", false);
    }

    public boolean isEconomyEnabled() {
        return economyEnabled && econ != null;
    }

    public String getJdaStatusName() {
        JDA current = jda;
        return current == null ? "OFFLINE" : current.getStatus().name();
    }

    public boolean isStatusTimerActive() {
        return statusTask != null && !statusTask.isCancelled();
    }

    public boolean isCounterTimerActive() {
        return playtimeTask != null && !playtimeTask.isCancelled();
    }

    public String normalizedConfigValue(String path) {
        String value = getConfig().getString(path, "");
        return value == null ? "" : value.trim();
    }

    public void debugLog(String message) {
        if (isDebugEnabled()) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}
