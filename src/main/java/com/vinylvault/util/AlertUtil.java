package com.vinylvault.util;

import com.vinylvault.service.ThemeService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class AlertUtil {

    private AlertUtil() {}

    /** Dialogs build their own scene, so they need the theme applied explicitly. */
    private static void theme(Alert alert) {
        ThemeService.getInstance().register(alert.getDialogPane().getScene());
    }

    public static void info(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    public static void error(String title, String message) {
        show(Alert.AlertType.ERROR, title, message);
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        theme(alert);
        Optional<ButtonType> result = alert.showAndWait();
        ThemeService.getInstance().unregister(alert.getDialogPane().getScene());
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        theme(alert);
        alert.showAndWait();
        ThemeService.getInstance().unregister(alert.getDialogPane().getScene());
    }
}
