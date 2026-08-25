package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

public class StatusUpdater implements Runnable {
    private final JDA jda;
    private final LanguageLoader languageLoader;
    private final MainSpigot plugin;

    public StatusUpdater(MainSpigot plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public void run() {
        if (MainSpigot.jda != jda || jda.getStatus() != JDA.Status.CONNECTED) {
            return;
        }

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
        if (discord_status.length() > 128) {
            discord_status = discord_status.substring(0, 128);
        }

        jda.getPresence().setActivity(Activity.watching(discord_status));
        plugin.debugLog("Updated Discord presence (online players: " + pn + ")");
    }
}
