package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.Economy;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.util.List;
import java.util.UUID;

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.econ;

public class Money implements ICommand {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public Money(MainSpigot plugin) {
        this.plugin = plugin;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public String getName() {
        return "money";
    }

    @Override
    public String getDescription() {
        return languageLoader.getTranslation("description_money");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        UUID uid = Economy.getUid(event.getUser().getId());

        if (uid == null) {
            event.reply(languageLoader.getTranslation("isnt_connectet_discord")).setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue(hook -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (econ == null) {
                hook.editOriginal(languageLoader.getTranslation("economy_unavailable")).queue();
                return;
            }

            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uid);
                double balance = econ.getBalance(player);
                hook.editOriginal(languageLoader.getTranslation("have_in_account") + econ.format(balance)).queue();
            } catch (Exception exception) {
                plugin.getLogger().severe("Vault balance lookup failed: " + exception.getMessage());
                hook.editOriginal(languageLoader.getTranslation("economy_unavailable")).queue();
            }
        }));
    }
}
