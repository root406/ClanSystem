package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanInfoCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        UUID playerUUID = sender.getUniqueId();

        if (!clanManager.isInClan(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Du bist in keinem Clan!");
            return;
        }

        String clanName = clanManager.getClanName(playerUUID);
        String clanTag = clanManager.getClanTag(playerUUID);
        int memberCount = clanManager.getClanMemberCount(clanName); // Neue Methode für Mitgliederanzahl

        sender.sendMessage(ChatColor.AQUA + "➤ Clan-Info");
        sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + clanName);
        sender.sendMessage(ChatColor.GRAY + "Kürzel: " + ChatColor.YELLOW + clanTag);
        sender.sendMessage(ChatColor.GRAY + "Mitglieder: " + ChatColor.GOLD + memberCount + "/20");
    }
}
