package de.joel.clansystem.commands.subcommands.admin;

import de.joel.clansystem.ClanSystem;
import de.joel.clansystem.commands.AdminSubCommand;
import de.joel.clansystem.manager.ClanManager;
import de.joel.clansystem.manager.AdminManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class ClanAdminListCommand implements AdminSubCommand {

    private final ClanManager clanManager;
    private final AdminManager adminManager;

    public ClanAdminListCommand(ClanManager clanManager, AdminManager adminManager) {
        this.clanManager = clanManager;
        this.adminManager = adminManager;
    }

    @Override
    public void execute(Player player, AdminManager adminManager, String[] args) {
        int page = 0;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cUngültige Seitenzahl!");
                return;
            }
        }
        openClanListInventory(player, page);
    }

    public void openClanListInventory(Player player, int page) {
        int invSize = 54;
        Inventory clanInventory = Bukkit.createInventory(null, invSize, "Clans - Seite " + (page + 1));

        Map<String, String> clansWithLeaders = adminManager.getAllClansWithLeaders();
        int totalClans = clansWithLeaders.size();
        int clansPerPage = 45;
        int maxPages = (int) Math.ceil((double) totalClans / clansPerPage);

        if (page >= maxPages || page < 0) {
            player.sendMessage("§cUngültige Seite!");
            return;
        }

        int startIndex = page * clansPerPage;
        int endIndex = Math.min(startIndex + clansPerPage, totalClans);

        int slot = 0;
        for (Map.Entry<String, String> entry : clansWithLeaders.entrySet().stream().skip(startIndex).limit(clansPerPage).toList()) {
            String clanNameAndTag = entry.getKey();
            String leaderName = entry.getValue();
            int memberCount = clanManager.getClanMemberCount(clanNameAndTag.split(" ")[0]);

            String clanName = clanNameAndTag.split(" \\[")[0];
            String clanTag = clanNameAndTag.split(" \\[")[1].replace("]", "");

            ItemStack clanItem = new ItemStack(Material.PAPER);
            ItemMeta meta = clanItem.getItemMeta();
            meta.setDisplayName("§6" + clanName + " §7[§e" + clanTag + "§7]");
            // Speichere den Clanname ohne Farbcodes im PersistentDataContainer
            meta.getPersistentDataContainer().set(new NamespacedKey(ClanSystem.getInstance(), "cleanClanName"), PersistentDataType.STRING, clanName);
            meta.setLore(List.of(
                    "§7Leader: §a" + leaderName,
                    "§7Mitglieder: §a" + memberCount,
                    "§7Klicke für Aktionen"
            ));
            clanItem.setItemMeta(meta);

            clanInventory.setItem(slot, clanItem);
            slot++;
        }

        // Navigation items
        if (page > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta meta = previousPage.getItemMeta();
            meta.setDisplayName("§aVorherige Seite");
            previousPage.setItemMeta(meta);
            clanInventory.setItem(45, previousPage);
        }

        if (page < maxPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("§aNächste Seite");
            nextPage.setItemMeta(meta);
            clanInventory.setItem(53, nextPage);
        }

        player.openInventory(clanInventory);
    }
}