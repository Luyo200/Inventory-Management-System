package com.yourname.stockwise.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Utility class to simplify displaying various types of alerts
 * (Information, Warning, Error, and Confirmation) in the StockWise application.
 * <p>
 * This class provides static methods to show alerts without duplicating
 * alert configuration logic throughout the application.
 * </p>
 * 
 * Example usage:
 * <pre>
 *     AlertHelper.showInfo("Success", "Operation completed successfully.");
 *     boolean confirmed = AlertHelper.showConfirmation("Delete", "Are you sure?");
 * </pre>
 * 
 * @author L Mahamba
 * @version 1.0.0
 */
public class AlertHelper {

    /**
     * Shows an informational dialog to the user.
     *
     * @param title   the window title of the alert
     * @param message the body message displayed in the alert
     */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Shows a warning dialog to the user.
     *
     * @param title   the window title of the alert
     * @param message the body message displayed in the alert
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    /**
     * Shows an error dialog to the user.
     *
     * @param title   the window title of the alert
     * @param message the body message displayed in the alert
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /**
     * Shows a confirmation dialog with OK and Cancel buttons.
     *
     * @param title   the window title of the alert
     * @param message the body message asking for user confirmation
     * @return true if the user clicked OK, false if canceled or closed
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Internal reusable method to display alerts of various types.
     *
     * @param type    the AlertType (INFO, WARNING, ERROR)
     * @param title   the title of the alert window
     * @param message the message displayed in the alert body
     */
    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
