package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Commands.AccountLink;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.Economy;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.util.List;
import java.util.UUID;

public class Link implements ICommand {
    private final LanguageLoader languageLoader;

    public Link(MainSpigot plugin) {
        new Economy(plugin);
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDiscretion() {
        return "Łączy konto Discord z kontem Minecraft";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "kod", languageLoader.getTranslation("code_generate"), true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String kod = event.getOption("kod").getAsString();

        // Sprawdza, czy kod jest na liście z poprzedniej komendy
        if (AccountLink.pendingCodes.containsKey(kod)) {
            UUID graczUUID = AccountLink.pendingCodes.get(kod);
            String discordID = event.getUser().getId();

            Economy.setPlayersLink(graczUUID, discordID);



            // Usuwa zużyty kod
            AccountLink.pendingCodes.remove(kod);

            event.reply(languageLoader.getTranslation("connect_account")).queue();
        } else {
            // Ukryta wiadomość błędu (tylko dla gracza wpisującego)
            event.reply(languageLoader.getTranslation("error_connect_account")).setEphemeral(true).queue();
        }
    }
}
