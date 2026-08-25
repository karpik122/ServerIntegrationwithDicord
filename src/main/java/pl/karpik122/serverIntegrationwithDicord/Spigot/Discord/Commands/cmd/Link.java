package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Commands.AccountLink;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.Economy;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;

import java.util.List;
import java.util.UUID;

public class Link implements ICommand {
    private final LanguageLoader languageLoader;

    public Link() {
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDescription() {
        return languageLoader.getTranslation("description_link");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "kod", languageLoader.getTranslation("code_generate"), true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String kod = event.getOption("kod") == null ? "" : event.getOption("kod").getAsString();
        UUID graczUUID = AccountLink.consumeCode(kod);

        if (graczUUID != null) {
            String discordID = event.getUser().getId();

            if (Economy.setPlayersLink(graczUUID, discordID)) {
                event.reply(languageLoader.getTranslation("connect_account")).setEphemeral(true).queue();
            } else {
                event.reply(languageLoader.getTranslation("error_connect_account")).setEphemeral(true).queue();
            }
        } else {
            event.reply(languageLoader.getTranslation("link_code_expired")).setEphemeral(true).queue();
        }
    }
}
