package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Logi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.Color;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CommandLogger implements Listener {
    private static final Set<String> SAFE_DEFAULT_EXCLUSIONS = Set.of(
            "login", "l", "log", "register", "reg", "changepassword",
            "changepass", "cp", "discordintegration", "di", "report"
    );

    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public CommandLogger(MainSpigot plugin) {
        this.plugin = plugin;
        this.languageLoader = LanguageManager.getInstance();
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!plugin.getConfig().getBoolean("command_log.enabled", true)) {
            return;
        }

        String commandName = extractCommandName(event.getMessage());
        if (getExcludedCommands().contains(commandName)) {
            return;
        }

        JDA currentJda = MainSpigot.jda;
        if (currentJda == null || currentJda.getStatus() != JDA.Status.CONNECTED) {
            return;
        }

        String logChannelId = plugin.normalizedConfigValue("id_log_channel");
        String guildId = plugin.normalizedConfigValue("guildID");
        TextChannel channel = logChannelId.isBlank() ? null : currentJda.getTextChannelById(logChannelId);
        if (channel == null || !channel.canTalk() || !channel.getGuild().getId().equals(guildId)) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(event.getPlayer().getName())
                .setColor(Color.RED)
                .setTitle(languageLoader.getTranslation("commandlog_use_log"))
                .addField(languageLoader.getTranslation("commandlog_command"), event.getMessage(), false)
                .addField(languageLoader.getTranslation("commandlog_player"), event.getPlayer().getName(), true)
                .setFooter(languageLoader.getTranslation("notification_from"))
                .setTimestamp(Instant.now());

        channel.sendMessageEmbeds(embed.build()).queue(
                ignored -> {
                },
                error -> plugin.debugLog("Could not send command log: " + error.getMessage())
        );
    }

    private Set<String> getExcludedCommands() {
        Set<String> exclusions = new HashSet<>(SAFE_DEFAULT_EXCLUSIONS);
        for (String configured : plugin.getConfig().getStringList("command_log.excluded_commands")) {
            String normalized = configured.toLowerCase(Locale.ROOT).trim();
            if (normalized.startsWith("/")) {
                normalized = normalized.substring(1);
            }
            if (!normalized.isBlank()) {
                exclusions.add(normalized);
            }
        }
        return exclusions;
    }

    private String extractCommandName(String rawMessage) {
        String root = rawMessage.substring(1).split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        int namespaceSeparator = root.indexOf(':');
        return namespaceSeparator >= 0 ? root.substring(namespaceSeparator + 1) : root;
    }
}
