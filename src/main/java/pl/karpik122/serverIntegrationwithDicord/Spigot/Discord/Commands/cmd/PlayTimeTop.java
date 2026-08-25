package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.PlaytimeStore.PlayerPlaytime;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

public class PlayTimeTop implements ICommand {
    private final LanguageLoader languageLoader;
    private final MainSpigot pl;

    public PlayTimeTop(MainSpigot pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public String getName() {
        String configuredName = languageLoader.getTranslation("playtime_command").toLowerCase(Locale.ROOT);
        return configuredName.matches("[a-z0-9_-]{1,32}") ? configuredName : "playtimetop";
    }

    @Override
    public String getDescription() {
        return languageLoader.getTranslation("playtime_description");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String notification_from = languageLoader.getTranslation("notification_from");

        List<PlayerPlaytime> topPlayers = pl.getPlaytimeStore().getTopPlayers(5);

        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle(languageLoader.getTranslation("playtime_top5"));
        emb.setColor(Color.YELLOW);
        emb.setFooter(notification_from);
        emb.setTimestamp(Instant.now());

        int count = 1;
        for (PlayerPlaytime entry : topPlayers) {
            String name = entry.playerName();
            int totalMinutes = entry.minutes();

            int h = totalMinutes / 60;
            int m = totalMinutes % 60;

            emb.addField("#" + count + " " + name, h
                    + " " + languageLoader.getTranslation("playtime_h") + " " + m
                    + " " + languageLoader.getTranslation("playtime_min"), false);
            count++;
        }

        if (topPlayers.isEmpty()) {
            emb.setDescription(languageLoader.getTranslation("playtime_empty"));
        }

        event.replyEmbeds(emb.build()).queue();
    }
}
