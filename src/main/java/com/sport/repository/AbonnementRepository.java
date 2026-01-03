package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.Membre;
import com.sport.model.StatutAbonnement;
import com.sport.model.TypeAbonnement;
import com.sport.utils.DBConnection;

public class AbonnementRepository {

    /* ===================== CREATE ===================== */
    public void ajouterAbonnement(Abonnement abonnement) {

        // 1️⃣ Calculer le montant automatiquement
        double montant = calculerMontant(abonnement.getTypeAbonnement());
        abonnement.setMontant(montant);

        // 2️⃣ Convertir java.util.Date -> LocalDate
       java.util.Date utilDateDebut = abonnement.getDateDebut();

        LocalDate dateDebut = new java.sql.Date(utilDateDebut.getTime())
                .toLocalDate();


        // 3️⃣ Calculer dateFin 
        LocalDate dateFin;
        switch (abonnement.getTypeAbonnement()) {
            case MENSUEL -> dateFin = dateDebut.plusMonths(1);
            case TRIMESTRIEL -> dateFin = dateDebut.plusMonths(3);
            case ANNUEL -> dateFin = dateDebut.plusYears(1);
            default -> dateFin = dateDebut;
        }

        // 4️⃣ Convertir LocalDate -> java.sql.Date
        java.sql.Date sqlDateDebut = java.sql.Date.valueOf(dateDebut);
        java.sql.Date sqlDateFin = java.sql.Date.valueOf(dateFin);

        String sql = """
            INSERT INTO abonnement
            (member_fullname, type, statut, date_debut, date_fin, autorenouvellement, montant)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, abonnement.getMembre().getNomComplet());
            stmt.setString(2, abonnement.getTypeAbonnement().name());
            stmt.setString(3, abonnement.getStatutAbonnement().name());
            stmt.setDate(4, sqlDateDebut);
            stmt.setDate(5, sqlDateFin);
            stmt.setBoolean(6, abonnement.isAutorenouvellement());
            stmt.setDouble(7, montant);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                abonnement.setId(rs.getInt(1));
            }

            System.out.println("✅ Abonnement "
                    + abonnement.getTypeAbonnement()
                    + " créé pour "
                    + abonnement.getMembre().getNomComplet());
            System.out.println("📅 Date de fin : " + dateFin);

        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout abonnement : " + e.getMessage());
        }
    }

    /* ===================== READ ALL ===================== */
    public List<Abonnement> listerTout() {
        List<Abonnement> list = new ArrayList<>();
        String sql = "SELECT * FROM abonnement";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToAbonnement(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur liste abonnements : " + e.getMessage());
        }

        return list;
    }

    /* ===================== READ BY ID ===================== */
    public Abonnement trouverParId(int id) {
        String sql = "SELECT * FROM abonnement WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAbonnement(rs);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche abonnement : " + e.getMessage());
        }

        return null;
    }

    //trouver par nom de memeber
    public List<Abonnement> trouverParNomMembre(String nomComplet) {
        List<Abonnement> list = new ArrayList<>();
        // Adapte 'member_fullname' si ta colonne s'appelle différemment
        String sql = "SELECT * FROM abonnement WHERE member_fullname = ? ORDER BY date_fin DESC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomComplet);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToAbonnement(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ===================== READ BY STATUS ===================== */
    public List<Abonnement> trouverParStatut(StatutAbonnement statut) {
        List<Abonnement> list = new ArrayList<>();
        String sql = "SELECT * FROM abonnement WHERE statut = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToAbonnement(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche par statut : " + e.getMessage());
        }

        return list;
    }

    /* ===================== UPDATE ===================== */
    public void modifierAbonnement(Abonnement ab) {
        String sql = """
            UPDATE abonnement
            SET type = ?, statut = ?, autorenouvellement = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ab.getTypeAbonnement().name());
            stmt.setString(2, ab.getStatutAbonnement().name());
            stmt.setBoolean(3, ab.isAutorenouvellement());
            stmt.setInt(4, ab.getId());

            stmt.executeUpdate();
            System.out.println("✅ Abonnement modifié");

        } catch (SQLException e) {
            System.out.println("❌ Erreur modification abonnement : " + e.getMessage());
        }
    }

    /* ===================== DELETE ===================== */
    public void supprimerAbonnement(int id) {
        String sql = "DELETE FROM abonnement WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("🗑️ Abonnement supprimé");

        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression abonnement : " + e.getMessage());
        }
    }

    /* ===================== MAPPING ===================== */
    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {

        Abonnement ab = new Abonnement();
        ab.setId(rs.getInt("id"));

        Membre membre = new Membre();
        membre.setNomComplet(rs.getString("member_fullname"));
        ab.setMembre(membre);

        ab.setTypeAbonnement(TypeAbonnement.valueOf(rs.getString("type")));
        ab.setStatutAbonnement(StatutAbonnement.valueOf(rs.getString("statut")));
        ab.setAutorenouvellement(rs.getBoolean("autorenouvellement"));
        ab.setMontant(rs.getDouble("montant"));

        ab.setDateDebut(rs.getDate("date_debut"));
        ab.setDateFin(rs.getDate("date_fin"));

        return ab;
    }

    /* ===================== BUSINESS ===================== */
    public double calculerMontant(TypeAbonnement type) {
        return switch (type) {
            case MENSUEL -> 300.00;
            case TRIMESTRIEL -> 800.00;
            case ANNUEL -> 2500.00;
        };
    }
}
