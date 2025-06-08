
package com.yourname.stockwise.util;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;

public class AlertHelper {

    /**
     * Shows an information dialog with the given title and message.
     *
     * @param title   the title of the alert window
     * @param message the message to display
     */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Shows a warning dialog with the given title and message.
     *
     * @param title   the title of the alert window
     * @param message the message to display
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    /**
     * Shows an error dialog with the given title and message.
     *
     * @param title   the title of the alert window
     * @param message the message to display
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /**
     * Shows a confirmation dialog with the given title and message.
     * 
     * @param title   the title of the alert window
     * @param message the message to display
     * @return true if the user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // Internal helper method to display basic alerts
    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}