package com.sport.controller.admin;

import java.io.IOException;

import com.sport.model.Membre;
import com.sport.service.MembreService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MemberAdminController {

    @FXML
    private TableView<Membre> memberTable;
    @FXML
    private TableColumn<Membre, Integer> colId;
    @FXML
    private TableColumn<Membre, String> colNom;
    @FXML
    private TableColumn<Membre, String> colPrenom;
    @FXML
    private TableColumn<Membre, String> colEmail;
    @FXML
    private TableColumn<Membre, String> colTelephone;
    @FXML
    private TableColumn<Membre, String> colObjectif;
    @FXML
    private TableColumn<Membre, String> colPreference;
    @FXML
    private TableColumn<Membre, Void> colModify;
    @FXML
    private TableColumn<Membre, Void> colDelete;

    @FXML
    private Button btnAddMember;

    private ObservableList<Membre> memberList;
    private MembreService membreService = new MembreService(); // your CRUD service

    @FXML
    public void initialize() {
        loadMembers();
        setupTableColumns();
        setupActionColumns();

        btnAddMember.setOnAction(e -> openAddMemberForm());
    }

    private void loadMembers() {
        try {
        memberList = FXCollections.observableArrayList(membreService.recupererTousLesMembres());
            memberTable.setItems(memberList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        // ======== Setup Table Columns ========
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
    colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
    colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    colObjectif.setCellValueFactory(new PropertyValueFactory<>("objectifSportif"));
    colPreference.setCellValueFactory(new PropertyValueFactory<>("preferences"));



    }

    private void setupActionColumns() {
        addDeleteButton();
        addModifyButton();
    }

    private void addDeleteButton() {
        colDelete.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("X");

            {
                deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    Membre member = getTableView().getItems().get(getIndex());
                    membreService.supprimerMembre(member.getId());
                    memberList.remove(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void addModifyButton() {
        colModify.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✎");

            {
                editBtn.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                editBtn.setOnAction(e -> {
                    Membre member = getTableView().getItems().get(getIndex());
                    openEditMemberForm(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });
    }

    private void openAddMemberForm() {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/member-form.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Ajouter un membre");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL); // blocks the main window
                stage.showAndWait(); // wait until form is closed
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


   private void openEditMemberForm(Membre member) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/member-form.fxml"));
        Parent root = loader.load();

        // Pass the selected member to the form controller
        MemberFormController controller = loader.getController();
        controller.setMember(member);  // you'll need a setter in MemberFormController

        Stage stage = new Stage();
        stage.setTitle("Edit Member");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); // blocks main window
        stage.showAndWait();

        // refresh the table after editing
        memberTable.refresh();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
