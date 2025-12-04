package com.sport.model;

import java.util.ArrayList;
import java.util.List;

public class Membre extends Utilisateur {

    private TypeObjectif  objectifSportif;
    private PreferenceActivite  preferences;

    private List<HistoriqueActivite> historiqueActivite;
    private List<Seance> listeSeances;

    public Membre() {
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
    }

    public Membre(int id, String nom, String prenom, String dateNaissance,
                  String email, String telephone, String adresse,
                  TypeObjectif  objectifSportif, PreferenceActivite  preferences) {

        super(id, nom, prenom, dateNaissance, email, telephone, adresse);
        this.objectifSportif = objectifSportif;
        this.preferences = preferences;
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
    }

    // ---------- Getters & Setters ----------

    public TypeObjectif  getObjectifSportif() {
        return objectifSportif;
    }

    public void setObjectifSportif(TypeObjectif  objectifSportif) {
        this.objectifSportif = objectifSportif;
    }

    public PreferenceActivite  getPreferences() {
        return preferences;
    }

    public void setPreferences(PreferenceActivite  preferences) {
        this.preferences = preferences;
    }

    public List<HistoriqueActivite> getHistoriqueActivite() {
        return historiqueActivite;
    }

    public void setHistoriqueActivite(List<HistoriqueActivite> historiqueActivite) {
        this.historiqueActivite = historiqueActivite;
    }

    public List<Seance> getListeSeances() {
        return listeSeances;
    }

    public void setListeSeances(List<Seance> listeSeances) {
        this.listeSeances = listeSeances;
    }
}
