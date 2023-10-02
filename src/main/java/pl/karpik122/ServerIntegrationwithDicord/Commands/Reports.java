package pl.karpik122.ServerIntegrationwithDicord.Commands;

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
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class Reports implements Listener, CommandExecutor {
    JDA jda = Main.jda;
    private final Main plugin;
    private final LanguageLoader languageLoader;
    public Reports(Main pl) {
        this.plugin = pl;
        plugin.getCommand("report").setExecutor(this);
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String report = plugin.getConfig().getString("report_channel");
        String report_successfully_reported = languageLoader.getTranslation("report_successfully_reported");
        String report_successfully_for = languageLoader.getTranslation("report_report_player");
        String report_for = languageLoader.getTranslation("report_for");
        String there_is_no_such_player = languageLoader.getTranslation("there_is_no_such_player");
        String report_was_reported_for = languageLoader.getTranslation("report_was_reported_for");
        String player = languageLoader.getTranslation("player");
        String report_applicant = languageLoader.getTranslation("report_applicant");
        String report_notified = languageLoader.getTranslation("report_notified");
        String report_content = languageLoader.getTranslation("report_content");
        String report_notification_from = languageLoader.getTranslation("report_notification_from");
        String report_incorrect_syntax = languageLoader.getTranslation("report_incorrect_syntax");
        String report_correct_usage = languageLoader.getTranslation("report_correct_usage");


        assert report != null;
        TextChannel reportChannel = jda.getTextChannelById(report);

        Player p = (Player) sender;
        Player reported;
        if (args.length > 1) {

            reported = Bukkit.getPlayer(args[0]);

            StringBuilder content = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                content.append(" ").append(args[i]);
            }

            try {
                assert reported != null;
                p.sendMessage(ChatColor.GREEN + report_successfully_reported + " " + ChatColor.RED + reported.getName() + ChatColor.GREEN + " " + report_for + " " + ChatColor.RED + content);
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + there_is_no_such_player);
            }

            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(ops ->
                    ops.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0f, 0f));

            String finalContent = content.toString();
            Player finalReported = reported;
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(ops ->
                    ops.sendMessage(ChatColor.GREEN + player + " " + ChatColor.RED + finalReported.getName() + ChatColor.GREEN + " " + report_was_reported_for + ChatColor.RED + finalContent));

            EmbedBuilder emb = new EmbedBuilder();
            emb.setAuthor(report_successfully_for, null, "https://minotar.net/helm/" + p.getName() + "/300.png");
            emb.setThumbnail("https://minotar.net/helm/" + reported.getName() + "/300.png");
            emb.addField(report_applicant, p.getName(), true);
            emb.addField(report_notified, reported.getName(), true);
            emb.addField(report_content, content.toString(), false);
            emb.setColor(Color.YELLOW);
            emb.setFooter(report_notification_from);
            emb.setTimestamp(Instant.now());


            assert reportChannel != null;
            reportChannel.sendMessageEmbeds(emb.build()).queue();
        }else {
            p.sendMessage(ChatColor.RED + report_incorrect_syntax + " \n " + ChatColor.GREEN + report_correct_usage);
        }
        return true;
    }
}
