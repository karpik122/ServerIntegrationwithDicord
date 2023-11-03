package pl.karpik122.ServerIntegrationwithDicord.Bungee.Commands;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeLanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.bungeeLanguageManager;


public class bungeeDiscordIntegrationAdminCommand extends Command {
    private final bungeeLanguageLoader languageLoader;

    public bungeeDiscordIntegrationAdminCommand() {
        super("");

        languageLoader = bungeeLanguageManager.getInstance();
    }


    public void execute(CommandSender command, String[] args) {
    }
}