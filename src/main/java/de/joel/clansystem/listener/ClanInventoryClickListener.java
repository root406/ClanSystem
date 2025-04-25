package de.joel.clansystem.listener;

import de.joel.clansystem.manager.ClanManager;
import de.joel.clansystem.manager.AdminManager;
import de.joel.clansystem.ClanSystem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Map;

public class ClanInventoryClickListener implements Listener {

    private final AdminManager adminManager;
    private final ClanManager clanManager;

    public ClanInventoryClickListener(AdminManager adminManager, ClanManager clanManager) {
        this.adminManager = adminManager;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (event.getView().getTitle().startsWith("Clans - Seite")) {
            event.setCancelled(true); // Verhindere, dass Gegenstände verschoben werden

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            Player player = (Player) event.getWhoClicked();
            String title = clickedItem.getItemMeta().getDisplayName();

            if (title.equals("Vorherige Seite")) {
                int page = Integer.parseInt(event.getView().getTitle().split(" ")[3]) - 2;
                openClanListInventory(player, page);
            } else if (title.equals("Nächste Seite")) {
                int page = Integer.parseInt(event.getView().getTitle().split(" ")[3]);
                openClanListInventory(player, page);
            } else {
                String clanName = title.split(" ")[0];
                // Öffne ein neues Inventar, um die Bann- und Lösch-Optionen anzuzeigen
                Inventory actionInventory = Bukkit.createInventory(null, 9, "Clan Aktionen");

                ItemStack banItem = new ItemStack(Material.REDSTONE_BLOCK);
                ItemMeta banMeta = banItem.getItemMeta();
                banMeta.setDisplayName("Clan bannen");
                banItem.setItemMeta(banMeta);

                ItemStack deleteItem = new ItemStack(Material.TNT);
                ItemMeta deleteMeta = deleteItem.getItemMeta();
                deleteMeta.setDisplayName("Clan löschen");
                deleteItem.setItemMeta(deleteMeta);

                actionInventory.setItem(3, banItem);
                actionInventory.setItem(5, deleteItem);

                player.openInventory(actionInventory);

                // Speichere den Clan-Namen im Spieler-Objekt, um später darauf zuzugreifen
                player.setMetadata("selectedClan", new FixedMetadataValue(ClanSystem.getInstance(), clanName));
            }
        }
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

            ItemStack clanItem = new ItemStack(Material.PAPER);
            ItemMeta meta = clanItem.getItemMeta();
            meta.setDisplayName(clanNameAndTag);
            meta.setLore(List.of("Leader: " + leaderName, "Mitglieder: " + memberCount));
            clanItem.setItemMeta(meta);

            clanInventory.setItem(slot, clanItem);
            slot++;
        }

        // Navigation items
        if (page > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta meta = previousPage.getItemMeta();
            meta.setDisplayName("Vorherige Seite");
            previousPage.setItemMeta(meta);
            clanInventory.setItem(45, previousPage);
        }

        if (page < maxPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("Nächste Seite");
            nextPage.setItemMeta(meta);
            clanInventory.setItem(53, nextPage);
        }

        player.openInventory(clanInventory);
    }
}