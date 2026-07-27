package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event.ChatFlagWords;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import static pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot.jda;


public class DiscordIntegrationAdminCommand implements CommandExecutor {

    private final MainSpigot pl;
    private final LanguageLoader languageLoader;

    public DiscordIntegrationAdminCommand(MainSpigot pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String remember_to_restart = languageLoader.getTranslation("remember_to_restart");

        String admin_command_help = languageLoader.getTranslation("admin_command_help");
        String admin_command_reload = languageLoader.getTranslation("admin_command_reload");
        String admin_command_past_token = languageLoader.getTranslation("admin_command_past_token");
        String admin_command_past_log = languageLoader.getTranslation("admin_command_past_log");
        String admin_command_past_report = languageLoader.getTranslation("admin_command_past_report");
        String admin_command_health = languageLoader.getTranslation("admin_command_health");
        String admin_command_accept = languageLoader.getTranslation("admin_command_accept");

        if (sender.hasPermission("serverintegrationwithdicord.admin")) {

            if (command.getName().equalsIgnoreCase("discordintegration")) {
                if (args.length < 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /discordintegration <reload | health | past> [token | log | report] <value>");
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(ChatColor.GREEN + "Reload...");
                    sender.sendMessage("");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
                    sender.sendMessage(ChatColor.AQUA + "░░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░░");
                    sender.sendMessage(ChatColor.AQUA + "░██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗░");
                    sender.sendMessage(ChatColor.AQUA + "░╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║░");
                    sender.sendMessage(ChatColor.AQUA + "░░╚═══██╗░██║░░░████╔═████║░░██║░░██║░");
                    sender.sendMessage(ChatColor.AQUA + "░██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝░");
                    sender.sendMessage(ChatColor.AQUA + "░╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░░");
                    sender.sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
                    sender.sendMessage("");
                    pl.saveConfig();
                    pl.reloadConfig();
                    reload();
                    sender.sendMessage(ChatColor.GREEN + "Success");
                    return true;
                }

                if (args[0].equalsIgnoreCase("health")) {
                    sendHealthCheck(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("past") && args.length > 1) {
                    if (args[1].equalsIgnoreCase("token") && args.length == 3) {
                        String discordToken = args[2];
                        saveTokenToConfig(discordToken);
                        sender.sendMessage(ChatColor.GREEN + admin_command_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    } else if (args[1].equalsIgnoreCase("log") && args.length == 3) {
                        String logid = args[2];
                        saveLogIdToConfig(logid);
                        sender.sendMessage(ChatColor.GREEN + admin_command_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    } else if (args[1].equalsIgnoreCase("report") && args.length == 3) {
                        String reportid = args[2];
                        saveReportIdConfig(reportid);
                        sender.sendMessage(ChatColor.GREEN + admin_command_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + admin_command_help);
                        sender.sendMessage(ChatColor.GREEN + admin_command_reload);
                        sender.sendMessage(ChatColor.GREEN + admin_command_past_token);
                        sender.sendMessage(ChatColor.GREEN + admin_command_past_log);
                        sender.sendMessage(ChatColor.GREEN + admin_command_past_report);
                        sender.sendMessage(ChatColor.GREEN + admin_command_health);
                        return true;
                    }
                }

                sender.sendMessage(ChatColor.YELLOW + admin_command_help);
                sender.sendMessage(ChatColor.GREEN + admin_command_reload);
                sender.sendMessage(ChatColor.GREEN + admin_command_past_token);
                sender.sendMessage(ChatColor.GREEN + admin_command_past_log);
                sender.sendMessage(ChatColor.GREEN + admin_command_past_report);
                sender.sendMessage(ChatColor.GREEN + admin_command_health);
                return true;
            }
        }
        return false;
    }

    public void reload() {
        pl.stopBot();

        pl.reloadConfig();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░╚═══██╗░██║░░░████╔═████║░░██║░░██║░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage("");

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Restart");

        languageLoader.reload();
        pl.runBot();
    }

    private void sendHealthCheck(CommandSender sender) {
        String logChannelId = pl.getConfig().getString("id_log_channel", "");
        boolean logIdConfigured = !logChannelId.isBlank() && !"id".equalsIgnoreCase(logChannelId);

        String jdaStatusName = pl.getJdaStatusName();
        boolean jdaOnline = jda != null && jda.getStatus() == JDA.Status.CONNECTED;

        boolean logChannelReachable = false;
        if (jda != null && logIdConfigured) {
            TextChannel channel = jda.getTextChannelById(logChannelId);
            logChannelReachable = channel != null;
        }

        sender.sendMessage(ChatColor.GOLD + "==== Discord Integration Health Check ====");
        sender.sendMessage(ChatColor.YELLOW + "JDA status: " + statusColor(jdaOnline) + jdaStatusName);
        sender.sendMessage(ChatColor.YELLOW + "Status timer: " + statusColor(pl.isStatusTimerActive()) + boolText(pl.isStatusTimerActive()));
        sender.sendMessage(ChatColor.YELLOW + "Counter timer: " + statusColor(pl.isCounterTimerActive()) + boolText(pl.isCounterTimerActive()));
        sender.sendMessage(ChatColor.YELLOW + "Log channel ID set: " + statusColor(logIdConfigured) + boolText(logIdConfigured));
        sender.sendMessage(ChatColor.YELLOW + "Log channel reachable: " + statusColor(logChannelReachable) + boolText(logChannelReachable));
        sender.sendMessage(ChatColor.YELLOW + "Debug mode: " + statusColor(pl.isDebugEnabled()) + boolText(pl.isDebugEnabled()));
        sender.sendMessage(ChatColor.GRAY + "Configured log channel: " + (logChannelId.isBlank() ? "<empty>" : logChannelId));
    }

    private ChatColor statusColor(boolean ok) {
        return ok ? ChatColor.GREEN : ChatColor.RED;
    }

    private String boolText(boolean value) {
        return value ? "YES" : "NO";
    }

    private void saveTokenToConfig(String token) {
        FileConfiguration config = pl.getConfig();
        config.set("TOKEN", token);
        pl.saveConfig();
        reload();
    }

    private void saveLogIdToConfig(String logid) {
        FileConfiguration config = pl.getConfig();
        config.set("id_log_channel", logid);
        pl.saveConfig();
        reload();
    }


    private void saveReportIdConfig(String reportid) {
        FileConfiguration config = pl.getConfig();
        config.set("report_channel", reportid);
        pl.saveConfig();
        reload();
    }
}
