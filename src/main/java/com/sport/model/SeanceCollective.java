package com.sport.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceCollective extends Seance {

    private int placesDisponibles;
    private List<Membre> listeMembers;

    // Constructeur
    public SeanceCollective(int id, String nom, int capaciteMax, Salle salle, LocalDateTime dateHeure,
                            Coach entraineur, TypeCours type, int duree, int placesDisponibles) {
        super(id, nom, capaciteMax, salle, dateHeure, entraineur, type, duree);
        this.placesDisponibles = placesDisponibles;
        this.listeMembers = new ArrayList<>();
    }

    // Getters et setters
    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public List<Membre> getListeMembers() {
        return listeMembers;
    }

    public void setListeMembers(List<Membre> listeMembers) {
        this.listeMembers = listeMembers;
    }
    @Override
    public void notifierParticipants() {
    }
}
