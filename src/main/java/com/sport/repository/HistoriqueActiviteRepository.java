package com.sport.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.HistoriqueActivite;
import com.sport.model.Membre;
import com.sport.model.TypeSeance;
import com.sport.utils.DBConnection;



public class HistoriqueActiviteRepository {

// CREATE
    public void ajouterHistoriqueActivite(HistoriqueActivite historique) {
        String sql = "INSERT INTO historiqueactivite (membre_id, typeSeance, duree, date, notes) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, historique.getMembre().getId());
            stmt.setString(2, historique.getTypeSeance() != null ? historique.getTypeSeance().name() : null);
            stmt.setInt(3, historique.getDuree());
            stmt.setDate(4, Date.valueOf(historique.getDate()));
            stmt.setString(5, historique.getNotes());
            
            stmt.executeUpdate();
            
            
        } catch (SQLException e) {
            System.out.println("Erreur ajout historique activité : " + e.getMessage());
        }
    }

   // READ (Tout)
    public List<HistoriqueActivite> listerHistoriquesActivite() {
        List<HistoriqueActivite> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historiqueactivite ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                historiques.add(mapResultSetToHistoriqueActivite(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur récupération des historiques : " + e.getMessage());
        }
        
        return historiques;
    }
    
    // READ (Par ID)
    public HistoriqueActivite trouverParId(int id) {
        String sql = "SELECT * FROM historiqueactivite WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHistoriqueActivite(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur recherche historique activité : " + e.getMessage());
        }
        
        return null;
    }
    
    // READ (Par Membre)
    public List<HistoriqueActivite> trouverParMembre(int membreId) {
        List<HistoriqueActivite> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historiqueactivite WHERE membre_id = ? ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, membreId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiques.add(mapResultSetToHistoriqueActivite(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur recherche historiques par membre : " + e.getMessage());
        }
        
        return historiques;
    }

    // UPDATE
    public void modifierHistoriqueActivite(HistoriqueActivite historique) {
        String sql = "UPDATE historiqueactivite SET membre_id = ?, typeSeance = ?, duree = ?, date = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, historique.getMembre().getId());
            stmt.setString(2, historique.getTypeSeance() != null ? historique.getTypeSeance().name() : null);
            stmt.setInt(3, historique.getDuree());
            stmt.setDate(4, Date.valueOf(historique.getDate()));
            stmt.setString(5, historique.getNotes());
            stmt.setInt(6, historique.getId());
            
            int lignesModifiees = stmt.executeUpdate();
            
            if (lignesModifiees > 0) {
                System.out.println("Historique d'activité modifié avec succès !");
            } else {
                System.out.println("Aucun historique trouvé avec l'ID : " + historique.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur modification historique activité : " + e.getMessage());
        }
    }
    
    // DELETE
    public void supprimerHistoriqueActivite(int historiqueId) {
        String sql = "DELETE FROM historiqueactivite WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, historiqueId);
            stmt.executeUpdate();
            System.out.println("Historique d'activité supprimé !");
            
        } catch (SQLException e) {
            System.out.println("Erreur suppression historique activité : " + e.getMessage());
        }
    }
    
    // DELETE (Tous les historiques d'un membre)
    public void supprimerHistoriquesParMembre(int membreId) {
        String sql = "DELETE FROM historiqueactivite WHERE membre_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, membreId);
            int lignesSuppr = stmt.executeUpdate();
            System.out.println(lignesSuppr + " historique(s) supprimé(s) pour le membre ID=" + membreId);
            
        } catch (SQLException e) {
            System.out.println("Erreur suppression historiques par membre : " + e.getMessage());
        }
    }
    
    // Mapping adapté à vos types
    private HistoriqueActivite mapResultSetToHistoriqueActivite(ResultSet rs) throws SQLException {
        // Récupérer le membre (simplifié - vous devriez utiliser MembreRepository)
        Membre membre = new Membre();
        membre.setId(rs.getInt("membre_id"));
        // TODO: Charger les détails complets du membre si nécessaire
        
        // Récupérer et convertir le type de séance
        String typeSeanceStr = rs.getString("typeSeance");
        TypeSeance typeSeance = null;
        if (typeSeanceStr != null) {
            try {
                typeSeance = TypeSeance.valueOf(typeSeanceStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Type de séance inconnu en base : " + typeSeanceStr);
            }
        }
        
        // Convertir java.sql.Date en LocalDate
        Date sqlDate = rs.getDate("date");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;
        
        // Créer l'objet HistoriqueActivite avec le constructeur BDD
        return new HistoriqueActivite(
            rs.getInt("id"),
            membre,
            typeSeance,
            rs.getInt("duree"),
            localDate,
            rs.getString("notes")
        );
    }
}

