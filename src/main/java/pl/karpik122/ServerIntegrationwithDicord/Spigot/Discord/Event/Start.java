package pl.karpik122.ServerIntegrationwithDicord.Spigot.Discord.Event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class Start extends ListenerAdapter {
    private final MainSpigot pl;
    private final LanguageLoader langLoader;
    public Start(MainSpigot plugin) {
        langLoader = LanguageManager.getInstance();
        this.pl = plugin;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        FileConfiguration config = pl.getConfig();

        TextChannel textChannelById = Objects.requireNonNull(event.getJDA().getGuildById((String) Objects.requireNonNull(config.get("guildID")))).getTextChannelById((String) config.get("id_log_channel"));
        String title = langLoader.getTranslation("im_connecting");

        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Color.green);
        emb.setTitle(title);
        emb.setTimestamp(Instant.now());

        MessageEmbed embed = emb.build();

        textChannelById.sendMessageEmbeds(embed).queue();
    }
}
