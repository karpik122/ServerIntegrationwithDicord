package pl.karpik122.ServerIntegrationwithDicord.Logi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        String use_command = languageLoader.getTranslation("commandlog_use_command");
        String log_command_use = languageLoader.getTranslation("commandlog_use_log");
        String command_use = languageLoader.getTranslation("commandlog_command_use");
        String command = languageLoader.getTranslation("commandlog_command");
        String player = languageLoader.getTranslation("commandlog_player");



        String id_log_channel = plugin.getConfig().getString("id_log_channel");
        EmbedBuilder eb = new EmbedBuilder();


        if (!event.getMessage().contains("/login") && !event.getMessage().contains("/l") &&
                !event.getMessage().contains("/register") && !event.getMessage().contains("/r") &&
                !event.getMessage().contains("/changepassword") &&
                !event.getMessage().contains("/changepass") && !event.getMessage().contains("/help") &&
                !event.getMessage().contains("/discordintegration") && !event.getMessage().contains("/report")) {

            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + event.getPlayer().getName() + " " + use_command + " " + event.getMessage());

            eb.setAuthor(event.getPlayer().getName());
            eb.setColor(Color.red);
            eb.setTitle(log_command_use);
            eb.addField(command, event.getMessage(), true);
            eb.addField(player, event.getPlayer().getName(), true);
            eb.setFooter(command_use);
            eb.setTimestamp(Instant.now());

            assert id_log_channel != null;
            TextChannel tc = jda.getTextChannelById(id_log_channel);

            assert tc != null;
            tc.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
