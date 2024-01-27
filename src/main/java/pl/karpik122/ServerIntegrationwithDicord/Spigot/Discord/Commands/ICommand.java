package pl.karpik122.ServerIntegrationwithDicord.Spigot.Discord.Commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    String getName();

    String getDiscretion();

    default List<Role> getRoles() {
        return Collections.emptyList();
    }

    List<OptionData> getOptions();

    void execute(SlashCommandInteractionEvent event);

}
