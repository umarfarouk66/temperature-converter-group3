import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Main Application class for Temperature Converter.
 * 
 * This class handles the GUI and user interactions for temperature conversion.
 * 
 * @author Umar Farouk Abubakar (CIS/STE/23/1031)
 * @version 1.0
 */
public class App extends Application {

    private TextField inputField;
    private ComboBox<String> fromUnitCombo;
    private ComboBox<String> toUnitCombo;
    private Label resultLabel;
    private Label errorLabel;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the JavaFX stage and sets up the user interface.
     * 
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Temperature Converter - Group 3");

        initializeUIComponents();
        
        Button convertButton = new Button("Convert");
        Button swapButton = new Button("↔ Swap");

        convertButton.setOnAction(e -> performConversion());
        swapButton.setOnAction(e -> swapUnits());

        VBox mainLayout = createMainLayout(convertButton, swapButton);

        Scene scene = new Scene(mainLayout, 500, 430);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeUIComponents() {
        inputField = new TextField();
        inputField.setPromptText("Enter temperature value");
        inputField.setPrefWidth(220);

        fromUnitCombo = createUnitComboBox("Celsius");
        toUnitCombo = createUnitComboBox("Fahrenheit");

        resultLabel = new Label("Result will appear here");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Creates and returns a ComboBox with temperature units.
     * 
     * @param defaultValue the default selected unit
     * @return configured ComboBox
     */
    private ComboBox<String> createUnitComboBox(String defaultValue) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Celsius", "Fahrenheit", "Kelvin");
        combo.setValue(defaultValue);
        return combo;
    }

    /**
     * Creates the main layout of the application.
     */
    private VBox createMainLayout(Button convertButton, Button swapButton) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
            new Label("🌡️ TEMPERATURE CONVERTER"),
            new HBox(10, new Label("Temperature:"), inputField),
            new HBox(10, new Label("From Unit:"), fromUnitCombo),
            new HBox(10, new Label("To Unit:"), toUnitCombo),
            new HBox(15, convertButton, swapButton),
            resultLabel,
            errorLabel
        );
        return layout;
    }

    /**
     * Performs the temperature conversion with proper validation.
     */
    private void performConversion() {
        errorLabel.setText("");
        try {
            double value = parseInput(inputField.getText());
            
            String fromUnit = fromUnitCombo.getValue();
            String toUnit = toUnitCombo.getValue();

            // Assertion as required by assignment
            assert fromUnit != null && toUnit != null : "Unit selection cannot be null";

            double result = convertTemperature(value, fromUnit, toUnit);
            
            resultLabel.setText(String.format("%.2f %s = %.2f %s", 
                               value, fromUnit, result, toUnit));

        } catch (NumberFormatException e) {
            errorLabel.setText("❌ Please enter a valid number!");
        } catch (Exception e) {
            errorLabel.setText("❌ An unexpected error occurred.");
        }
    }

    /**
     * Parses user input and validates it.
     * 
     * @param input the raw input string
     * @return parsed double value
     * @throws NumberFormatException if input is invalid
     */
    private double parseInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Input cannot be empty");
        }
        return Double.parseDouble(input.trim());
    }

    /**
     * Converts temperature from one unit to another.
     * 
     * @param value the temperature value
     * @param from the source unit
     * @param to the target unit
     * @return converted temperature value
     */
    private double convertTemperature(double value, String from, String to) {
        if (from.equals(to)) {
            return value;
        }

        double celsius = toCelsius(value, from);
        return fromCelsius(celsius, to);
    }

    /**
     * Converts any unit to Celsius.
     */
    private double toCelsius(double value, String unit) {
        return switch (unit) {
            case "Celsius" -> value;
            case "Fahrenheit" -> (value - 32) * 5.0 / 9.0;
            case "Kelvin" -> value - 273.15;
            default -> throw new IllegalArgumentException("Invalid source unit: " + unit);
        };
    }

    /**
     * Converts Celsius to target unit.
     */
    private double fromCelsius(double celsius, String unit) {
        return switch (unit) {
            case "Celsius" -> celsius;
            case "Fahrenheit" -> celsius * 9.0 / 5.0 + 32;
            case "Kelvin" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Invalid target unit: " + unit);
        };
    }

    /**
     * Swaps the from and to units.
     */
    private void swapUnits() {
        String temp = fromUnitCombo.getValue();
        fromUnitCombo.setValue(toUnitCombo.getValue());
        toUnitCombo.setValue(temp);
    }
}