package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ClanListCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        UUID playerUUID = sender.getUniqueId();

        if (!clanManager.isInClan(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Du bist in keinem Clan!");
            return;
        }

        String clanName = clanManager.getClanName(playerUUID);
        Map<String, String> clanMembers = clanManager.getClanMembersWithRanks(clanName); // Neue Methode

        sender.sendMessage(ChatColor.AQUA + "➤ Clan-Mitglieder: " + ChatColor.GREEN + clanName);
        for (Map.Entry<String, String> entry : clanMembers.entrySet()) {
            sender.sendMessage(ChatColor.GRAY + entry.getKey() + ChatColor.GOLD + " (" + entry.getValue() + ")");
        }
    }
}
