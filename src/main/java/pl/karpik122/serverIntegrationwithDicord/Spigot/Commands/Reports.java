package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.time.Instant;

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.jda;

public class Reports extends ListenerAdapter implements CommandExecutor {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public Reports(MainSpigot pl) {
        this.plugin = pl;
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

        Player p = (Player) sender;

        if (args.length > 1) {
            Player reported = Bukkit.getPlayer(args[0]);

            // Poprawne sprawdzanie, czy gracz istnieje (zamiast try-catch i assert)
            if (reported == null) {
                p.sendMessage(ChatColor.RED + there_is_no_such_player);
                return true;
            }

            String playerName = reported.getName();
            StringBuilder content = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                content.append(" ").append(args[i]);
            }

            report_successfully = report_successfully.replace("{player}", playerName);
            report_successfully = report_successfully.replace("{reason}", content.toString());

            // 1. Wiadomość dla zgłaszającego
            p.sendMessage(ChatColor.GREEN + report_successfully);

            // 2. Wiadomość i dźwięk dla OPów
            String finalReport_successfully = report_successfully;
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(ops -> {
                ops.playSound(ops.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0f, 0f);
                // Sprawdzamy, czy OP nie jest osobą, która zgłasza
                if (!ops.equals(p)) {
                    ops.sendMessage(ChatColor.GREEN + finalReport_successfully);
                }
            });

            // 3. Wysłanie na Discord
            if (jda != null && report != null) {
                TextChannel reportChannel = jda.getTextChannelById(report);
                if (reportChannel != null) {
                    EmbedBuilder emb = new EmbedBuilder();
                    emb.setAuthor(report_report, null, "https://minotar.net/helm/" + p.getName() + "/300.png");
                    emb.setThumbnail("https://minotar.net/helm/" + reported.getName() + "/300.png");
                    emb.addField(report_applicant, p.getName(), true);
                    emb.addField(report_notified, reported.getName(), true);
                    emb.addField(report_reason, content.toString(), false);
                    emb.setColor(Color.YELLOW);
                    emb.setFooter(notification_from);
                    emb.setTimestamp(Instant.now());

                    if (plugin.getConfig().getBoolean("discordadmininteraction")) {
                        reportChannel.sendMessageEmbeds(emb.build()).addComponents(ActionRow.of(
                                        Button.danger("ban:" + playerName, "Ban " + playerName),
                                        Button.primary("kick:" + playerName, "Kick " + playerName)
                                )
                        ).queue();
                    } else {
                        reportChannel.sendMessageEmbeds(emb.build()).queue();
                    }
                }
            }
        } else {
            p.sendMessage(ChatColor.RED + report_correct_usage);
        }
        return true;
    }
}
