package pl.karpik122.ServerIntegrationwithDicord.Logi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;

import java.awt.*;
import java.time.Instant;

public class CommandLogger implements Listener {

    private final Main plugin;
    private final LanguageLoader languageLoader;
    public CommandLogger(Main pl) {
        this.plugin = pl;
        languageLoader = LanguageManager.getInstance();
    }
    JDA jda = Main.jda;

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String id_log_channel = plugin.getConfig().getString("id_log_channel");

        String commandlog_use_log = languageLoader.getTranslation("commandlog_use_log");
        String commandlog_command = languageLoader.getTranslation("commandlog_command");
        String commandlog_player = languageLoader.getTranslation("commandlog_player");
        String notification_from = languageLoader.getTranslation("notification_from");


        if (!event.getMessage().contains("/login") && !event.getMessage().contains("/l") &&
                !event.getMessage().contains("/register") && !event.getMessage().contains("/r") &&
                !event.getMessage().contains("/changepassword") &&
                !event.getMessage().contains("/changepass") && !event.getMessage().contains("/help") &&
                !event.getMessage().contains("/discordintegration") && !event.getMessage().contains("/report")) {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setAuthor(event.getPlayer().getName());
            eb.setColor(Color.RED);
            eb.setTitle(commandlog_use_log);
            eb.addField(commandlog_command, event.getMessage(), true);
            eb.addField(commandlog_player, event.getPlayer().getName(), true);
            eb.setFooter(notification_from);
            eb.setTimestamp(Instant.now());

            assert id_log_channel != null;
            TextChannel tc = jda.getTextChannelById(id_log_channel);

            assert tc != null;
            tc.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
