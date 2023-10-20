package pl.karpik122.ServerIntegrationwithDicord.Spigot.Events;

import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Spigot.MainSpigot;

public class StatusUpdater extends TimerTask {
    JDA jda = MainSpigot.jda;
    private final LanguageLoader languageLoader;
    public MainSpigot plugin;
    public StatusUpdater(MainSpigot pl) {
        this.plugin = pl;
        languageLoader = LanguageManager.getInstance();
    }

    public void run() {
        String discord_status = languageLoader.getTranslation("discord_status");

        int pn = Bukkit.getServer().getOnlinePlayers().size();
        int ps = Bukkit.getServer().getMaxPlayers();
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
