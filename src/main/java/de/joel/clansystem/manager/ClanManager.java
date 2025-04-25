package de.joel.clansystem.manager;

import de.joel.clansystem.api.MySQL;
import de.joel.clansystem.ClanSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import de.joel.clansystem.events.ClanTagUpdateEvent;
import org.bukkit.Bukkit;

public class ClanManager {

    private final Map<UUID, String> clanCache = new HashMap<>();
    private final Map<UUID, String> rankCache = new HashMap<>();
    public final Map<UUID, String> clanTagCache = new HashMap<>();
    private final Map<UUID, String> deleteClanCache = new HashMap<>(); // Für temporäres Speichern des Clans zum Löschen

    private final MySQL database;
    private AdminManager adminManager;

    public ClanManager(MySQL database) {
        this.database = database;
    }

    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    /** Clan erstellen **/
    public void createClan(String clanName, String clanTag, UUID ownerUUID) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO clans (name, tag) VALUES (?, ?)")) {
            stmt.setString(1, clanName);
            stmt.setString(2, clanTag);
            stmt.executeUpdate();

            addPlayerToClan(ownerUUID, clanName, "LEADER");

            clanCache.put(ownerUUID, clanName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /** Clan löschen (über Bestätigung) **/
    public void deleteClan(UUID playerUUID) {
        String clanName = deleteClanCache.get(playerUUID);
        if (clanName == null) return;

        Connection conn = null;
        try {
            conn = database.getConnection();
            conn.setAutoCommit(false);

            int clanId = -1;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM clans WHERE name = ?")) {
                stmt.setString(1, clanName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    clanId = rs.getInt("id");
                }
            }
            if (clanId == -1) return;

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM clan_bans WHERE clan_id = ?")) {
                stmt.setInt(1, clanId);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM clan_members WHERE clan_id = ?")) {
                stmt.setInt(1, clanId);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM clans WHERE id = ?")) {
                stmt.setInt(1, clanId);
                stmt.executeUpdate();
            }

            conn.commit();

            deleteClanCache.remove(playerUUID);
            clanCache.values().removeIf(name -> name.equals(clanName));
            clanTagCache.values().removeIf(tag -> tag.equals(getClanTagForClan(clanName)));

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        // Clan-Tag-Update für alle betroffenen Spieler auslösen
        for (UUID memberUUID : clanCache.keySet()) {
            if (clanCache.get(memberUUID).equals(clanName)) {
                clanTagCache.remove(memberUUID); // Clan-Tag sofort entfernen
                Bukkit.getPluginManager().callEvent(new ClanTagUpdateEvent(memberUUID)); // Event feuern
            }
        }
    }


    // Methode, um den Clan für die Löschung vorzubereiten
    public void prepareToDeleteClan(UUID playerUUID, String clanName) {
        // Setze den Clan-Namen im Cache, damit er später gelöscht werden kann
        if (clanName == null) {
            deleteClanCache.remove(playerUUID); // Lösche den Cache, wenn der Clan nicht mehr gelöscht werden soll
        } else {
            deleteClanCache.put(playerUUID, clanName); // Setze den Clan-Namen für den Löschvorgang
        }
    }

    // Methode, um zu prüfen, ob ein Spieler einen Clan zum Löschen vorbereitet hat
    public boolean hasClanToDelete(UUID playerUUID) {
        return deleteClanCache.containsKey(playerUUID);
    }


    /** Spieler zu Clan hinzufügen **/
    public void addPlayerToClan(UUID playerUUID, String clanName, String rank) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO clan_members (uuid, clan_id, rank) VALUES (?, (SELECT id FROM clans WHERE name = ?), ?)")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, clanName);
            stmt.setString(3, rank);
            stmt.executeUpdate();

            clanCache.put(playerUUID, clanName);
            rankCache.put(playerUUID, rank);

