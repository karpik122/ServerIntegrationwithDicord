package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.Logi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.jda;

public class CommandLogger implements Listener {
    private static final Set<String> EXCLUDED_COMMANDS = Set.of(
            "/login", "/l", "/register", "/r", "/changepassword",
            "/changepass", "/help", "/discordintegration", "/report"
    );

    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;
    public CommandLogger(MainSpigot pl) {
        this.plugin = pl;
        languageLoader = LanguageManager.getInstance();
    }


    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String id_log_channel = plugin.getConfig().getString("id_log_channel");

        String commandlog_use_log = languageLoader.getTranslation("commandlog_use_log");
        String commandlog_command = languageLoader.getTranslation("commandlog_command");
        String commandlog_player = languageLoader.getTranslation("commandlog_player");
        String notification_from = languageLoader.getTranslation("notification_from");

        if (id_log_channel == null || id_log_channel.isBlank() || jda == null) {
            return;
        }

        String message = event.getMessage().toLowerCase(Locale.ROOT);
        String commandName = message.split("\\s+", 2)[0];
        if (EXCLUDED_COMMANDS.contains(commandName)) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(event.getPlayer().getName());
        eb.setColor(Color.RED);
        eb.setTitle(commandlog_use_log);
        eb.addField(commandlog_command, event.getMessage(), true);
        eb.addField(commandlog_player, event.getPlayer().getName(), true);
        eb.setFooter(notification_from);
        eb.setTimestamp(Instant.now());

        TextChannel tc = jda.getTextChannelById(id_log_channel);
        if (tc != null) {
            tc.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
