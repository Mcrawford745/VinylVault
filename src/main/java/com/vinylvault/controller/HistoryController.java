package com.vinylvault.controller;

import com.vinylvault.model.ListeningLog;
import com.vinylvault.service.StorageService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HistoryController {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    @FXML private TableView<ListeningLog> tableView;
    @FXML private TableColumn<ListeningLog, String> colDate;
    @FXML private TableColumn<ListeningLog, String> colArtist;
    @FXML private TableColumn<ListeningLog, String> colTitle;
    @FXML private TableColumn<ListeningLog, String> colFormat;
    @FXML private TableColumn<ListeningLog, String> colNotes;

    private StorageService storage;

    public void init(StorageService storage) {
        this.storage = storage;

        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                FMT.format(Instant.parse(c.getValue().getListenedAt()))));
        colArtist.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArtist()));
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAlbumTitle()));
        colFormat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormat()));
        colNotes.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotes()));

        refresh();
    }

    public void refresh() {
        tableView.setItems(FXCollections.observableArrayList(storage.getDb().getListeningHistory()));
    }
}
