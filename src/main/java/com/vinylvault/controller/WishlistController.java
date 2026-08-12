package com.vinylvault.controller;

import com.vinylvault.model.Album;
import com.vinylvault.service.StorageService;
import com.vinylvault.util.AlertUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WishlistController {

    @FXML private TableView<Album> tableView;
    @FXML private TableColumn<Album, String>  colArtist;
    @FXML private TableColumn<Album, String>  colTitle;
    @FXML private TableColumn<Album, String>  colFormat;
    @FXML private TableColumn<Album, Integer> colYear;
    @FXML private TextField fieldTitle;
    @FXML private TextField fieldArtist;
    @FXML private ComboBox<String> fieldFormat;
    @FXML private TextField fieldYear;
    @FXML private TextField fieldGenre;
    @FXML private TextArea  fieldNotes;

    private StorageService storage;

    public void init(StorageService storage) {
        this.storage = storage;

        colArtist.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArtist()));
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colFormat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormat()));
        colYear.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getReleaseYear()).asObject());

        if (fieldFormat != null)
            fieldFormat.setItems(FXCollections.observableArrayList("Vinyl LP", "Vinyl 7\"", "CD", "Cassette"));

        refresh();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, a) -> {
            if (a == null) return;
            if (fieldTitle  != null) fieldTitle.setText(a.getTitle());
            if (fieldArtist != null) fieldArtist.setText(a.getArtist());
            if (fieldFormat != null) fieldFormat.setValue(a.getFormat());
            if (fieldYear   != null) fieldYear.setText(String.valueOf(a.getReleaseYear()));
            if (fieldGenre  != null) fieldGenre.setText(a.getGenre());
            if (fieldNotes  != null) fieldNotes.setText(a.getNotes());
        });
    }

    public void refresh() {
        ObservableList<Album> items = FXCollections.observableArrayList(storage.getDb().getWishlist());
        tableView.setItems(items);
    }

    @FXML private void onAddNew() {
        Album a = new Album();
        a.setWishlist(true);
        storage.addAlbum(a);
        refresh();
        tableView.getSelectionModel().selectLast();
    }

    @FXML private void onSave() {
        Album a = tableView.getSelectionModel().getSelectedItem();
        if (a == null) { AlertUtil.error("No Selection", "Select an item to save."); return; }

        if (fieldTitle  != null) a.setTitle(fieldTitle.getText().trim());
        if (fieldArtist != null) a.setArtist(fieldArtist.getText().trim());
        if (fieldFormat != null && fieldFormat.getValue() != null) a.setFormat(fieldFormat.getValue());
        if (fieldYear   != null) { try { a.setReleaseYear(Integer.parseInt(fieldYear.getText().trim())); } catch (NumberFormatException ignored) {} }
        if (fieldGenre  != null) a.setGenre(fieldGenre.getText().trim());
        if (fieldNotes  != null) a.setNotes(fieldNotes.getText().trim());

        storage.updateAlbum(a);
        refresh();
        AlertUtil.info("Saved", "Wishlist item saved.");
    }

    @FXML private void onDelete() {
        Album a = tableView.getSelectionModel().getSelectedItem();
        if (a == null) return;
        if (AlertUtil.confirm("Delete", "Remove \"" + a.getTitle() + "\" from wishlist?")) {
            storage.deleteAlbum(a.getId());
            refresh();
        }
    }

    @FXML private void onMoveToCollection() {
        Album a = tableView.getSelectionModel().getSelectedItem();
        if (a == null) { AlertUtil.error("No Selection", "Select an item to move."); return; }
        storage.moveToCollection(a.getId());
        refresh();
        AlertUtil.info("Moved", "\"" + a.getTitle() + "\" moved to your collection!");
    }
}
