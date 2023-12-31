package pl.karpik122.ServerIntegrationwithDicord.Spigot.Discord;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager extends ListenerAdapter {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public CommandManager(MainSpigot pl) {
        this.plugin = pl;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        String nick = languageLoader.getTranslation("discord_playtime_nick");
        String discord_playtime = languageLoader.getTranslation("discord_playtime");
        String no_such_player = languageLoader.getTranslation("playtime_null_player");
        String min_in_server = languageLoader.getTranslation("playtime_minutes_on_server");
        String hours_in_server = languageLoader.getTranslation("playtime_hours_on_server");

        File f = new File(plugin.getDataFolder(), "playtime.yml");
        YamlConfiguration file = YamlConfiguration.loadConfiguration(f);

        if (command.equals(discord_playtime)) {

            String player = Objects.requireNonNull(event.getOption(nick)).getAsString();

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

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        String nick = languageLoader.getTranslation("discord_playtime_nick");
        String enter_nick = languageLoader.getTranslation("discord_playtime_enter_player_name");
        String discord_playtime_description = languageLoader.getTranslation("discord_playtime_description");
        String discord_playtime = languageLoader.getTranslation("discord_playtime");

        List<CommandData> commandData = new ArrayList<>();

        OptionData option1 = new OptionData(OptionType.STRING, nick, enter_nick, true);

        commandData.add(Commands.slash(discord_playtime, discord_playtime_description).addOptions(option1));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
