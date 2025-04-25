package de.joel.clansystem.listener;

import de.joel.clansystem.manager.ClanManager;
import de.joel.clansystem.manager.AdminManager;
import de.joel.clansystem.ClanSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.ChatColor;

public class ClanActionClickListener implements Listener {

    private final AdminManager adminManager;
    private final ClanManager clanManager;

    public ClanActionClickListener(AdminManager adminManager, ClanManager clanManager) {
        this.adminManager = adminManager;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (event.getView().getTitle().equals("Clan Aktionen")) {
            event.setCancelled(true); // Verhindere, dass Gegenstände verschoben werden

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            Player player = (Player) event.getWhoClicked();
            String clanName = null;

            for (MetadataValue value : player.getMetadata("selectedClan")) {
                if (value.getOwningPlugin() == ClanSystem.getInstance()) {
                    clanName = value.asString();
                    break;
                }
            }

            if (clanName == null) return;

            // Entferne Farbcodes aus dem Clanname
            String cleanClanName = ChatColor.stripColor(clanName);

            String action = clickedItem.getItemMeta().getDisplayName();

            if (action.equals("Clan bannen")) {
                adminManager.banClan(cleanClanName);
                player.sendMessage("§aClan §e" + cleanClanName + " §awurde gebannt!");
            } else if (action.equals("Clan löschen")) {
                boolean success = adminManager.deleteClan(cleanClanName);
                if (success) {
                    player.sendMessage("§aClan §e" + cleanClanName + " §awurde gelöscht!");
                } else {
                    player.sendMessage("§cFehler beim Löschen des Clans §e" + cleanClanName + "§c.");
                }
            }

            player.closeInventory();
        }
    }
}