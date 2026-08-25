package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandExecutor extends ListenerAdapter {

    private final List<ICommand> commands = new ArrayList<>();
    private final Set<String> commandNames = new HashSet<>();
    private final MainSpigot plugin;
    private final String guildId;

    public CommandExecutor(MainSpigot plugin, String guildId) {
        this.plugin = plugin;
        this.guildId = guildId;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("Tej komendy można używać tylko na serwerze Discord.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getGuild().getId().equals(guildId)) {
            event.reply("Bot nie jest skonfigurowany dla tego serwera Discord.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        for (ICommand command : commands) {
            if (command.getName().equals(event.getName())) {
                try {
                    command.execute(event);
                } catch (Exception exception) {
                    plugin.getLogger().severe(
                            "Discord command /" + event.getName()
                                    + " failed: " + exception.getMessage()
                    );

                    exception.printStackTrace();

                    if (event.isAcknowledged()) {
                        event.getHook()
                                .sendMessage("Wystąpił błąd podczas wykonywania komendy.")
                                .setEphemeral(true)
                                .queue();
                    } else {
                        event.reply("Wystąpił błąd podczas wykonywania komendy.")
                                .setEphemeral(true)
                                .queue();
                    }
                }
                return;
            }
        }

        event.reply("Ta komenda jest nieaktualna. Uruchom ponownie Discorda.")
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if (guildId.isBlank() || !event.getGuild().getId().equals(guildId)) {
            return;
        }

        List<CommandData> commandData = new ArrayList<>();
        for (ICommand command : commands) {
            commandData.add(Commands.slash(command.getName(), safeDescription(command.getDescription()))
                    .addOptions(command.getOptions()));
        }

        event.getGuild().updateCommands().addCommands(commandData).queue(
                ignored -> plugin.getLogger().info("Registered " + commandData.size()
                        + " Discord slash commands in guild " + event.getGuild().getName()),
                error -> plugin.getLogger().severe("Could not register Discord slash commands: "
                        + error.getMessage())
        );
    }

    public void add(ICommand command) {
        if (!command.getName().matches("[a-z0-9_-]{1,32}")) {
            throw new IllegalArgumentException("Invalid Discord command name: " + command.getName());
        }
        if (!commandNames.add(command.getName())) {
            throw new IllegalArgumentException("Duplicate Discord command name: " + command.getName());
        }
        commands.add(command);
    }

    private String safeDescription(String rawDescription) {
        String description = rawDescription == null ? "" : rawDescription.trim();
        if (description.isBlank()) {
            description = "Minecraft server integration command";
        }
        return description.length() > 100 ? description.substring(0, 100) : description;
    }
}
