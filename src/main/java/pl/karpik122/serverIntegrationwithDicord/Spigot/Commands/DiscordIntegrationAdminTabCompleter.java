package pl.karpik122.serverIntegrationwithDicord.Spigot.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiscordIntegrationAdminTabCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("serverintegrationwithdicord.admin")) {
            return List.of();
        }

        List<String> candidates = new ArrayList<>();
        if (args.length == 1) {
            candidates.addAll(List.of("reload", "health", "set", "past"));
        } else if (args.length == 2
                && ("set".equalsIgnoreCase(args[0]) || "past".equalsIgnoreCase(args[0]))) {
            candidates.addAll(List.of("token", "guild", "log", "report"));
        }

        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        return candidates.stream()
                .filter(candidate -> candidate.startsWith(prefix))
                .toList();
    }
}
