package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.Color;
import java.time.Instant;

public class Start extends ListenerAdapter {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;
    private final String guildId;
    private final String logChannelId;

    public Start(MainSpigot plugin, String guildId, String logChannelId) {
        this.plugin = plugin;
        this.languageLoader = LanguageManager.getInstance();
        this.guildId = guildId;
        this.logChannelId = logChannelId;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (guildId.isBlank()) {
            plugin.getLogger().warning("guildID is not configured; Discord slash commands cannot be registered.");
            return;
        }

        Guild guild = event.getJDA().getGuildById(guildId);
        if (guild == null) {
            plugin.getLogger().warning(languageLoader.getTranslation("guild_not_found"));
            return;
        }

        if (logChannelId.isBlank()) {
            plugin.getLogger().warning("id_log_channel is not configured.");
            return;
        }

        TextChannel logChannel = guild.getTextChannelById(logChannelId);
        if (logChannel == null || !logChannel.canTalk()) {
            plugin.getLogger().warning(languageLoader.getTranslation("log_channel_not_found"));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(languageLoader.getTranslation("im_connecting"))
                .setFooter(languageLoader.getTranslation("notification_from"))
                .setTimestamp(Instant.now());

        logChannel.sendMessageEmbeds(embed.build()).queue(
                ignored -> plugin.debugLog("Sent Discord startup message"),
                error -> plugin.getLogger().warning("Could not send Discord startup message: " + error.getMessage())
        );
    }
}