            // Clan-Tag für den Spieler im Cache speichern
            String clanTag = getClanTagForClan(clanName);
            clanTagCache.put(playerUUID, clanTag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Spieler aus Clan entfernen **/
    public void removePlayerFromClan(UUID playerUUID) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM clan_members WHERE uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            stmt.executeUpdate();

            clanCache.remove(playerUUID);
            rankCache.remove(playerUUID);
            clanTagCache.remove(playerUUID);  // Clan-Tag aus dem Cache entfernen

            // Sicherstellen, dass der Cache wirklich up-to-date ist
            loadClanTag(playerUUID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /** Spieler befördern **/
    public void promotePlayer(UUID playerUUID, String newRank) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE clan_members SET rank = ? WHERE uuid = ?")) {
            stmt.setString(1, newRank);
            stmt.setString(2, playerUUID.toString());
            stmt.executeUpdate();

            rankCache.put(playerUUID, newRank);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Spieler bannen **/
    public void banPlayer(UUID playerUUID, String clanName) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO clan_bans (uuid, clan_id) VALUES (?, (SELECT id FROM clans WHERE name = ?))")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, clanName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Prüfen, ob Spieler gebannt ist **/
    public boolean isPlayerBanned(UUID playerUUID, String clanName) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM clan_bans WHERE uuid = ? AND clan_id = (SELECT id FROM clans WHERE name = ?)")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, clanName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Clan-Name anhand der UUID abrufen **/
    public String getClanName(UUID playerUUID) {
        if (clanCache.containsKey(playerUUID)) {
            return clanCache.get(playerUUID);
        }

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT c.name FROM clans c JOIN clan_members m ON c.id = m.clan_id WHERE m.uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String clanName = rs.getString("name");
                clanCache.put(playerUUID, clanName);
                return clanName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Clan-Tag für einen Clan abrufen **/
    public String getClanTagForClan(String clanName) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT tag FROM clans WHERE name = ?")) {
            stmt.setString(1, clanName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tag");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ""; // Wenn kein Clan-Tag gefunden wird
    }

    public String getRank(UUID playerUUID) {
        if (rankCache.containsKey(playerUUID)) {
            return rankCache.get(playerUUID);
        }

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT rank FROM clan_members WHERE uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String rank = rs.getString("rank");
                rankCache.put(playerUUID, rank);
                return rank;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Überprüfen, ob ein Spieler in einem Clan ist **/
    public boolean isInClan(UUID playerUUID) {
        if (clanCache.containsKey(playerUUID)) {
            return true;
        }

        String clanName = getClanName(playerUUID);
        if (clanName != null) {
            clanCache.put(playerUUID, clanName);
            return true;
        }
        return false;
    }



    /** Laden der Clan-Tags für alle Spieler beim Serverstart **/
    public void loadAllClanTags() {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT m.uuid, c.tag FROM clan_members m JOIN clans c ON m.clan_id = c.id")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUUID = UUID.fromString(rs.getString("uuid"));
                String tag = rs.getString("tag");
                clanTagCache.put(playerUUID, tag);  // Clan-Tag für den Spieler in den Cache setzen
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Clan-Tag für einen Spieler laden **/
    public String loadClanTag(UUID playerUUID) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.tag FROM clan_members m JOIN clans c ON m.clan_id = c.id WHERE m.uuid = ?"
             )) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String tag = rs.getString("tag");
                clanTagCache.put(playerUUID, tag);
                return tag;
            } else {
                clanTagCache.remove(playerUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ""; // Falls kein Clan-Tag gefunden wird
    }


    public boolean clanExists(String clanName) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM clans WHERE name = ?")) {
            stmt.setString(1, clanName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tagExists(String clanTag) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM clans WHERE tag = ?")) {
            stmt.setString(1, clanTag);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getClanTag(UUID playerUUID) {
        if (clanTagCache.containsKey(playerUUID)) {
            return clanTagCache.get(playerUUID);
        }
        return loadClanTag(playerUUID);
    }


    /** Prüft, ob ein Spieler befördert werden kann **/
    public boolean canPromote(UUID senderUUID, UUID targetUUID) {
        String senderRole = getRank(senderUUID);
        String targetRole = getRank(targetUUID);

        if (senderRole == null || targetRole == null) return false;

        // Nur Leader und Co-Leader dürfen befördern
        if (!(senderRole.equals("LEADER") || senderRole.equals("CO_LEADER"))) {
            return false;
        }

        // Member → Mod
        if (targetRole.equals("MEMBER")) return true;
        // Mod → Co-Leader
        if (targetRole.equals("MOD") && senderRole.equals("LEADER")) return true;

        return false; // Kann nicht weiter befördert werden
    }

    /** Gibt den neuen Rang nach der Beförderung zurück **/
    public String getNextRank(String currentRank) {
        switch (currentRank) {
            case "MEMBER": return "MOD";
            case "MOD": return "CO_LEADER";
            default: return null; // Co-Leader kann nicht weiter befördert werden
        }
    }

    /** Prüft, ob der Spieler der Leader des Clans ist **/
    public boolean isLeader(UUID playerUUID) {
        String rank = getRank(playerUUID);
        return rank != null && rank.equals("LEADER");
    }

    public int getClanMemberCount(String clanName) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS member_count FROM clan_members " +
                             "WHERE clan_id = (SELECT id FROM clans WHERE name = ?)")) {
            stmt.setString(1, clanName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("member_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, String> getClanMembersWithRanks(String clanName) {
        Map<String, String> members = new LinkedHashMap<>();
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT m.uuid, m.rank FROM clan_members m " +
                             "WHERE m.clan_id = (SELECT id FROM clans WHERE name = ?) " +
                             "ORDER BY FIELD(m.rank, 'LEADER', 'CO_LEADER', 'MOD', 'MEMBER')"
             )) {
            stmt.setString(1, clanName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String playerUUID = rs.getString("uuid");
                String playerName = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName();
                members.put(playerName, rs.getString("rank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public void setPlayerRank(UUID playerUUID, String rank) {
        try (Connection conn = this.database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE clan_members SET rank = ? WHERE uuid = ?")) {
            stmt.setString(1, rank);
            stmt.setString(2, playerUUID.toString());
            stmt.executeUpdate();

            rankCache.put(playerUUID, rank); // Cache aktualisieren
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isClanBanned(String clanName) {
        return adminManager.isClanBanned(clanName);
    }

    /**
     * Aktualisiert die Caches nach der Löschung eines Clans
     */
    public void updateCachesAfterClanDeletion(String clanName) {
        // Clan-Cache aktualisieren
        clanCache.values().removeIf(name -> name.equals(clanName));

        // Clan-Tag-Cache aktualisieren
        clanTagCache.values().removeIf(tag -> tag.equals(getClanTagForClan(clanName)));

        // Clan-Tag-Update für alle betroffenen Spieler auslösen
        for (UUID memberUUID : clanCache.keySet()) {
            if (clanCache.get(memberUUID).equals(clanName)) {
                clanTagCache.remove(memberUUID); // Clan-Tag sofort entfernen
                Bukkit.getPluginManager().callEvent(new ClanTagUpdateEvent(memberUUID)); // Event feuern
            }
        }
    }


    /** Weitere Methoden... **/
}