package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.jda;

public class ChatFlagWords implements Listener {
    private final MainSpigot pl;
    private final LanguageLoader languageLoader;

    private volatile List<String> flaggedWords = List.of();
    private volatile List<String> normalizedFlaggedWords = List.of();

    public ChatFlagWords(MainSpigot pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();

        loadWords();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!pl.getConfig().getBoolean("flag", false)) {
            return;
        }

        String message = event.getMessage();
        Player playerName = event.getPlayer();
        String normalizedMessage = message.toLowerCase(Locale.ROOT);
        List<String> currentWords = flaggedWords;
        List<String> currentNormalizedWords = normalizedFlaggedWords;

        if (currentWords.isEmpty()) {
            return;
        }

        for (int i = 0; i < currentNormalizedWords.size(); i++) {
            String normalizedWord = currentNormalizedWords.get(i);
            if (normalizedMessage.contains(normalizedWord)) {
                sendDiscordEmbed(playerName, message, currentWords.get(i));
                break;
            }
        }
    }

    private void sendDiscordEmbed(Player player, String fullMessage, String flaggedWord) {
        String notification_from = languageLoader.getTranslation("notification_from");
        String channelId = pl.getConfig().getString("id_log_channel");

        JDA currentJda = jda;
        if (channelId == null || channelId.isBlank() || currentJda == null
                || currentJda.getStatus() != JDA.Status.CONNECTED) {
            return;
        }

        TextChannel reportChannel = currentJda.getTextChannelById(channelId);
        String guildId = pl.normalizedConfigValue("guildID");

        String playerName = player.getName();

        if (reportChannel != null && reportChannel.canTalk()
                && reportChannel.getGuild().getId().equals(guildId)) {
            EmbedBuilder emb = new EmbedBuilder();
            emb.setTitle(languageLoader.getTranslation("found"));
            emb.setColor(Color.RED);
            emb.setThumbnail("https://minotar.net/helm/" + player.getName() + "/300.png");
            emb.addField(languageLoader.getTranslation("commandlog_player"), player.getName(), true);
            emb.addField(languageLoader.getTranslation("word"), flaggedWord, true);
            emb.addField(languageLoader.getTranslation("fullmessage"), fullMessage, false);
            emb.setFooter(notification_from);
            emb.setTimestamp(Instant.now());

            if (pl.getConfig().getBoolean("discordadmininteraction")) {
                reportChannel.sendMessageEmbeds(emb.build()).addComponents(ActionRow.of(
                                Button.danger("ban:" + playerName, "Ban " + playerName),
                                Button.primary("kick:" + playerName, "Kick " + playerName)
                        )
                ).queue(ignored -> {
                }, error -> pl.debugLog("Could not send flagged-word alert: " + error.getMessage()));
            } else {
                reportChannel.sendMessageEmbeds(emb.build()).queue(
                        ignored -> {
                        },
                        error -> pl.debugLog("Could not send flagged-word alert: " + error.getMessage())
                );
            }
        }
    }

    private void loadWords() {
        List<String> loadedWords = pl.getConfig().getStringList("flagwords").stream()
                .map(String::trim)
                .filter(word -> !word.isBlank())
                .toList();
        List<String> normalized = new ArrayList<>(loadedWords.size());
        for (String word : loadedWords) {
            normalized.add(word.toLowerCase(Locale.ROOT));
        }
        flaggedWords = List.copyOf(loadedWords);
        normalizedFlaggedWords = List.copyOf(normalized);
    }

    public void reloadWords() {
        loadWords();
    }
}
