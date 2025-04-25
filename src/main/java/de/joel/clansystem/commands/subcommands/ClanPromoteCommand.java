package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanPromoteCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Verwendung: /clan promote <Spieler>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Der Spieler ist nicht online!");
            return;
        }

        UUID senderUUID = sender.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        if (!clanManager.isInClan(senderUUID) || !clanManager.isInClan(targetUUID)) {
            sender.sendMessage(ChatColor.RED + "Beide Spieler müssen in einem Clan sein!");
            return;
        }

        if (!clanManager.getClanName(senderUUID).equals(clanManager.getClanName(targetUUID))) {
            sender.sendMessage(ChatColor.RED + "Der Spieler ist nicht in deinem Clan!");
            return;
        }

        if (!clanManager.canPromote(senderUUID, targetUUID)) {
            sender.sendMessage(ChatColor.RED + "Du kannst diesen Spieler nicht befördern!");
            return;
        }

        String newRank = clanManager.getNextRank(clanManager.getRank(targetUUID));
        if (newRank == null) {
            sender.sendMessage(ChatColor.RED + "Dieser Spieler kann nicht weiter befördert werden!");
            return;
        }

        clanManager.promotePlayer(targetUUID, newRank);
        sender.sendMessage(ChatColor.GREEN + target.getName() + " wurde zum " + newRank + " befördert!");
        target.sendMessage(ChatColor.GOLD + "Du wurdest zum " + newRank + " befördert!");
    }
}
