package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClanHelpCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "-----[ Clan-Befehle ]-----");
        sender.sendMessage(ChatColor.YELLOW + "/clan create <Name> <Tag> " + ChatColor.GRAY + "- Erstelle einen Clan");
        sender.sendMessage(ChatColor.YELLOW + "/clan invite <Spieler> " + ChatColor.GRAY + "- Lade einen Spieler ein");
        sender.sendMessage(ChatColor.YELLOW + "/clan accept " + ChatColor.GRAY + "- Trete einem Clan bei");
        sender.sendMessage(ChatColor.YELLOW + "/clan leave " + ChatColor.GRAY + "- Verlasse deinen Clan");
        sender.sendMessage(ChatColor.YELLOW + "/clan list " + ChatColor.GRAY + "- Zeigt alle Clan-Mitglieder an");
        sender.sendMessage(ChatColor.YELLOW + "/clan info " + ChatColor.GRAY + "- Zeigt Clan-Infos an");
        sender.sendMessage(ChatColor.YELLOW + "/clan kick <Spieler> " + ChatColor.GRAY + "- Entferne ein Clan-Mitglied");
        sender.sendMessage(ChatColor.YELLOW + "/clan delete " + ChatColor.GRAY + "- Beantrage die löschung deines Clans");
        sender.sendMessage(ChatColor.YELLOW + "/clan confirm " + ChatColor.GRAY + "- Bestätige die Löschung deines Clans");
        sender.sendMessage(ChatColor.YELLOW + "/clan promote " + ChatColor.GRAY + "- Promote einen Clan Mitglied und setze sein Rang höher");
        sender.sendMessage(ChatColor.YELLOW + "/clan move <Spieler> " + ChatColor.GRAY + "- Übertrage deinen Clan an einen anderen Spieler");
        sender.sendMessage(ChatColor.YELLOW + "/clan help " + ChatColor.GRAY + "- Zeigt diese Hilfe an");
        sender.sendMessage(ChatColor.GOLD + "-------------------------");
    }
}
