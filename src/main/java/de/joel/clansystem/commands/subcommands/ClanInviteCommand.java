package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ClanInviteCommand implements SubCommand {

    private final HashMap<UUID, String> pendingInvites; // Einladungsliste

    public ClanInviteCommand(HashMap<UUID, String> pendingInvites) {
        this.pendingInvites = pendingInvites;
    }

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Verwendung: /clan invite <Spieler>");
            return;
        }

        UUID senderUUID = sender.getUniqueId();
        String clanName = clanManager.getClanName(senderUUID);

        if (clanName == null) {
            sender.sendMessage(ChatColor.RED + "Du bist in keinem Clan!");
            return;
        }

        String rank = clanManager.getRank(senderUUID);
        if (rank == null || (!rank.equalsIgnoreCase("LEADER")  && !rank.equalsIgnoreCase("CO_LEADER") && !rank.equalsIgnoreCase("MODERATOR"))) {
            sender.sendMessage(ChatColor.RED + "Nur Clan-Leader oder Moderatoren können einladen!");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Dieser Spieler ist nicht online!");
            return;
        }

        UUID targetUUID = target.getUniqueId();
        if (clanManager.isInClan(targetUUID)) {
            sender.sendMessage(ChatColor.RED + "Dieser Spieler ist bereits in einem Clan!");
            return;
        }

        // Einladung speichern
        pendingInvites.put(targetUUID, clanName);

        // Klickbare Einladung senden
        target.sendMessage(ChatColor.GRAY + sender.getName() + " hat dich in den Clan " + ChatColor.AQUA + clanName + ChatColor.GRAY + " eingeladen!");

        TextComponent accept = new TextComponent(ChatColor.GREEN + "[ANNEHMEN]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept"));

        TextComponent deny = new TextComponent(ChatColor.RED + "[ABLEHNEN]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan deny"));

        target.spigot().sendMessage(accept, new TextComponent(" "), deny);
        sender.sendMessage(ChatColor.GREEN + "Du hast " + target.getName() + " in deinen Clan eingeladen!");
    }
}
