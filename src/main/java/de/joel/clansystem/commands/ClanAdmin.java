package de.joel.clansystem.commands;

import de.joel.clansystem.commands.subcommands.admin.ClanAdminBanCommand;
import de.joel.clansystem.commands.subcommands.admin.ClanAdminDeleteCommand;
import de.joel.clansystem.commands.subcommands.admin.ClanAdminListCommand;
import de.joel.clansystem.commands.subcommands.admin.ClanAdminUnbanCommand;
import de.joel.clansystem.manager.AdminManager;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ClanAdmin implements CommandExecutor {

    private final AdminManager adminManager;
    private final ClanManager clanManager;
    private final Map<String, AdminSubCommand> subCommands = new HashMap<>();

    public ClanAdmin(AdminManager adminManager, ClanManager clanManager) {
        this.adminManager = adminManager;
        this.clanManager = clanManager;

        // ✅ Subcommands registrieren
        subCommands.put("ban", new ClanAdminBanCommand());
        subCommands.put("unban", new ClanAdminUnbanCommand());
        subCommands.put("delete", new ClanAdminDeleteCommand());
        subCommands.put("list", new ClanAdminListCommand(clanManager, adminManager));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern ausgeführt werden.");
            return true;
        }

        Player player = (Player) sender;

        // ❌ Admin-Permission checken
        if (!player.hasPermission("clansystem.admin")) {
            player.sendMessage("§cDazu hast du keine Rechte!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cVerwendung: /clanadmin <ban|unban|delete|list>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        AdminSubCommand cmd = subCommands.get(subCommand);

        if (cmd == null) {
            player.sendMessage("§cUnbekanntes Admin-Subcommand.");
            return true;
        }

        cmd.execute(player, adminManager, args);
        return true;
    }
}