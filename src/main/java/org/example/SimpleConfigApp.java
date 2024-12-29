package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleConfigApp extends Application {
    private static double disturbProbability = 0.1;
    private static int messageRate = 10;
    private static int maxActiveMessages = 10;
    private TextField messageRateField;
    private TextField distortionProbabilityField;
    private TextField maxActiveMessagesField;

    public static synchronized int getMessageRate() {
        return messageRate;
    }

    public static synchronized void setMessageRate(int messageRate) {
        SimpleConfigApp.messageRate = messageRate;
    }

    public static synchronized double getDisturbProbability() {
        return disturbProbability;
    }

    public static synchronized void setDisturbProbability(double disturbProbability) {
        SimpleConfigApp.disturbProbability = disturbProbability;
    }

    public synchronized static int getMaxActiveMessages() {
        return maxActiveMessages;
    }

    public synchronized static void setMaxActiveMessages(int maxActiveMessages) {
        SimpleConfigApp.maxActiveMessages = maxActiveMessages;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Konfiguracja Wiadomości");

        Label messageRateLabel = new Label("Ilość wiadomości na sekundę:");
        messageRateField = new TextField("10");

        Label distortionProbabilityLabel = new Label("Prawdopodobieństwo zniekształcenia (0-1):");
        distortionProbabilityField = new TextField("0.1");

        Button saveButton = new Button("Zapisz");
        saveButton.setOnAction(e -> saveConfig());

        Label maxActiveMessagesLabel = new Label("Maksymalna ilość aktywnych wiadomości:");
        maxActiveMessagesField = new TextField("10");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                messageRateLabel,
                messageRateField,
                distortionProbabilityLabel,
                distortionProbabilityField,
                maxActiveMessagesLabel,
                maxActiveMessagesField,
                saveButton
        );

        Scene scene = new Scene(layout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void launchConfigApp() {
        new Thread(() -> Application.launch(SimpleConfigApp.class)).start();
    }

    private void saveConfig() {
        String messageRateText = messageRateField.getText();
        String distortionProbabilityText = distortionProbabilityField.getText();
        String maxActiveMessagesText = maxActiveMessagesField.getText();

        try {
            int rate = Integer.parseInt(messageRateText);
            double probability = Double.parseDouble(distortionProbabilityText);
            int maxActive = Integer.parseInt(maxActiveMessagesText);

            if (rate < 0 || probability < 0 || probability > 1) {
                showAlert("Błąd", "Niepoprawne wartości! Upewnij się, że wartości są prawidłowe.");
                return;
            }

            setMessageRate(rate);
            setDisturbProbability(probability);
            setMaxActiveMessages(maxActive);
        } catch (NumberFormatException e) {
            showAlert("Błąd", "Wprowadź poprawne liczby.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
