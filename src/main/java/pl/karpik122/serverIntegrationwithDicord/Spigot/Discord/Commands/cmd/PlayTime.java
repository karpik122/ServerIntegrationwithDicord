package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;


import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;

public class PlayTime implements ICommand {
    private final LanguageLoader languageLoader = LanguageManager.getInstance();
    private final MainSpigot plugin;

    public PlayTime(MainSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        String configuredName = languageLoader.getTranslation("discord_playtime").toLowerCase(Locale.ROOT);
        return configuredName.matches("[a-z0-9_-]{1,32}") ? configuredName : "playtime";
    }

    @Override
    public String getDescription() {
        return languageLoader.getTranslation("discord_playtime_description");
    }

    @Override
    public List<OptionData> getOptions() {
        String nick = languageLoader.getTranslation("discord_playtime_nick");
        String enter_nick = languageLoader.getTranslation("discord_playtime_enter_player_name");

        String normalizedOptionName = nick.toLowerCase(Locale.ROOT);
        String safeOptionName = normalizedOptionName.matches("[a-z0-9_-]{1,32}")
                ? normalizedOptionName : "nick";
        return List.of(new OptionData(OptionType.STRING, safeOptionName, enter_nick, true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String nick = languageLoader.getTranslation("discord_playtime_nick");
        String no_such_player = languageLoader.getTranslation("playtime_null_player");
        String min_in_server = languageLoader.getTranslation("playtime_minutes_on_server");
        String hours_in_server = languageLoader.getTranslation("playtime_hours_on_server");
        String notification_from = languageLoader.getTranslation("notification_from");

        String normalizedOptionName = nick.toLowerCase(Locale.ROOT);
        String safeOptionName = normalizedOptionName.matches("[a-z0-9_-]{1,32}")
                ? normalizedOptionName : "nick";
        OptionMapping playerOption = event.getOption(safeOptionName);
        if (playerOption == null) {
            event.reply(languageLoader.getTranslation("discord_command_error")).setEphemeral(true).queue();
            return;
        }
        String player = playerOption.getAsString();
        OptionalInt storedMinutes = plugin.getPlaytimeStore().getMinutes(player);

        if (storedMinutes.isEmpty()) {
            event.reply(no_such_player).setEphemeral(true).queue();
            return;
        }
        int minutes = storedMinutes.getAsInt();
        int h = minutes / 60;

        min_in_server = min_in_server.replace("{minutes}", String.valueOf(minutes));
        min_in_server = min_in_server.replace("{player}", player);

        hours_in_server = hours_in_server.replace("{hours}", String.valueOf(h));
        hours_in_server = hours_in_server.replace("{minutes}", String.valueOf(minutes));
        hours_in_server = hours_in_server.replace("{player}", player);

        EmbedBuilder emb = new EmbedBuilder();
        emb.setAuthor(player, null, "https://minotar.net/helm/" + player + "/300.png");
        if (h == 0) {
            emb.setDescription(min_in_server);

        } else {
            emb.setDescription(hours_in_server);

        }
        emb.setColor(Color.YELLOW);
        emb.setFooter(notification_from);
        emb.setTimestamp(Instant.now());
        event.replyEmbeds(emb.build()).queue();
    }
}
