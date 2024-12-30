package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SimpleConfigApp extends Application {
    private static int messageRate = 10;
    private static int maxActiveMessages = 10;

    private TextField messageRateField;
    private TextField maxActiveMessagesField;

    private static final Map<Integer, NodeConfig> nodeConfigs = new HashMap<>();
    private static final Map<Integer, TextField> probabilityFields = new HashMap<>();
    private static final Map<Integer, ComboBox<String>> errorTypeSelectors = new HashMap<>();

    private static final int[] nodes = {8081, 8082, 8083, 8084, 8085, 8086, 8087};

    public static synchronized int getMessageRate() {
        return messageRate;
    }

    public static synchronized void setMessageRate(int messageRate) {
        SimpleConfigApp.messageRate = messageRate;
    }

    public synchronized static int getMaxActiveMessages() {
        return maxActiveMessages;
    }

    public synchronized static void setMaxActiveMessages(int maxActiveMessages) {
        SimpleConfigApp.maxActiveMessages = maxActiveMessages;
    }

    public static synchronized double getNodeProbability(int node) {
        return nodeConfigs.getOrDefault(node, new NodeConfig()).getProbability();
    }

    public static synchronized ErrorType getNodeErrorType(int node) {
        return nodeConfigs.getOrDefault(node, new NodeConfig()).getErrorType();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Konfiguracja Węzłów i Wiadomości");

        VBox mainLayout = new VBox(10);

        Label messageRateLabel = new Label("Ilość wiadomości na sekundę:");
        messageRateField = new TextField(String.valueOf(messageRate));

        Label maxActiveMessagesLabel = new Label("Maksymalna ilość aktywnych wiadomości:");
        maxActiveMessagesField = new TextField(String.valueOf(maxActiveMessages));

        mainLayout.getChildren().addAll(
                messageRateLabel,
                messageRateField,
                maxActiveMessagesLabel,
                maxActiveMessagesField
        );

        VBox nodeConfigLayout = new VBox(10);

        for (int node : nodes) {
            Label nodeLabel = new Label("Węzeł: " + node);
            TextField probabilityField = new TextField("0.1");
            ComboBox<String> errorTypeSelector = new ComboBox<>();
            errorTypeSelector.getItems().addAll(ErrorType.getAllNames());
            errorTypeSelector.setValue(ErrorType.DIRECTIONAL_0_TO_1.getName());

            nodeConfigs.put(node, new NodeConfig(0.1, ErrorType.DIRECTIONAL_0_TO_1));

            probabilityFields.put(node, probabilityField);
            errorTypeSelectors.put(node, errorTypeSelector);

            nodeConfigLayout.getChildren().addAll(
                    nodeLabel,
                    new Label("Prawdopodobieństwo błędu (0-1):"),
                    probabilityField,
                    new Label("Rodzaj błędu:"),
                    errorTypeSelector
            );
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(nodeConfigLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainLayout.getChildren().add(scrollPane);

        Button saveButton = new Button("Zapisz");
        saveButton.setOnAction(e -> saveConfig());
        mainLayout.getChildren().add(saveButton);

        Scene scene = new Scene(mainLayout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveConfig() {
        String messageRateText = messageRateField.getText();
        String maxActiveMessagesText = maxActiveMessagesField.getText();

        try {
            int rate = Integer.parseInt(messageRateText);
            int maxActive = Integer.parseInt(maxActiveMessagesText);

            if (rate < 0 || maxActive <= 0) {
                showAlert("Błąd", "Niepoprawne wartości! Upewnij się, że wartości są prawidłowe.");
                return;
            }

            setMessageRate(rate);
            setMaxActiveMessages(maxActive);

            for (int node : nodes) {
                TextField probabilityField = probabilityFields.get(node);
                ComboBox<String> errorTypeSelector = errorTypeSelectors.get(node);

                double probability = Double.parseDouble(probabilityField.getText());
                ErrorType errorType = ErrorType.getByName(errorTypeSelector.getValue());

                if (probability < 0 || probability > 1) {
                    showAlert("Błąd", "Niepoprawne prawdopodobieństwo dla węzła " + node);
                    return;
                }

                nodeConfigs.put(node, new NodeConfig(probability, errorType));
            }

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

    public static void launchConfigApp() {
        new Thread(() -> Application.launch(SimpleConfigApp.class)).start();
    }

    public static class NodeConfig {
        private final double probability;
        private final ErrorType errorType;

        public NodeConfig() {
            this.probability = 0.1;
            this.errorType = ErrorType.DIRECTIONAL_0_TO_1;
        }

        public NodeConfig(double probability, ErrorType errorType) {
            this.probability = probability;
            this.errorType = errorType;
        }

        public double getProbability() {
            return probability;
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }
}
