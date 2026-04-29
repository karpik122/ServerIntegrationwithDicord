package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.Economy;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;

import java.util.List;
import java.util.UUID;

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.econ;

public class Money implements ICommand {
    private final LanguageLoader languageLoader;

    public Money() {
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public String getName() {
        return "money";
    }

    @Override
    public String getDiscretion() {
        return languageLoader.getTranslation("description_money");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // 1. Pobiera identyfikator z pliku
        UUID uid = Economy.getUid(event.getUser().getId());

        // 2. NAJPIERW sprawdza, czy gracz jest połączony
        if (uid == null) {
            event.reply(languageLoader.getTranslation("isnt_connectet_discord")).setEphemeral(true).queue();
            return;
        }

        // 3. Zamienia UUID na obiekt gracza z Minecrafta (Bukkit)
        OfflinePlayer player = Bukkit.getOfflinePlayer(uid);

        // 4. Pobiera stan konta
        double balance = econ.getBalance(player);

        // 5. Wysyła wiadomość na Discordzie
        event.reply(languageLoader.getTranslation("have_in_account") + balance).queue();
    }
}