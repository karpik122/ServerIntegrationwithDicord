package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
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

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.jda;

public class ChatFlagWords extends ListenerAdapter implements Listener {
    private final MainSpigot pl;
    private final LanguageLoader languageLoader;

    @Getter
    private List<String> flaggedWords = new ArrayList<>();

    public ChatFlagWords(MainSpigot pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();

        // 1. POPRAWKA: Wczytujemy słowa z configu od razu przy uruchomieniu!
        loadWords();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player playerName = event.getPlayer();

        for (String word : flaggedWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                sendDiscordEmbed(playerName, message, word);
            }
        }
    }

    private void sendDiscordEmbed(Player player, String fullMessage, String flaggedWord) {
        String notification_from = languageLoader.getTranslation("notification_from");
        String channelId = pl.getConfig().getString("id_log_channel");

        TextChannel reportChannel = jda.getTextChannelById(channelId);

        String playerName = player.getName();

        if (reportChannel != null) {
            EmbedBuilder emb = new EmbedBuilder();
            emb.setTitle(languageLoader.getTranslation("flag"));
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
                ).queue();
            } else {
                reportChannel.sendMessageEmbeds(emb.build()).queue();
            }
        }
    }

    public void loadWords() {
        flaggedWords = pl.getConfig().getStringList("flagwords");
    }

    public void reloadWords() {
        flaggedWords.clear();
        loadWords();
    }
}