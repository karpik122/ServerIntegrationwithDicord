package pl.karpik122.ServerIntegrationwithDicord.Bungee;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.Config;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.LanguageLoader;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.File.LanguageManager;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.Timer.Counter;
import pl.karpik122.ServerIntegrationwithDicord.Bungee.Util.UpdateChecker;

import java.util.Timer;


public class MainBungee extends Plugin {
    LanguageLoader langLoader;
    Config config;
    public static JDA jda;

    @SneakyThrows
    @Override
    public void onEnable() {
        config = Config.getInstance(this);

        String TOKEN = (String) config.getConfig().get("TOKEN");
        langLoader = new LanguageLoader(this);
        LanguageManager.initialize(this);

        String there_is_not_a_new_update = langLoader.getTranslation("there_is_not_a_new_update");
        String there_is_a_new_update = langLoader.getTranslation("there_is_a_new_update");
        String loading = langLoader.getTranslation("loading");
        String Log_in_for = langLoader.getTranslation("Log_in_for");

        new UpdateChecker(this, 111094).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info(there_is_not_a_new_update);
            } else {
                getLogger().info(there_is_a_new_update);
                getLogger().info(ChatColor.GREEN + "https://www.spigotmc.org/resources/server-integration-with-discord.111094/");
            }
        });

        Timer timer = new Timer();

        if (TOKEN == null || TOKEN.equals("BOT_TOKEN")) {
            getLogger().warning("Invalid TOKEN in config.yml file. I turn off the plugin...");
            timer.schedule(new Counter(this), 0L, 60000L);
            return;
        }
        timer.schedule(new Counter(this), 0L, 60000L);
        getLogger().info(ChatColor.RED + "Server Integration with Dicord - " + loading);
        runBot();

        Log_in_for = Log_in_for.replace("{botName}", jda.getSelfUser().getName());
        Log_in_for = Log_in_for.replace("{botID}",  jda.getSelfUser().getId());

        getLogger().info(ChatColor.GREEN + "Server Integration with Dicord - " + Log_in_for);

        getLogger().info("");
        getLogger().info("");
        getLogger().info(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        getLogger().info(ChatColor.AQUA + "░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░");
        getLogger().info(ChatColor.AQUA + "██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗");
        getLogger().info(ChatColor.AQUA + "╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║");
        getLogger().info(ChatColor.AQUA + "░╚═══██╗░██║░░░████╔═████║░░██║░░██║");
        getLogger().info(ChatColor.AQUA + "██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝");
        getLogger().info(ChatColor.AQUA + "╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░");
        getLogger().info(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        getLogger().info("");


    }

    @Override
    public void onDisable() {
        getLogger().info("");
        getLogger().info("");
        getLogger().info(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        getLogger().info(ChatColor.AQUA + "░██████╗░██╗░░██╗░░░░░░░██╗░██████╗░");
        getLogger().info(ChatColor.AQUA + "██╔════╝░██║░░██║░░██╗░░██║░██╔══██╗");
        getLogger().info(ChatColor.AQUA + "╚█████╗░░██║░░╚██╗████╗██╔╝░██║░░██║");
        getLogger().info(ChatColor.AQUA + "░╚═══██╗░██║░░░████╔═████║░░██║░░██║");
        getLogger().info(ChatColor.AQUA + "██████╔╝░██║░░░╚██╔╝░╚██╔╝░░██████╔╝");
        getLogger().info(ChatColor.AQUA + "╚═════╝░░╚═╝░░░░╚═╝░░░╚═╝░░░╚═════╝░");
        getLogger().info(ChatColor.AQUA + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        getLogger().info("");

        jda.shutdown();
    }

    public void runBot() {

        String TOKEN = (String) config.getConfig().get("TOKEN");
        try {
            jda = JDABuilder.createDefault(TOKEN, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.MESSAGE_CONTENT, GatewayIntent.SCHEDULED_EVENTS)
                    .addEventListeners()
                    .build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
