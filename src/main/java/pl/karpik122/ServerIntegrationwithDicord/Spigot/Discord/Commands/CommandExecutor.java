package pl.karpik122.ServerIntegrationwithDicord.Spigot.Discord.Commands;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor extends ListenerAdapter {

    private final List<ICommand> commands = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (ICommand command : commands) {
            if (command.getName().equals(event.getName())) {
                command.execute(event);
                return;
            }
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        for (ICommand command : commands) {
            if (command.getOptions() == null) {
                commandData.add(Commands.slash(command.getName(), command.getDiscretion()));
            } else {
                commandData.add(Commands.slash(command.getName(), command.getDiscretion())
                        .addOptions(command.getOptions()));
            }
        }

        System.out.println("Command Data: " + commandData);

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }


    public void add(ICommand command) {
        commands.add(command);
    }
}
