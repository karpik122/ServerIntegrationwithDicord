package pl.karpik122.ServerIntegrationwithDicord.Bungee.Events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.MainBungee;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

import java.util.TimerTask;

public class bungeeStatusUpdater extends TimerTask {
    JDA jda = MainSpigot.jda;
    private LanguageLoader languageLoader;
    public MainBungee plugin;
    public bungeeStatusUpdater() {
        languageLoader = LanguageManager.getInstance();
    }

    public void run() {
        String discord_status = languageLoader.getTranslation("discord_status");

        int pn = plugin.getProxy().getOnlineCount();
        int ps = 0;

        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            ps += server.getPlayers().size();
        }

        if (pn == 0) {
            jda.getPresence().setStatus(OnlineStatus.IDLE);
        } else if (pn == ps) {
            jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        } else {
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
        }


        discord_status = discord_status.replace("{player_count}", String.valueOf(pn));

        jda.getPresence().setActivity(Activity.watching(discord_status));
    }
}
