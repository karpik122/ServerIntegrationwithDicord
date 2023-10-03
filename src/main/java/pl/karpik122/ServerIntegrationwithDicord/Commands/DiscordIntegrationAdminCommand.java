package pl.karpik122.ServerIntegrationwithDicord.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;


public class DiscordIntegrationAdminCommand implements CommandExecutor {

    private final Main pl;
    private final LanguageLoader languageLoader;

    public DiscordIntegrationAdminCommand(Main pl) {
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
        String admin_command_accept = languageLoader.getTranslation("admin_command_accept");



        if (command.getName().equalsIgnoreCase("discordintegration")) {

            if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.GREEN + "Reload(...)");

                pl.saveConfig();
                pl.reloadConfig();
                reload();
                return true;
            }

            if (args[0].equalsIgnoreCase("past")) {
                if (args[1].equalsIgnoreCase("token")) {
                    if (args.length == 3) {
                        String discordToken = args[2];

                        saveTokenToConfig(discordToken);

                        sender.sendMessage(ChatColor.GREEN + admin_command_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    }
                    sender.sendMessage(ChatColor.YELLOW + admin_command_past_token);
                    return true;
                }
                if (args[1].equalsIgnoreCase("log")) {
                    if (args.length == 3) {
                        String logid = args[2];

                        saveLogIdToConfig(logid);

                        sender.sendMessage(ChatColor.GREEN + admin_command_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    }
                    sender.sendMessage(ChatColor.YELLOW + admin_command_past_log);
                    return true;
                }
                if (args[1].equalsIgnoreCase("report")) {
                    if (args.length == 3) {
                        String reportid = args[2];

                        saveReportIdConfig(reportid);

                        sender.sendMessage(ChatColor.GREEN + admin_command_accept);
                        sender.sendMessage(ChatColor.RED + remember_to_restart);
                        return true;
                    }
                    sender.sendMessage(ChatColor.YELLOW + admin_command_past_report);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + admin_command_help);
                sender.sendMessage(ChatColor.GREEN + admin_command_reload);
                sender.sendMessage(ChatColor.GREEN + admin_command_past_token);
                sender.sendMessage(ChatColor.GREEN + admin_command_past_log);
                sender.sendMessage(ChatColor.GREEN + admin_command_past_report);
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + admin_command_help);
            sender.sendMessage(ChatColor.GREEN + admin_command_reload);
            sender.sendMessage(ChatColor.GREEN + admin_command_past_token);
            sender.sendMessage(ChatColor.GREEN + admin_command_past_log);
            sender.sendMessage(ChatColor.GREEN + admin_command_past_report);

            return true;
        }
        return false;
    }

    public void reload() {

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░╚═══██╗░██║░░░████╔═████║░░██║░░██║");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Restart");

        pl.runBot();
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
