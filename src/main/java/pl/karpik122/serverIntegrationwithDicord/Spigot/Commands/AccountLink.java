package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountLink implements Listener, CommandExecutor {
    private final LanguageLoader languageLoader;
    public static final Map<String, UUID> pendingCodes = new HashMap<>();

    public AccountLink() {
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player p)) {
            return false;
        }

        String code = UUID.randomUUID().toString().split("-")[0];
        pendingCodes.put(code, p.getUniqueId());

        // Tworzy wiadomość z kodem (dodałem podkreślenie §n, żeby gracz wiedział, że można kliknąć)
        TextComponent message = new TextComponent(languageLoader.getTranslation("account_link") + code);

        // Dodaje akcję kopiowania do schowka po kliknięciu
        message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code));

        // Dodaje dymek z podpowiedzią po najechaniu myszką
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(languageLoader.getTranslation("copy_link"))));

        // Zwróć uwagę, że wysyłamy to używając p.spigot().sendMessage()
        p.spigot().sendMessage(message);
        p.sendMessage(languageLoader.getTranslation("use_in_discord"));

        return true;
    }
}