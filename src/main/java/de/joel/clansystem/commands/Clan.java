package de.joel.clansystem.commands;

import de.joel.clansystem.commands.subcommands.*;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Clan implements CommandExecutor {

    private final ClanManager clanManager;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final HashMap<UUID, String> pendingInvites = new HashMap<>(); // Einladungsliste

    public Clan(ClanManager clanManager) {
        this.clanManager = clanManager;

        // Subcommands registrieren
        subCommands.put("kick", new ClanKickCommand());
        subCommands.put("create", new ClanCreateCommand());
        subCommands.put("delete", new ClanDeleteCommand());
        subCommands.put("confirm", new ClanConfirmCommand());
        subCommands.put("invite", new ClanInviteCommand(pendingInvites));
        subCommands.put("accept", new ClanAcceptCommand(pendingInvites));
        subCommands.put("deny", new ClanDenyCommand(pendingInvites));
        subCommands.put("promote", new ClanPromoteCommand());
        subCommands.put("info", new ClanInfoCommand());
        subCommands.put("list", new ClanListCommand());
        subCommands.put("move", new ClanMoveCommand());
        subCommands.put("help", new ClanHelpCommand());
        subCommands.put("leave", new ClanLeaveCommand());
        // subCommands.put("ban", new ClanBanCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Verwendung: /clan <kick|ban|invite|promote|accept|deny>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        SubCommand cmd = subCommands.get(subCommand);

        if (cmd == null) {
            sender.sendMessage("Unbekanntes Sub-Command.");
            return true;
        }

        cmd.execute((Player) sender, clanManager, args);
        return true;
    }
}
