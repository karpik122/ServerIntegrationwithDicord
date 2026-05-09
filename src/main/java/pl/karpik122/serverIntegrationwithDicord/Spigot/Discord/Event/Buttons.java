package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.util.List;

public class Buttons extends ListenerAdapter {
    private final MainSpigot pl;
    private final LanguageLoader languageLoader;

    public Buttons(MainSpigot pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        // Pobieramy dozwolone role z configu
        List<String> adminRoleIds = pl.getConfig().getStringList("admindiscordid");
        String member = event.getUser().getName();
        // Sprawdzamy, czy członek posiada chociaż jedną wymaganą rolę
        boolean hasPermission = event.getMember().getRoles().stream()
                .anyMatch(role -> adminRoleIds.contains(role.getId()));

        if (!hasPermission) {
            // Jeśli nie ma, wyświetlamy mu ukrytą wiadomość i przerywamy
            event.reply(languageLoader.getTranslation("flag_op")).setEphemeral(true).queue();
            return;
        }

        String[] data = event.getComponentId().split(":");

        if (data.length != 2) return;

        String action = data[0];
        String targetPlayer = data[1];
        Player player = Bukkit.getPlayer(targetPlayer);

        Bukkit.getScheduler().runTask(pl, () -> {
            if (action.equals("ban")) {
                // Obliczamy datę za 7 dni
                java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + (7L * 24L * 60L * 60L * 1000L));

                // Nadajemy bana z wyliczoną datą
                Bukkit.getBanList(BanList.Type.NAME).addBan(
                        targetPlayer,
                        languageLoader.getTranslation("flag_ban1") + member,
                        expirationDate,
                        member
                );

                event.reply(languageLoader.getTranslation("flag_ban2") + targetPlayer).queue();

            } else if (action.equals("kick")) {
                if (player != null) {
                    player.kickPlayer(languageLoader.getTranslation("flag_kick1") + member);
                    event.reply(languageLoader.getTranslation("flag_kick2") + targetPlayer).queue();
                } else {
                    String offline = languageLoader.getTranslation("flag_ofline_player");
                    offline.replace("{player}", targetPlayer);
                    event.reply(offline).setEphemeral(true).queue();
                }
            }
        });
    }
}