package pl.karpik122.ServerIntegrationwithDicord.Commands;

import net.dv8tion.jda.api.JDA;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;
import sun.jvmstat.perfdata.monitor.CountedTimerTaskUtils;

import java.util.Timer;
import java.util.TimerTask;


public class DiscordIntegrationAdminCommand implements CommandExecutor {

    private final Main pl;
    private final LanguageLoader languageLoader;
    JDA jda = Main.jda;

    public DiscordIntegrationAdminCommand(Main pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String remember_to_restart = languageLoader.getTranslation("remember_to_restart");
        String changes_saved_successfully = languageLoader.getTranslation("changes_saved_successfully");
        String admin_command_0arg = languageLoader.getTranslation("admin_command_0arg");
        String admin_command_0arg1 = languageLoader.getTranslation("admin_command_0arg1");
        String admin_command_0arg2 = languageLoader.getTranslation("admin_command_0arg2");
        String admin_command_0arg3 = languageLoader.getTranslation("admin_command_0arg3");
        String admin_command_0arg4 = languageLoader.getTranslation("admin_command_0arg4");
        String admin_command_reload = languageLoader.getTranslation("admin_command_reload");
        String admin_command_token_past = languageLoader.getTranslation("admin_command_token_past");
        String admin_command_token_accept = languageLoader.getTranslation("admin_command_token_accept");
        String admin_command_logid_past = languageLoader.getTranslation("admin_command_logid_past");
        String admin_command_report_past = languageLoader.getTranslation("admin_command_report_past");


        if (command.getName().equalsIgnoreCase("discordintegration")) {
            // Sprawdź, czy komenda jest poprawnie używana, np. /discordintegration reload

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                // Wywołaj funkcję do przeładowania integracji z Discordem
                sender.sendMessage("Reload(...)");

                pl.saveConfig();
                pl.reloadConfig();
                jda.shutdown();

                pl.onEnable();

                sender.sendMessage(ChatColor.GREEN + admin_command_reload);
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("past")) {
                if (args[1].equalsIgnoreCase("token")) {
                    if (args.length == 3) {
                        String discordToken = args[2];

                        saveTokenToConfig(discordToken);

                        sender.sendMessage(ChatColor.GREEN + admin_command_token_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                    }
                    sender.sendMessage(admin_command_token_past);
                    sender.sendMessage(admin_command_report_past);
                    sender.sendMessage(admin_command_logid_past);
                    return true;
                }
                if (args[1].equalsIgnoreCase("log")) {
                    if (args.length == 3) {
                        String logid = args[2];

                        saveLogIdToConfig(logid);

                        sender.sendMessage(ChatColor.GREEN + changes_saved_successfully);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    }
                    sender.sendMessage(admin_command_token_past);
                    sender.sendMessage(admin_command_report_past);
                    sender.sendMessage(admin_command_logid_past);
                    return true;
                }
                if (args[1].equalsIgnoreCase("report")) {
                    if (args.length == 3) {
                        String reportid = args[2];

                        saveReportIdConfig(reportid);

                        sender.sendMessage(ChatColor.GREEN + changes_saved_successfully);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    }
                    sender.sendMessage(admin_command_token_past);
                    sender.sendMessage(admin_command_report_past);
                    sender.sendMessage(admin_command_logid_past);
                    return true;
                }
                sender.sendMessage(admin_command_token_past);
                sender.sendMessage(admin_command_report_past);
                sender.sendMessage(admin_command_logid_past);
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + admin_command_0arg);
            sender.sendMessage(ChatColor.GREEN + admin_command_0arg1);
            sender.sendMessage(ChatColor.GREEN + admin_command_0arg2);
            sender.sendMessage(ChatColor.GREEN + admin_command_0arg3);
            sender.sendMessage(ChatColor.GREEN + admin_command_0arg4);
            return true;
        }
        return false;
    }

    private void saveTokenToConfig(String token) {
        FileConfiguration config = pl.getConfig();
        config.set("TOKEN", token);
        pl.saveConfig();
    }

    private void saveLogIdToConfig(String logid) {
        FileConfiguration config = pl.getConfig();
        config.set("id_log_channel", logid);
        pl.saveConfig();
    }

    private void saveReportIdConfig(String reportid) {
        FileConfiguration config = pl.getConfig();
        config.set("report_channel", reportid);
        pl.saveConfig();
    }
}
