package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanMoveCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        // Check, ob der Befehl richtig genutzt wird
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Benutzung: /clan move <Spieler>");
            return;
        }

        // Überprüfen, ob der Spieler in einem Clan ist
        UUID senderUUID = sender.getUniqueId();
        String clanName = clanManager.getClanName(senderUUID);

        if (clanName == null) {
            sender.sendMessage(ChatColor.RED + "Du bist in keinem Clan!");
            return;
        }

        // Überprüfen, ob der Sender der Clan-Leader ist
        String senderRank = clanManager.getRank(senderUUID);
        if (!"LEADER".equalsIgnoreCase(senderRank)) {
            sender.sendMessage(ChatColor.RED + "Nur der Clan-Leader kann den Clan übertragen!");
            return;
        }

        // Zielspieler finden
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden oder offline!");
            return;
        }

        UUID targetUUID = target.getUniqueId();

        // Überprüfen, ob der Zielspieler im gleichen Clan ist
        String targetClan = clanManager.getClanName(targetUUID);
        if (targetClan == null || !targetClan.equalsIgnoreCase(clanName)) {
            sender.sendMessage(ChatColor.RED + "Der Spieler ist nicht in deinem Clan!");
            return;
        }

        // Neuen Leader setzen und alten Leader aus dem Clan entfernen
        clanManager.setPlayerRank(targetUUID, "LEADER"); // Neuer Leader
        clanManager.removePlayerFromClan(senderUUID); // Alten Leader entfernen

        sender.sendMessage(ChatColor.GREEN + "Du hast die Clanführung an " + target.getName() + " übertragen!");
        target.sendMessage(ChatColor.GOLD + "Du bist jetzt der Anführer des Clans '" + clanName + "'!");
    }
}
