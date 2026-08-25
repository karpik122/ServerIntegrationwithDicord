package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageLoader;
import pl.karpik122.serverIntegrationwithDicord.Spigot.File.LanguageManager;
import pl.karpik122.serverIntegrationwithDicord.Spigot.MainSpigot;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountLink implements CommandExecutor {
    private static final char[] CODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ConcurrentMap<String, PendingLink> PENDING_CODES = new ConcurrentHashMap<>();

    private final MainSpigot plugin;
    private final LanguageLoader languageLoader;

    public AccountLink(MainSpigot plugin) {
        this.plugin = plugin;
        languageLoader = LanguageManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(languageLoader.getTranslation("only_player"));
            return true;
        }

        clearExpiredCodes();
        PENDING_CODES.entrySet().removeIf(entry -> entry.getValue().playerId().equals(p.getUniqueId()));

        String code = generateUniqueCode();
        long ttlMinutes = Math.clamp(plugin.getConfig().getLong("link_code_ttl_minutes", 10L), 1L, 1440L);
        long expiresAt = System.currentTimeMillis() + Duration.ofMinutes(ttlMinutes).toMillis();
        PENDING_CODES.put(code, new PendingLink(p.getUniqueId(), expiresAt));

        TextComponent message = new TextComponent(languageLoader.getTranslation("account_link") + code);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(languageLoader.getTranslation("copy_link"))));
        p.spigot().sendMessage(message);
        p.sendMessage(languageLoader.getTranslation("use_in_discord"));

        return true;
    }

    public static UUID consumeCode(String rawCode) {
        if (rawCode == null) {
            return null;
        }

        PendingLink pendingLink = PENDING_CODES.remove(rawCode.trim().toUpperCase(Locale.ROOT));
        if (pendingLink == null || pendingLink.expiresAtMillis() < System.currentTimeMillis()) {
            return null;
        }
        return pendingLink.playerId();
    }

    public static int getPendingCodeCount() {
        clearExpiredCodes();
        return PENDING_CODES.size();
    }

    public static void clearAllCodes() {
        PENDING_CODES.clear();
    }

    private static String generateUniqueCode() {
        String code;
        do {
            StringBuilder builder = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                builder.append(CODE_CHARACTERS[RANDOM.nextInt(CODE_CHARACTERS.length)]);
            }
            code = builder.toString();
        } while (PENDING_CODES.containsKey(code));
        return code;
    }

    private static void clearExpiredCodes() {
        long now = System.currentTimeMillis();
        PENDING_CODES.entrySet().removeIf(entry -> entry.getValue().expiresAtMillis() < now);
    }

    private record PendingLink(UUID playerId, long expiresAtMillis) {
    }
}
