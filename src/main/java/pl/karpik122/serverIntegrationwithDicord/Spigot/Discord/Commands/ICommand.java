package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {

    String getName();

    String getDescription();

    default List<OptionData> getOptions() {
        return List.of();
    }

    void execute(SlashCommandInteractionEvent event);

}
