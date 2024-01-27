package pl.karpik122.ServerIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayTime implements ICommand {
    private final LanguageLoader languageLoader = LanguageManager.getInstance();

    @Override
    public String getName() {
        return languageLoader.getTranslation("discord_playtime");
    }

    @Override
    public String getDiscretion() {
        return languageLoader.getTranslation("discord_playtime_description");
    }

    @Override
    public List<OptionData> getOptions() {
        String nick = languageLoader.getTranslation("discord_playtime_nick");
        String enter_nick = languageLoader.getTranslation("discord_playtime_enter_player_name");

        List<OptionData> option = new ArrayList<>();
        option.add(new OptionData(OptionType.STRING, nick, enter_nick, true));

        return option;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String nick = languageLoader.getTranslation("discord_playtime_nick");
        String no_such_player = languageLoader.getTranslation("playtime_null_player");
        String min_in_server = languageLoader.getTranslation("playtime_minutes_on_server");
        String hours_in_server = languageLoader.getTranslation("playtime_hours_on_server");
        String player = Objects.requireNonNull(event.getOption(nick)).getAsString();

        File f = new File(MainSpigot.getPlugin().getDataFolder(), "playtime.yml");
        YamlConfiguration file = YamlConfiguration.loadConfiguration(f);

        if (file.get(player) == null) {
            event.reply(no_such_player).setEphemeral(true).queue();
            return;
        }
        int minutes = file.getInt(player);
        int h = minutes / 60;

        min_in_server = min_in_server.replace("{minutes}", String.valueOf(minutes));
        min_in_server = min_in_server.replace("{player}", player);

        hours_in_server = hours_in_server.replace("{hours}", String.valueOf(h));
        hours_in_server = hours_in_server.replace("{minutes}", String.valueOf(minutes));
        hours_in_server = hours_in_server.replace("{player}", player);

        if (h == 0) {
            event.reply(min_in_server).queue();
            return;
        }
        event.reply(hours_in_server).queue();
    }
}
