package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Rapport;
import com.sport.model.TypeRapport;
import com.sport.utils.DBConnection;

public class RapportRepository {

// CREATE
    public void ajouterRapport(Rapport rapport) {
        String sql = "INSERT INTO RAPPORT (type, dateDebut, dateFin, donnees, dateCreation) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rapport.getType());
            stmt.setString(2, rapport.getDateDebut());
            stmt.setString(3, rapport.getDateFin());
            stmt.setString(4, rapport.getDonnees());
            stmt.setString(5, rapport.getDateDebut());
            
            stmt.executeUpdate();
             } catch (SQLException e) {
            System.out.println("Erreur ajout rapport : " + e.getMessage());
        }
    }
// READ (Tout)
    public List<Rapport> listerRapports() {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM RAPPORT ORDER BY dateCreation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rapports.add(mapResultSetToRapport(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur récupération des rapports : " + e.getMessage());
        }
        
        return rapports;
    }
    
    // READ (Par ID)
    public Rapport getRapportParId(int id) {
        String sql = "SELECT * FROM RAPPORT WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRapport(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur recherche rapport : " + e.getMessage());
        }
        
        return null;
    }

   

    // DELETE
    public void supprimerRapport(int rapportId) {
        String sql = "DELETE FROM RAPPORT WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rapportId);
            stmt.executeUpdate();
            System.out.println("Rapport supprimé !");
            
        } catch (SQLException e) {
            System.out.println("Erreur suppression rapport : " + e.getMessage());
        }
    }

   // Mapping adapté à vos types
    private Rapport mapResultSetToRapport(ResultSet rs) throws SQLException {
        // C'est ici que le constructeur vide Rapport() est appelé !
        Rapport rapport = new Rapport();
        
        rapport.setId(rs.getInt("id"));
        
        String type = rs.getString("type");
        if (type != null) {
            // Conversion String -> Enum TypeRapport (si nécessaire)
            try {
                // Vérifier si le type existe dans l'enum
                TypeRapport.valueOf(type);
                rapport.setType(type);
            } catch (IllegalArgumentException e) {
                System.out.println("Type de rapport inconnu en base : " + type);
                // Ou assigner une valeur par défaut
                rapport.setType("STATISTIQUES_GLOBALES");
            }
        }
        
        rapport.setDateDebut(rs.getString("dateDebut"));
        rapport.setDateFin(rs.getString("dateFin"));
        rapport.setDonnees(rs.getString("donnees"));
        rapport.setDateDebut(rs.getString("dateCreation"));
        
        return rapport;
    }  
}