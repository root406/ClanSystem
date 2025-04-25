package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanKickCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cVerwendung: /clan kick <Spieler>");
            return;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = (target != null) ? target.getUniqueId() : Bukkit.getOfflinePlayer(targetName).getUniqueId();

        String senderClan = clanManager.getClanName(sender.getUniqueId());
        String targetClan = clanManager.getClanName(targetUUID);

        if (senderClan == null) {
            sender.sendMessage("§cDu bist in keinem Clan!");
            return;
        }

        if (targetClan == null || !senderClan.equals(targetClan)) {
            sender.sendMessage("§cDieser Spieler ist nicht in deinem Clan!");
            return;
        }

        // Verhindern, dass der Sender sich selbst kickt
        if (sender.getUniqueId().equals(targetUUID)) {
            sender.sendMessage("§cDu kannst dich nicht selbst aus dem Clan kicken!");
            return;
        }

        String senderRank = clanManager.getRank(sender.getUniqueId());
        String targetRank = clanManager.getRank(targetUUID);

        if (!canKick(senderRank, targetRank)) {
            sender.sendMessage("§cDu bist nicht dazu berechtigt, diesen Spieler zu kicken!");
            return;
        }

        clanManager.removePlayerFromClan(targetUUID);
        sender.sendMessage("§aDu hast " + targetName + " aus dem Clan entfernt!");

        if (target != null) {
            target.sendMessage("§cDu wurdest aus dem Clan " + senderClan + " gekickt!");
        }
    }

    private boolean canKick(String senderRank, String targetRank) {
        if ("LEADER".equals(senderRank)) return true;
        if ("CO_LEADER".equals(senderRank)) return !"LEADER".equals(targetRank);
        if ("MOD".equals(senderRank)) return "MEMBER".equals(targetRank);
        return false;
    }
}
