package pl.karpik122.ServerIntegrationwithDicord.Events;

import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Main;

public class StatusUpdater extends TimerTask {
    JDA jda = Main.jda;
    private final LanguageLoader languageLoader;
    public Main plugin;
    public StatusUpdater(Main pl) {
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

        jda.getPresence().setActivity(Activity.watching(pn + " " + discord_status));
    }
}
