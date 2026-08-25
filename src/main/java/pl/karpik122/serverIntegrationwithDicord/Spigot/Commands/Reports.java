package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

import java.awt.Color;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Reports implements CommandExecutor {
    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public Reports(MainSpigot plugin) {
        this.plugin = plugin;
        this.languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player reporter)) {
            sender.sendMessage(languageLoader.getTranslation("only_player"));
            return true;
        }

        if (args.length < 2) {
            reporter.sendMessage(ChatColor.RED + languageLoader.getTranslation("report_correct_usage"));
            return true;
        }

        long remainingSeconds = getRemainingCooldownSeconds(reporter.getUniqueId());
        if (remainingSeconds > 0) {
            reporter.sendMessage(ChatColor.RED + languageLoader.getTranslation("report_cooldown")
                    .replace("{seconds}", String.valueOf(remainingSeconds)));
            return true;
        }

        Player reported = Bukkit.getPlayerExact(args[0]);
        if (reported == null) {
            reporter.sendMessage(ChatColor.RED + languageLoader.getTranslation("there_is_no_such_player"));
            return true;
        }

        JDA currentJda = MainSpigot.jda;
        TextChannel reportChannel = findReportChannel(currentJda);
        if (currentJda == null || currentJda.getStatus() != JDA.Status.CONNECTED
                || reportChannel == null || !reportChannel.canTalk()) {
            reporter.sendMessage(ChatColor.RED + languageLoader.getTranslation("report_unavailable"));
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(languageLoader.getTranslation("report_report"), null,
                        "https://minotar.net/helm/" + reporter.getName() + "/300.png")
                .setThumbnail("https://minotar.net/helm/" + reported.getName() + "/300.png")
                .addField(languageLoader.getTranslation("report_applicant"), reporter.getName(), true)
                .addField(languageLoader.getTranslation("report_notified"), reported.getName(), true)
                .addField(languageLoader.getTranslation("report_reason"), reason, false)
                .setColor(Color.YELLOW)
                .setFooter(languageLoader.getTranslation("notification_from"))
                .setTimestamp(Instant.now());

        var sendAction = reportChannel.sendMessageEmbeds(embed.build());
        if (plugin.getConfig().getBoolean("discordadmininteraction")) {
            sendAction = sendAction.addComponents(ActionRow.of(
                    Button.danger("ban:" + reported.getName(), "Ban " + reported.getName()),
                    Button.primary("kick:" + reported.getName(), "Kick " + reported.getName())
            ));
        }

        long cooldownSeconds = Math.clamp(
                plugin.getConfig().getLong("report_cooldown_seconds", 30L),
                0L,
                3600L
        );
        if (cooldownSeconds > 0) {
            cooldowns.put(reporter.getUniqueId(), System.currentTimeMillis() + cooldownSeconds * 1000L);
        }

        sendAction.queue(
                ignored -> Bukkit.getScheduler().runTask(plugin,
                        () -> notifySuccessfulReport(reporter, reported, reason)),
                error -> {
                    cooldowns.remove(reporter.getUniqueId());
                    plugin.getLogger().warning("Could not send player report to Discord: " + error.getMessage());
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (reporter.isOnline()) {
                            reporter.sendMessage(ChatColor.RED
                                    + languageLoader.getTranslation("report_send_failed"));
                        }
                    });
                }
        );
        return true;
    }

    private TextChannel findReportChannel(JDA currentJda) {
        if (currentJda == null) {
            return null;
        }

        String channelId = plugin.normalizedConfigValue("report_channel");
        String guildId = plugin.normalizedConfigValue("guildID");
        if (channelId.isBlank() || guildId.isBlank()) {
            return null;
        }

        TextChannel channel = currentJda.getTextChannelById(channelId);
        if (channel == null || !channel.getGuild().getId().equals(guildId)) {
            return null;
        }
        return channel;
    }

    private void notifySuccessfulReport(Player reporter, Player reported, String reason) {
        String success = languageLoader.getTranslation("report_successfully")
                .replace("{player}", reported.getName())
                .replace("{reason}", reason);

        if (reporter.isOnline()) {
            reporter.sendMessage(ChatColor.GREEN + success);
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(ServerOperator::isOp)
                .filter(operator -> !operator.equals(reporter))
                .forEach(operator -> {
                    operator.playSound(operator.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    operator.sendMessage(ChatColor.GREEN + success);
                });
    }

    private long getRemainingCooldownSeconds(UUID playerId) {
        Long expiresAt = cooldowns.get(playerId);
        if (expiresAt == null) {
            return 0L;
        }

        long remainingMillis = expiresAt - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            cooldowns.remove(playerId);
            return 0L;
        }
        return (remainingMillis + 999L) / 1000L;
    }
}
