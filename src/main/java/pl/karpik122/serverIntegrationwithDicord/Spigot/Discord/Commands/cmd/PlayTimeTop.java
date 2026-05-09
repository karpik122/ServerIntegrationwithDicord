package pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.cmd;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.karpik122.serverIntegrationwithDicord.Spigot.Discord.Commands.ICommand;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayTimeTop implements ICommand {
    private final LanguageLoader languageLoader;
    private final MainSpigot pl;

    public PlayTimeTop(MainSpigot pl) {
        this.pl = pl;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public String getName() {
        return languageLoader.getTranslation("playtime_command");
    }

    @Override
    public String getDiscretion() {
        return languageLoader.getTranslation("playtime_description");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String notification_from = languageLoader.getTranslation("notification_from");

        File f = new File(pl.getDataFolder(), "playtime.yml");
        YamlConfiguration file = YamlConfiguration.loadConfiguration(f);

        // Tworzymy listę graczy z pliku
        List<Map.Entry<String, Integer>> topPlayers = new ArrayList<>();
        for (String playerName : file.getKeys(false)) {
            topPlayers.add(new AbstractMap.SimpleEntry<>(playerName, file.getInt(playerName)));
        }

        // Sortujemy wyniki od największego do najmniejszego czasu
        topPlayers.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle(languageLoader.getTranslation("playtime_top5"));
        emb.setColor(Color.YELLOW);
        emb.setFooter(notification_from);
        emb.setTimestamp(Instant.now());

        // Bot sprawdza ile jest osób. Jeśli mniej niż 5, pokaże tylko te dostępne.
        int count = 1;
        for (int i = 0; i < Math.min(5, topPlayers.size()); i++) {
            Map.Entry<String, Integer> entry = topPlayers.get(i);
            String name = entry.getKey();
            int totalMinutes = entry.getValue();

            int h = totalMinutes / 60;
            int m = totalMinutes % 60;

            emb.addField("#" + count + " " + name, h
                    + " " + languageLoader.getTranslation("playtime_h") + " " + m
                    + " " + languageLoader.getTranslation("playtime_min"), false);
            count++;
        }

        // Wiadomość gdy plik jest całkowicie pusty
        if (topPlayers.isEmpty()) {
            emb.setDescription("Nikt jeszcze nie grał.");
        }

        event.replyEmbeds(emb.build()).queue();
    }
}
