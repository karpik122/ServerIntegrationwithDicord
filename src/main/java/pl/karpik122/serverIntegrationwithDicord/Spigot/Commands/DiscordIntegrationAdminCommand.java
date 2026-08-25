package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.util.Locale;

public class DiscordIntegrationAdminCommand implements CommandExecutor {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public DiscordIntegrationAdminCommand(MainSpigot plugin) {
        this.plugin = plugin;
        this.languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("serverintegrationwithdicord.admin")) {
            sender.sendMessage(ChatColor.RED + languageLoader.getTranslation("no_permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.YELLOW + "Reloading Discord integration...");
            plugin.reloadIntegration();
            sender.sendMessage(ChatColor.GREEN + languageLoader.getTranslation("admin_command_accept"));
            return true;
        }

        if (args[0].equalsIgnoreCase("health")) {
            sendHealthCheck(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("past")) {
            handleSet(sender, args);
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + languageLoader.getTranslation("admin_command_missing_value"));
            sendHelp(sender);
            return;
        }

        String target = args[1].toLowerCase(Locale.ROOT);
        String value = args[2].trim();
        String configPath;

        switch (target) {
            case "token" -> configPath = "TOKEN";
            case "log" -> configPath = "id_log_channel";
            case "report" -> configPath = "report_channel";
            case "guild" -> configPath = "guildID";
            default -> {
                sendHelp(sender);
                return;
            }
        }

        if (!target.equals("token") && !isDiscordId(value)) {
            sender.sendMessage(ChatColor.RED + languageLoader.getTranslation("admin_command_invalid_id"));
            return;
        }
        if (target.equals("token") && value.isBlank()) {
            sender.sendMessage(ChatColor.RED + languageLoader.getTranslation("admin_command_missing_value"));
            return;
        }

        FileConfiguration config = plugin.getConfig();
        config.set(configPath, value);
        plugin.saveConfig();
        plugin.reloadIntegration();
        sender.sendMessage(ChatColor.GREEN + languageLoader.getTranslation("admin_command_accept"));
    }

    private void sendHealthCheck(CommandSender sender) {
        JDA currentJda = MainSpigot.jda;
        boolean jdaOnline = currentJda != null && currentJda.getStatus() == JDA.Status.CONNECTED;
        String guildId = plugin.normalizedConfigValue("guildID");
        String logChannelId = plugin.normalizedConfigValue("id_log_channel");
        String reportChannelId = plugin.normalizedConfigValue("report_channel");

        Guild guild = currentJda == null || guildId.isBlank() ? null : currentJda.getGuildById(guildId);
        TextChannel logChannel = guild == null || logChannelId.isBlank()
                ? null : guild.getTextChannelById(logChannelId);
        TextChannel reportChannel = guild == null || reportChannelId.isBlank()
                ? null : guild.getTextChannelById(reportChannelId);

        sender.sendMessage(ChatColor.GOLD + "==== Discord Integration Health Check ====");
        sendStatus(sender, "JDA", jdaOnline, plugin.getJdaStatusName());
        sendStatus(sender, "Guild configured", !guildId.isBlank(), displayId(guildId));
        sendStatus(sender, "Guild reachable", guild != null, guild == null ? "NO" : guild.getName());
        sendStatus(sender, "Log channel", logChannel != null && logChannel.canTalk(), displayId(logChannelId));
        sendStatus(sender, "Report channel", reportChannel != null && reportChannel.canTalk(), displayId(reportChannelId));
        sendStatus(sender, "Discord status task", plugin.isStatusTimerActive(), boolText(plugin.isStatusTimerActive()));
        sendStatus(sender, "Playtime task", plugin.isCounterTimerActive(), boolText(plugin.isCounterTimerActive()));
        sendStatus(sender, "Vault economy", plugin.isEconomyEnabled(), boolText(plugin.isEconomyEnabled()));
        sender.sendMessage(ChatColor.YELLOW + "Pending link codes: " + ChatColor.WHITE
                + AccountLink.getPendingCodeCount());
        sender.sendMessage(ChatColor.YELLOW + "Debug mode: " + ChatColor.WHITE
                + boolText(plugin.isDebugEnabled()));
    }

    private void sendStatus(CommandSender sender, String name, boolean ok, String value) {
        sender.sendMessage(ChatColor.YELLOW + name + ": "
                + (ok ? ChatColor.GREEN : ChatColor.RED) + value);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + languageLoader.getTranslation("admin_command_help"));
        sender.sendMessage(ChatColor.GREEN + "/discordintegration reload");
        sender.sendMessage(ChatColor.GREEN + "/discordintegration health");
        sender.sendMessage(ChatColor.GREEN + "/discordintegration set token <token>");
        sender.sendMessage(ChatColor.GREEN + "/discordintegration set guild <guildId>");
        sender.sendMessage(ChatColor.GREEN + "/discordintegration set log <channelId>");
        sender.sendMessage(ChatColor.GREEN + "/discordintegration set report <channelId>");
        sender.sendMessage(ChatColor.GRAY + "Legacy alias: 'past' works in place of 'set'.");
    }

    private boolean isDiscordId(String value) {
        return value.matches("\\d{15,22}");
    }

    private String displayId(String value) {
        return value.isBlank() ? "<not configured>" : value;
    }

    private String boolText(boolean value) {
        return value ? "YES" : "NO";
    }
}
