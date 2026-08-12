package com.vinylvault.controller;

import com.vinylvault.model.CollectionStats;
import com.vinylvault.service.StatsService;
import com.vinylvault.service.StorageService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Map;

public class StatsController {

    @FXML private Label labelTotalOwned;
    @FXML private Label labelTotalWishlist;
    @FXML private Label labelTotalListens;
    @FXML private Label labelTotalSpent;
    @FXML private VBox  vboxFormats;
    @FXML private VBox  vboxDecades;
    @FXML private VBox  vboxTopArtists;

    private StatsService statsService;

    public void init(StorageService storage) {
        statsService = new StatsService(storage);
        refresh();
    }

    public void refresh() {
        CollectionStats s = statsService.compute();

        set(labelTotalOwned,   String.valueOf(s.totalOwned()));
        set(labelTotalWishlist, String.valueOf(s.totalWishlist()));
        set(labelTotalListens,  String.valueOf(s.totalListens()));
        set(labelTotalSpent,   String.format("$%.2f", s.totalSpent()));

        populateVBox(vboxFormats,    s.byFormat());
        populateVBox(vboxDecades,    s.byDecade());
        populateVBox(vboxTopArtists, s.topArtists());
    }

    private void set(Label label, String value) {
        if (label != null) label.setText(value);
    }

    private void populateVBox(VBox box, Map<String, Long> data) {
        if (box == null) return;
        box.getChildren().clear();
        data.forEach((k, v) -> box.getChildren().add(new Label(k + ": " + v)));
    }
}
