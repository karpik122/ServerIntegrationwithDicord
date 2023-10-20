package pl.karpik122.ServerIntegrationwithDicord.Spigot.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DiscordIntegrationAdminTabCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("discordintegration")) {

            if (args.length == 1) {
                completions.add("reload");
                completions.add("past");
            }

            else if (args.length == 2 && !"reload".startsWith(args[0])) {
                completions.add("token");
                completions.add("log");
                completions.add("report");
            }
        }
        return completions;
    }

}
