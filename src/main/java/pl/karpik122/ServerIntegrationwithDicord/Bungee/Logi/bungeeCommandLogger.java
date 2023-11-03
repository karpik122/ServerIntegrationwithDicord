package pl.karpik122.ServerIntegrationwithDicord.Bungee.Logi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeConfig;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeLanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeLanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;


import java.awt.*;
import java.time.Instant;

public class bungeeCommandLogger implements Listener {
    bungeeConfig config;
    private final bungeeLanguageLoader languageLoader;

    public bungeeCommandLogger() {
        languageLoader = bungeeLanguageManager.getInstance();
    }

    JDA jda = MainBungee.jda;

    @EventHandler
    public void onCommand(ChatEvent event) {
        String id_log_channel = config.getConfig().getString("id_log_channel");

        String commandlog_use_log = languageLoader.getTranslation("commandlog_use_log");
        String commandlog_command = languageLoader.getTranslation("commandlog_command");
        String commandlog_player = languageLoader.getTranslation("commandlog_player");
        String notification_from = languageLoader.getTranslation("notification_from");

        if (event.getMessage().startsWith("/")) {
            if (!event.getMessage().contains("login") && !event.getMessage().contains("l") &&
                    !event.getMessage().contains("register") && !event.getMessage().contains("r") &&
                    !event.getMessage().contains("changepassword") &&
                    !event.getMessage().contains("changepass") && !event.getMessage().contains("help") &&
                    !event.getMessage().contains("discordintegration") && !event.getMessage().contains("report")) {

                ProxiedPlayer player = (ProxiedPlayer) event;


                EmbedBuilder eb = new EmbedBuilder();

                eb.setAuthor(event.toString());
                eb.setColor(Color.RED);
                eb.setTitle(commandlog_use_log);
                eb.addField(commandlog_command, event.getMessage(), true);
                eb.addField(commandlog_player, player.getName(), true);
                eb.setFooter(notification_from);
                eb.setTimestamp(Instant.now());

                assert id_log_channel != null;
                TextChannel tc = jda.getTextChannelById(id_log_channel);

                assert tc != null;
                tc.sendMessageEmbeds(eb.build()).queue();
            }
        }
    }
}
