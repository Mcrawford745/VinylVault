package com.vinylvault.controller;

import com.vinylvault.model.Album;
import com.vinylvault.service.StorageService;
import com.vinylvault.util.AlertUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CollectionController {

    @FXML private TextField searchField;
    @FXML private TableView<Album> tableView;
    @FXML private TableColumn<Album, String> colArtist;
    @FXML private TableColumn<Album, String> colTitle;
    @FXML private TableColumn<Album, String> colFormat;
    @FXML private TableColumn<Album, String> colGenre;
    @FXML private TableColumn<Album, Integer> colYear;
    @FXML private TableColumn<Album, Integer> colPlays;
    @FXML private TableColumn<Album, String> colCondition;

    // Detail fields
    @FXML private TextField fieldTitle;
    @FXML private TextField fieldArtist;
    @FXML private ComboBox<String> fieldFormat;
    @FXML private TextField fieldGenre;
    @FXML private ComboBox<String> fieldMood;
    @FXML private TextField fieldYear;
    @FXML private TextField fieldLabel;
    @FXML private TextField fieldCatalog;
    @FXML private TextField fieldBarcode;
    @FXML private ComboBox<String> fieldMediaCond;
    @FXML private ComboBox<String> fieldSleeveCond;
    @FXML private TextField fieldPrice;
    @FXML private TextArea  fieldNotes;
    @FXML private TextArea  fieldTracklist;
    @FXML private TextField fieldListenNotes;

    private StorageService storage;
    private ObservableList<Album> items;
    private FilteredList<Album> filtered;

    public void init(StorageService storage) {
        this.storage = storage;

        // Table columns
        colArtist.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArtist()));
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colFormat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormat()));
        colGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenre()));
        colYear.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getReleaseYear()).asObject());
        colPlays.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPlayCount()).asObject());
        colCondition.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMediaCondition()));

        // Combo options
        if (fieldFormat != null)    fieldFormat.setItems(FXCollections.observableArrayList("Vinyl LP", "Vinyl 7\"", "CD", "Cassette"));
        if (fieldMood != null)      fieldMood.setItems(FXCollections.observableArrayList("Chill", "Energetic", "Focus", "Dark", "Happy", "Sad", "Party"));
        List<String> conds = List.of("M", "NM", "VG+", "VG", "G+", "F", "P");
        if (fieldMediaCond != null)  fieldMediaCond.setItems(FXCollections.observableArrayList(conds));
        if (fieldSleeveCond != null) fieldSleeveCond.setItems(FXCollections.observableArrayList(conds));

        refresh();

        // Selection listener → populate detail form
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, album) -> {
            if (album != null) populateForm(album);
        });

        // Search filter
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, val) -> {
                filtered.setPredicate(a -> val == null || val.isBlank()
                        || a.getTitle().toLowerCase().contains(val.toLowerCase())
                        || a.getArtist().toLowerCase().contains(val.toLowerCase())
                        || a.getFormat().toLowerCase().contains(val.toLowerCase()));
            });
        }
    }

    public void refresh() {
        items = FXCollections.observableArrayList(storage.getDb().getCollection());
        filtered = new FilteredList<>(items, a -> true);
        tableView.setItems(filtered);
    }

    private void populateForm(Album a) {
        setText(fieldTitle,   a.getTitle());
        setText(fieldArtist,  a.getArtist());
        setCombo(fieldFormat, a.getFormat());
        setText(fieldGenre,   a.getGenre());
        setCombo(fieldMood,   a.getMood());
        setText(fieldYear,    String.valueOf(a.getReleaseYear()));
        setText(fieldLabel,   a.getLabel());
        setText(fieldCatalog, a.getCatalogNumber());
        setText(fieldBarcode, a.getBarcode());
        setCombo(fieldMediaCond,  a.getMediaCondition());
        setCombo(fieldSleeveCond, a.getSleeveCondition());
        setText(fieldPrice,   String.valueOf(a.getPurchasePrice()));
        setText(fieldNotes,   a.getNotes());
        if (fieldTracklist != null) fieldTracklist.setText(String.join("\n", a.getTracklist()));
        if (fieldListenNotes != null) fieldListenNotes.clear();
    }

    @FXML private void onAddNew() {
        Album a = new Album();
        storage.addAlbum(a);
        refresh();
        tableView.getSelectionModel().selectLast();
    }

    @FXML private void onSave() {
        Album a = tableView.getSelectionModel().getSelectedItem();
        if (a == null) { AlertUtil.error("No Selection", "Select an album to save."); return; }

        a.setTitle(text(fieldTitle));
        a.setArtist(text(fieldArtist));
        a.setFormat(combo(fieldFormat, "Vinyl LP"));
        a.setGenre(text(fieldGenre));
        a.setMood(combo(fieldMood, ""));
        try { a.setReleaseYear(Integer.parseInt(text(fieldYear))); } catch (NumberFormatException ignored) {}
        a.setLabel(text(fieldLabel));
        a.setCatalogNumber(text(fieldCatalog));
        a.setBarcode(text(fieldBarcode));
        a.setMediaCondition(combo(fieldMediaCond, "NM"));
        a.setSleeveCondition(combo(fieldSleeveCond, "NM"));
        try { a.setPurchasePrice(Double.parseDouble(text(fieldPrice))); } catch (NumberFormatException ignored) {}
        a.setNotes(text(fieldNotes));
        if (fieldTracklist != null && !fieldTracklist.getText().isBlank()) {
            a.setTracklist(List.of(fieldTracklist.getText().split("\n")));
        }

        storage.updateAlbum(a);
        refresh();
        AlertUtil.info("Saved", "Album saved successfully.");
    }

    @FXML private void onDelete() {
        Album a = tableView.getSelectionModel().getSelectedItem();
        if (a == null) return;
        if (AlertUtil.confirm("Delete", "Delete \"" + a.getTitle() + "\"?")) {
            storage.deleteAlbum(a.getId());
            refresh();
        }
    }

    @FXML private void onLogListen() {
        Album a = tableView.getSelectionModel().getSelectedItem();
        if (a == null) { AlertUtil.error("No Selection", "Select an album to log a listen."); return; }
        String notes = fieldListenNotes != null ? fieldListenNotes.getText() : "";
        storage.logListen(a.getId(), notes);
        refresh();
        AlertUtil.info("Logged", "Listen logged for: " + a.getTitle());
    }

    // -----------------------------------------------------------------------

    private void setText(TextField f, String v)    { if (f != null) f.setText(v != null ? v : ""); }
    private void setText(TextArea  f, String v)    { if (f != null) f.setText(v != null ? v : ""); }
    private void setCombo(ComboBox<String> c, String v) { if (c != null) c.setValue(v); }
    private String text(TextField f)  { return f != null ? f.getText().trim() : ""; }
    private String text(TextArea  f)  { return f != null ? f.getText().trim() : ""; }
    private String combo(ComboBox<String> c, String def) { return (c != null && c.getValue() != null) ? c.getValue() : def; }
}
