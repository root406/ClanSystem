package de.joel.clansystem;

import de.joel.clansystem.api.MySQL;
import de.joel.clansystem.commands.Clan;
import de.joel.clansystem.commands.ClanAdmin;
import de.joel.clansystem.listener.ClanActionClickListener;
import de.joel.clansystem.listener.ClanInventoryClickListener;
import de.joel.clansystem.listener.ClanTagUpdateListener;
import de.joel.clansystem.listener.JoinListener;
import de.joel.clansystem.manager.ClanManager;
import de.joel.clansystem.manager.AdminManager;
import de.joel.clansystem.placeholders.ClanPlaceholderExpansion;
import de.joel.clansystem.tabcompleter.ClanTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClanSystem extends JavaPlugin {

    private static ClanSystem instance;
    private MySQL mySQL;
    private ClanManager clanManager;
    private AdminManager adminManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        mySQL = new MySQL();

        // ** MySQL-Verbindung aufbauen & prüfen **
        if (!mySQL.connect()) {
            getLogger().severe("❌ Konnte keine Verbindung zur MySQL-Datenbank herstellen!");
            getLogger().severe("⚠ Das Plugin wird deaktiviert.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        mySQL.checkAndCreateTables();

        clanManager = new ClanManager(mySQL);
        adminManager = new AdminManager(mySQL, clanManager);
        clanManager.setAdminManager(adminManager);

        clanManager.loadAllClanTags();

        // ** Befehle registrieren **
        getCommand("clan").setExecutor(new Clan(clanManager));
        getCommand("clan").setTabCompleter(new ClanTabCompleter());

        getCommand("clanadmin").setExecutor(new ClanAdmin(adminManager, clanManager));

        // ** Event Listener registrieren **
        getServer().getPluginManager().registerEvents(new JoinListener(clanManager), this);
        getServer().getPluginManager().registerEvents(new ClanTagUpdateListener(new ClanPlaceholderExpansion(clanManager)), this);

        getServer().getPluginManager().registerEvents(new ClanInventoryClickListener(adminManager, clanManager), this);
        getServer().getPluginManager().registerEvents(new ClanActionClickListener(adminManager, clanManager), this);

        // ** PlaceholderAPI Support **
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ClanPlaceholderExpansion(clanManager).register();
            getLogger().info("✅ PlaceholderAPI erfolgreich registriert!");
        } else {
            getLogger().warning("⚠ PlaceholderAPI nicht gefunden! Clan-Tag Placeholder funktioniert nicht.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("🔌 ClanSystem wird gestoppt...");
        if (mySQL != null) {
            mySQL.disconnect();
        }
    }

    public static ClanSystem getInstance() {
        return instance;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }
}