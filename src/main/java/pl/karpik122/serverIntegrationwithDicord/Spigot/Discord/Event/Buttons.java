package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.time.Duration;
import java.util.List;

public class Buttons extends ListenerAdapter {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public Buttons(MainSpigot plugin) {
        this.plugin = plugin;
        this.languageLoader = LanguageManager.getInstance();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] data = event.getComponentId().split(":", 2);
        if (data.length != 2 || !(data[0].equals("ban") || data[0].equals("kick"))) {
            return;
        }

        Member member = event.getMember();
        List<String> adminRoleIds = plugin.getConfig().getStringList("admindiscordid");
        boolean hasPermission = member != null && member.getRoles().stream()
                .anyMatch(role -> adminRoleIds.contains(role.getId()));

        if (!hasPermission) {
            event.reply(languageLoader.getTranslation("flag_op")).setEphemeral(true).queue();
            return;
        }

        String action = data[0];
        String targetPlayer = data[1];
        String moderatorName = event.getUser().getName();

        event.deferReply(true).queue(hook -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (action.equals("ban")) {
                ProfileBanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
                PlayerProfile targetProfile = Bukkit.createPlayerProfile(targetPlayer);
                banList.addBan(
                        targetProfile,
                        languageLoader.getTranslation("flag_ban1") + moderatorName,
                        Duration.ofDays(7),
                        moderatorName
                );

                Player onlinePlayer = Bukkit.getPlayerExact(targetPlayer);
                if (onlinePlayer != null) {
                    onlinePlayer.kickPlayer(languageLoader.getTranslation("flag_ban1") + moderatorName);
                }
                hook.editOriginal(languageLoader.getTranslation("flag_ban2") + targetPlayer).queue();
                return;
            }

            Player player = Bukkit.getPlayerExact(targetPlayer);
            if (player == null) {
                String offline = languageLoader.getTranslation("flag_ofline_player")
                        .replace("{player}", targetPlayer);
                hook.editOriginal(offline).queue();
                return;
            }

            player.kickPlayer(languageLoader.getTranslation("flag_kick1") + moderatorName);
            hook.editOriginal(languageLoader.getTranslation("flag_kick2") + targetPlayer).queue();
        }));
    }
}
