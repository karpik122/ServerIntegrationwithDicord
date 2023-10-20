package pl.karpik122.ServerIntegrationwithDicord.Spigot.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class Reports implements Listener, CommandExecutor {
    JDA jda = MainSpigot.jda;
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;
    public Reports(MainSpigot pl) {
        this.plugin = pl;
        Objects.requireNonNull(plugin.getCommand("report")).setExecutor(this);
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String report = plugin.getConfig().getString("report_channel");

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

        Player p = (Player) sender;
        Player reported;
        if (args.length > 1) {

            reported = Bukkit.getPlayer(args[0]);
            assert reported != null;
            String playerName = reported.getName();

            StringBuilder content = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                content.append(" ").append(args[i]);
            }

            report_successfully = report_successfully.replace("{player}", playerName);
            report_successfully = report_successfully.replace("{reason}", String.valueOf(content));

            try {

                p.sendMessage(ChatColor.GREEN + report_successfully);
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + there_is_no_such_player);
            }

            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(ops ->
                    ops.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0f, 0f));


            String finalReport_successfully = report_successfully;
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(ops ->
                    ops.sendMessage(ChatColor.GREEN + finalReport_successfully)
            );

            EmbedBuilder emb = new EmbedBuilder();
            emb.setAuthor(report_report, null, "https://minotar.net/helm/" + p.getName() + "/300.png");
            emb.setThumbnail("https://minotar.net/helm/" + reported.getName() + "/300.png");
            emb.addField(report_applicant, p.getName(), true);
            emb.addField(report_notified, reported.getName(), true);
            emb.addField(report_reason, content.toString(), false);
            emb.setColor(Color.YELLOW);
            emb.setFooter(notification_from);
            emb.setTimestamp(Instant.now());

            assert reportChannel != null;
            reportChannel.sendMessageEmbeds(emb.build()).queue();

        } else {
            p.sendMessage(ChatColor.RED + report_correct_usage);
        }
        return true;
    }
}
