package de.joel.clansystem.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClanTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions; // Keine Tab-Vervollständigung für Konsole
        }

        if (args.length == 1) {
            // Liste aller verfügbaren Subcommands
            completions.add("kick");
            completions.add("create");
            completions.add("delete");
            completions.add("confirm");
            completions.add("invite");
            completions.add("accept");
            completions.add("deny");
            completions.add("promote");
            completions.add("info");
            completions.add("list");
            completions.add("move");
            completions.add("help");
        }

        return completions;
    }
}
