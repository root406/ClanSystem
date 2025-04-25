package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanLeaveCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        UUID playerUUID = sender.getUniqueId();

        if (!clanManager.isInClan(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Du bist in keinem Clan!");
            return;
        }

        if (clanManager.isLeader(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Du bist der Leader! Übertrage erst die Leitung oder lösche den Clan.");
            return;
        }

        clanManager.removePlayerFromClan(playerUUID);
        sender.sendMessage(ChatColor.GREEN + "Du hast den Clan verlassen!");
    }
}
