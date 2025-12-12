// package com.sport.repository;

// import java.sql.*;
// import java.util.List;
// import java.util.ArrayList;


// import com.sport.model.Coach;
// import com.sport.model.Membre;
// import com.sport.model.Salle;
// import com.sport.model.Equipement;
// import com.sport.utils.DBConnection;


// public class AdministrateurRepository {

// public String genererRapport(TypeRapport type) {
//     switch (type) {
//         case OCCUPATION_COURS:
//             return genererRapportOccupationCours();
//         case FREQUENTATION_SALLE:
//             return genererRapportFrequentationSalle();
//         case SATISFACTION_MEMBRES:
//             return genererRapportSatisfactionMembres();
//         case REVENUS_ABONNEMENTS:
//             return genererRapportRevenusAbonnements();
//         default:
//             return "Type de rapport non reconnu.";
//     }
// }
// }