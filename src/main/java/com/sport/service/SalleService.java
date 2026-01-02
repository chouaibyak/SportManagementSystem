package com.sport.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.sport.model.Salle;
import com.sport.repository.SalleRepository;
import com.sport.utils.DBConnection;

public class SalleService {

    private final SalleRepository salleRepository;

    // ✅ Default constructor
    public SalleService() {
        this.salleRepository = new SalleRepository();
    }

    // ➤ Ajouter une salle
    public void ajouterSalle(Salle salle) {
        salleRepository.ajouterSalle(salle);
    }

    // ➤ Modifier une salle
    public boolean modifierSalle(Salle salle) {
        return salleRepository.modifierSalle(salle);
    }

    // ➤ Supprimer une salle
    public boolean supprimerSalle(int id) {
        return salleRepository.supprimerSalle(id);
    }

    // ➤ Lister toutes les salles
    public List<Salle> getToutesLesSalles() {
        return salleRepository.listerSalles();
    }

    // ➤ Obtenir une salle par ID
    public Salle getSalleById(int id) {
        return salleRepository.getSalleById(id);
    }

    // ➤ Vérifier disponibilité
    public boolean salleDisponible(int idSalle, Date date) {
        Salle salle = salleRepository.getSalleById(idSalle);

        if (salle == null) {
            System.out.println("Salle introuvable ID=" + idSalle);
            return false;
        }
        return salle.verifierDisponibilite(date);
    }

    
    // ➤ Compter le nombre de personnes dans une salle à un moment donné
    public int countPeopleInSalle(int salleId) {

        String sql = """
            SELECT COUNT(r.membre_id)
            FROM seance s
            JOIN reservation r ON r.seance_id = s.id
            WHERE s.salle_id = ?
            AND NOW() BETWEEN s.dateHeure
                        AND DATE_ADD(s.dateHeure, INTERVAL s.duree MINUTE)
        """;

        try (Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, salleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
