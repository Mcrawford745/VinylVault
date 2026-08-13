package com.vinylvault.controller;

import com.vinylvault.model.Album;
import com.vinylvault.service.LuckyService;
import com.vinylvault.service.StorageService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class LuckyModalController {

    @FXML private ComboBox<String> comboFormat;
    @FXML private ComboBox<String> comboMood;
    @FXML private RadioButton radioRandom;
    @FXML private RadioButton radioFormat;
    @FXML private RadioButton radioMood;
    @FXML private RadioButton radioForgotten;
    @FXML private Label labelResult;
    @FXML private Label labelArtist;
    @FXML private Label labelFormat;

    private LuckyService lucky;

    public void init(StorageService storage) {
        lucky = new LuckyService(storage);

        comboFormat.getItems().addAll("ALL", "Vinyl LP", "Vinyl 7\"", "CD", "Cassette");
        comboFormat.setValue("ALL");

        comboMood.getItems().addAll("Chill", "Energetic", "Focus", "Dark", "Happy", "Sad", "Party");
        comboMood.setValue("Chill");
    }

    @FXML
    private void onSpin() {
        Optional<Album> result;

        if (radioFormat != null && radioFormat.isSelected()) {
            result = lucky.pickByFormat(comboFormat.getValue());
        } else if (radioMood != null && radioMood.isSelected()) {
            result = lucky.pickByMood(comboMood.getValue());
        } else if (radioForgotten != null && radioForgotten.isSelected()) {
            result = lucky.pickForgotten();
        } else {
            result = lucky.pickRandom();
        }

        result.ifPresentOrElse(a -> {
            labelResult.setText(a.getTitle());
            if (labelArtist != null) labelArtist.setText(a.getArtist());
            if (labelFormat != null) labelFormat.setText(a.getFormat() + " · " + a.getReleaseYear());
        }, () -> {
            labelResult.setText("No matching records found.");
            if (labelArtist != null) labelArtist.setText("");
            if (labelFormat != null) labelFormat.setText("");
        });
    }
}
