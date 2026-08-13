package com.vinylvault.controller;

import com.vinylvault.service.ExporterService;
import com.vinylvault.service.StorageService;
import com.vinylvault.service.ThemeService;
import com.vinylvault.util.AlertUtil;
import com.vinylvault.util.Theme;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Tab tabCollection;
    @FXML private Tab tabWishlist;
    @FXML private Tab tabHistory;
    @FXML private Tab tabStats;
    @FXML private ComboBox<Theme> comboTheme;

    private StorageService storage;
    private ExporterService exporter;

    // Child controllers — set via FXML fx:include
    @FXML private CollectionController collectionController;
    @FXML private WishlistController   wishlistController;
    @FXML private HistoryController    historyController;
    @FXML private StatsController      statsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage  = new StorageService();
        exporter = new ExporterService(storage);

        // Inject shared storage into child controllers
        if (collectionController != null) collectionController.init(storage);
        if (wishlistController   != null) wishlistController.init(storage);
        if (historyController    != null) historyController.init(storage);
        if (statsController      != null) statsController.init(storage);

        // Theme picker — reflects the persisted choice, applies on selection
        ThemeService themes = ThemeService.getInstance();
        comboTheme.getItems().setAll(Theme.values());
        comboTheme.setValue(themes.getCurrent());
        comboTheme.valueProperty().addListener((obs, old, now) -> themes.setTheme(now));

        // Refresh stats tab when selected
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
            if (now == tabStats && statsController != null) statsController.refresh();
            if (now == tabHistory && historyController != null) historyController.refresh();
        });
    }

    @FXML
    private void onExportHtml() {
        try {
            Path out = exporter.export();
            AlertUtil.info("Export Successful", "Web viewer exported to:\n" + out.toAbsolutePath());
        } catch (Exception e) {
            AlertUtil.error("Export Failed", e.getMessage());
        }
    }

    @FXML
    private void onOpenLucky() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lucky_modal.fxml"));
            javafx.scene.Parent root = loader.load();
            LuckyModalController ctrl = loader.getController();
            ctrl.init(storage);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("I'm Feeling Lucky");
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 480, 360);
            ThemeService.getInstance().register(scene);
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
            ThemeService.getInstance().unregister(scene);
        } catch (Exception e) {
            AlertUtil.error("Error", "Could not open Lucky modal: " + e.getMessage());
        }
    }
}
