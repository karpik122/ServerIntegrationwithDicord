package pl.karpik122.ServerIntegrationwithDicord.Bungee.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeConfig;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeLanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeLanguageManager;

import java.awt.*;
import java.time.Instant;
public class bungeeReports extends Command implements Listener {
    JDA jda = MainBungee.jda;
    private final bungeeLanguageLoader languageLoader;
    bungeeConfig config;
    private MainBungee pl;
    public bungeeReports() {
        super("report");
        languageLoader = bungeeLanguageManager.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String report = config.getConfig().getString("report_channel");

        String there_is_no_such_player = languageLoader.getTranslation("there_is_no_such_player");
        String report_report = languageLoader.getTranslation("report_report");
        String report_applicant = languageLoader.getTranslation("report_applicant");
        String report_notified = languageLoader.getTranslation("report_notified");
        String notification_from = languageLoader.getTranslation("notification_from");
        String report_correct_usage = languageLoader.getTranslation("report_correct_usage");
        String report_reason = languageLoader.getTranslation("report_reason");
        String report_successfully = languageLoader.getTranslation("report_successfully");

        assert report != null;
        TextChannel reportChannel = jda.getTextChannelById(report);
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String p = player.getName();

        if (args.length > 1) {

            String playerName = ProxyServer.getInstance().getName();
            report = ProxyServer.getInstance().getPlayer(playerName).getName();
            StringBuilder content = new StringBuilder();


            for (int i = 1; i < args.length; i++) {
                content.append(" ").append(args[i]);
            }

            report_successfully = report_successfully.replace("{player}", playerName);
            report_successfully = report_successfully.replace("{reason}", String.valueOf(content));

            try {

                pl.getLogger().info(ChatColor.GREEN + report_successfully);
            } catch (Exception e) {
                pl.getLogger().info(ChatColor.RED + there_is_no_such_player);
            }


            EmbedBuilder emb = new EmbedBuilder();
            emb.setAuthor(report_report, null, "https://minotar.net/helm/" + p + "/300.png");
            emb.setThumbnail("https://minotar.net/helm/" + report + "/300.png");
            emb.addField(report_applicant, p, true);
            emb.addField(report_notified, report, true);
            emb.addField(report_reason, content.toString(), false);
            emb.setColor(Color.YELLOW);
            emb.setFooter(notification_from);
            emb.setTimestamp(Instant.now());

            assert reportChannel != null;
            reportChannel.sendMessageEmbeds(emb.build()).queue();

        } else {
            pl.getLogger().info(ChatColor.RED + report_correct_usage);
        }
    }
}
