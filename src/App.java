import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI Application class for the Temperature Converter.
 * 
 * This application is designed for Group 3 (Software Construction II) and
 * implements a premium, modular, and real-time interactive user interface.
 * Features include:
 * <ul>
 *   <li>Real-time reactive conversion</li>
 *   <li>Visual 3D Canvas-rendered thermometer</li>
 *   <li>Step-by-step arithmetic path explainer</li>
 *   <li>Conversion history log tracker</li>
 *   <li>Frosted-glass dark mode styling via custom CSS</li>
 * </ul>
 * 
 * @author Umar Farouk Abubakar (CIS/STE/23/1031)
 * @author Khaleed Yusuf Salisu (CIS/STE/23/1023)
 * @author Aliyu Hassan Muhammad (CIS/STE/23/1024)
 * @author Kabir Aliyu Nadada (CIS/STE/23/1028)
 * @author Ismail Ibrahim Musa (CIS/STE/23/1032)
 * @author Habib Lawal (CIS/STE/23/1025)
 * @author Usman Nuhu (CIS/STE/23/1027)
 * @author Aliyu Kabir (CIS/STE/23/1026)
 * @version 2.0
 */
public class App extends Application {

    // UI Input Controls
    private TextField inputField;
    private ComboBox<TemperatureUnit> fromUnitCombo;
    private ComboBox<TemperatureUnit> toUnitCombo;

    // UI Output Displays
    private Label bigResultLabel;
    private Label formulaLabel;
    private Label errorLabel;
    private VBox historyListContainer;

    // Custom UI Components
    private ThermometerGauge thermometer;

    // Historical Records
    private final List<String> conversionHistory = new ArrayList<>();
    private static final int MAX_HISTORY_ITEMS = 5;

    /**
     * Entry point for running the desktop GUI application.
     * 
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts and initializes the JavaFX Primary Stage.
     * 
     * @param primaryStage the primary stage window
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Thermodynamic Converter - Group 3");

        // Initialize and wire UI elements
        initializeUIComponents();

        // Assemble Left and Right Panels
        VBox leftColumn = createLeftColumn();
        VBox rightColumn = createRightColumn();

        // Assemble Main HBox Card Layout
        HBox mainCard = new HBox(30);
        mainCard.getStyleClass().add("glass-card");
        mainCard.setPadding(new Insets(30));
        mainCard.setAlignment(Pos.CENTER);
        mainCard.getChildren().addAll(leftColumn, rightColumn);

        // Put card inside a root layout for background styling
        StackPane root = new StackPane(mainCard);
        root.getStyleClass().add("root-pane");
        root.setPadding(new Insets(20));

        // Create and configure Scene
        Scene scene = new Scene(root, 770, 520);
        
        // Load stylesheet securely
        try {
            String cssPath = getClass().getResource("style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Warning: Custom style.css could not be loaded. Falling back to default styles.");
        }

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Run initial default conversion
        performConversion(false);
    }

    /**
     * Initializes all the interface controls, custom canvas elements, and event listeners.
     */
    private void initializeUIComponents() {
        // Input textfield
        inputField = new TextField("0");
        inputField.setPromptText("e.g. 25, -10.5");
        inputField.getStyleClass().add("modern-text-field");

        // Initialize Custom Canvas Thermometer
        thermometer = new ThermometerGauge();

        // Unit Dropdowns
        fromUnitCombo = new ComboBox<>();
        fromUnitCombo.getItems().addAll(TemperatureUnit.values());
        fromUnitCombo.setValue(TemperatureUnit.CELSIUS);
        fromUnitCombo.getStyleClass().add("modern-combo-box");

        toUnitCombo = new ComboBox<>();
        toUnitCombo.getItems().addAll(TemperatureUnit.values());
        toUnitCombo.setValue(TemperatureUnit.FAHRENHEIT);
        toUnitCombo.getStyleClass().add("modern-combo-box");

        // Result displays
        bigResultLabel = new Label("32.00 °F");
        bigResultLabel.getStyleClass().add("big-result-label");

        formulaLabel = new Label();
        formulaLabel.getStyleClass().add("formula-label");
        formulaLabel.setWrapText(true);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);

        // History container
        historyListContainer = new VBox(6);
        historyListContainer.setAlignment(Pos.TOP_LEFT);

        // Real-time reactive events: Convert as the user types
        inputField.textProperty().addListener((obs, oldVal, newVal) -> performConversion(false));
        fromUnitCombo.valueProperty().addListener((obs, oldVal, newVal) -> performConversion(false));
        toUnitCombo.valueProperty().addListener((obs, oldVal, newVal) -> performConversion(false));
    }

    /**
     * Assembles the Left column of the application layout.
     * Contains inputs, swaps, formula explainer, and class credits.
     * 
     * @return constructed VBox panel
     */
    private VBox createLeftColumn() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPrefWidth(380);

        // Header Panel
        VBox header = new VBox(2);
        Label title = new Label("🌡️ TEMPERATURE CONVERTER");
        title.getStyleClass().add("title-label");
        Label subtitle = new Label("Group 3 • Software Construction II");
        subtitle.getStyleClass().add("subtitle-label");
        header.getChildren().addAll(title, subtitle);

        // Input Grid Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 10, 0));

        Label tempLabel = new Label("Temperature Value:");
        tempLabel.getStyleClass().add("form-label");
        
        Label fromLabel = new Label("From Scale:");
        fromLabel.getStyleClass().add("form-label");

        Label toLabel = new Label("To Scale:");
        toLabel.getStyleClass().add("form-label");

        grid.add(tempLabel, 0, 0);
        grid.add(inputField, 1, 0);
        grid.add(fromLabel, 0, 1);
        grid.add(fromUnitCombo, 1, 1);
        grid.add(toLabel, 0, 2);
        grid.add(toUnitCombo, 1, 2);

        // Buttons Layout
        Button swapButton = new Button("↔ Swap Units");
        swapButton.getStyleClass().add("secondary-button");
        swapButton.setOnAction(e -> swapUnits());

        Button convertButton = new Button("Convert");
        convertButton.getStyleClass().add("primary-button");
        convertButton.setOnAction(e -> performConversion(true)); // Explicit check adding to history

        HBox btnContainer = new HBox(12, swapButton, convertButton);
        btnContainer.setPadding(new Insets(5, 0, 10, 0));

        // Formula Card Panel
        VBox formulaCard = new VBox(8);
        formulaCard.getStyleClass().add("formula-card");
        formulaCard.setPadding(new Insets(12));
        Label formulaHeader = new Label("MATHEMATICAL FORMULA EXPLAINER");
        formulaHeader.getStyleClass().add("formula-header");
        formulaCard.getChildren().addAll(formulaHeader, formulaLabel);

        // App Footer Credits
        Label footer = new Label("Umar Farouk A. • Khaleed Y. • Aliyu H. • Kabir A. • Ismail I. • Habib L. • Usman N. • Aliyu K.");
        footer.getStyleClass().add("footer-label");

        panel.getChildren().addAll(header, grid, btnContainer, formulaCard, errorLabel, footer);
        return panel;
    }

    /**
     * Assembles the Right column of the application layout.
     * Contains the dynamic canvas thermometer, big result, and scrollable history log.
     * 
     * @return constructed VBox panel
     */
    private VBox createRightColumn() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(280);

        // Dynamic visual thermometer container
        StackPane gaugeContainer = new StackPane(thermometer);
        gaugeContainer.setPadding(new Insets(10));
        
        // Dynamic large result card
        VBox resultCard = new VBox(2, bigResultLabel);
        resultCard.setAlignment(Pos.CENTER);
        resultCard.getStyleClass().add("result-card");
        resultCard.setPadding(new Insets(10, 15, 10, 15));

        // Historical Log container
        VBox historySection = new VBox(6);
        historySection.setAlignment(Pos.TOP_LEFT);
        Label historyTitle = new Label("RECENT CONVERSIONS LOG");
        historyTitle.getStyleClass().add("history-header");

        ScrollPane scrollHistory = new ScrollPane(historyListContainer);
        scrollHistory.setFitToWidth(true);
        scrollHistory.setPrefHeight(100);
        scrollHistory.getStyleClass().add("history-scroll-pane");

        historySection.getChildren().addAll(historyTitle, scrollHistory);

        panel.getChildren().addAll(gaugeContainer, resultCard, historySection);
        return panel;
    }

    /**
     * Executes the conversion routine.
     * 
     * @param isExplicit true if clicked by user (triggers strict errors and history logging)
     */
    private void performConversion(boolean isExplicit) {
        String inputStr = inputField.getText();
        
        // Reset error message on active typing
        if (!isExplicit) {
            errorLabel.setText("");
        }

        // Graceful handling of empty or blank fields during typing
        if (inputStr == null || inputStr.trim().isEmpty()) {
            if (isExplicit) {
                errorLabel.setText("❌ Input cannot be empty! Please enter a value.");
            }
            bigResultLabel.setText("---");
            formulaLabel.setText("Awaiting valid numerical input...");
            return;
        }

        try {
            double value = Double.parseDouble(inputStr.trim());
            TemperatureUnit fromUnit = fromUnitCombo.getValue();
            TemperatureUnit toUnit = toUnitCombo.getValue();

            // Strict internal assertion check
            assert fromUnit != null && toUnit != null : "Assertion failed: Selected units must not be null";

            // Process mathematical conversion
            double result = TemperatureConverter.convert(value, fromUnit, toUnit);

            // Clear any lingering errors
            errorLabel.setText("");

            // Update UI Outputs
            bigResultLabel.setText(String.format("%.2f %s", result, toUnit.getSymbol()));
            
            // Draw interactive explanation formula path
            String mathPath = TemperatureConverter.getFormulaExplanation(value, fromUnit, toUnit, result);
            formulaLabel.setText(mathPath);

            // Update custom drawing thermometer
            thermometer.updateTemperature(value, fromUnit);

            // Add item to history log on explicit request
            if (isExplicit) {
                String logEntry = String.format("%.2f%s ➔ %.2f%s", value, fromUnit.getSymbol(), result, toUnit.getSymbol());
                addToHistory(logEntry);
            }

        } catch (NumberFormatException e) {
            bigResultLabel.setText("Error");
            formulaLabel.setText("Unable to compute: invalid input formatting.");
            if (isExplicit) {
                errorLabel.setText("❌ Invalid number format! Please enter a valid decimal number.");
            }
        } catch (BelowAbsoluteZeroException e) {
            bigResultLabel.setText("Limit");
            formulaLabel.setText("Conversion mathematically impossible below physical Absolute Zero.");
            errorLabel.setText("⚠️ " + e.getMessage());
            thermometer.updateTemperature(Double.NEGATIVE_INFINITY, TemperatureUnit.CELSIUS); // Reset visual
        } catch (Exception e) {
            bigResultLabel.setText("Error");
            formulaLabel.setText("An unexpected runtime aberration occurred.");
            if (isExplicit) {
                errorLabel.setText("❌ Critical: An unexpected error has occurred.");
            }
        }
    }

    /**
     * Swaps the source unit and target unit dropdown selections, then
     * automatically recalculates the results.
     */
    private void swapUnits() {
        TemperatureUnit from = fromUnitCombo.getValue();
        TemperatureUnit to = toUnitCombo.getValue();
        
        fromUnitCombo.setValue(to);
        toUnitCombo.setValue(from);
        
        performConversion(false);
    }

    /**
     * Appends a new successful conversion record to the history cache
     * and shifts older logs downward.
     * 
     * @param logEntry the formatted log string to push
     */
    private void addToHistory(String logEntry) {
        // Avoid duplicate logging of the same consecutive result
        if (!conversionHistory.isEmpty() && conversionHistory.get(0).equals(logEntry)) {
            return;
        }

        conversionHistory.add(0, logEntry); // Push to front

        if (conversionHistory.size() > MAX_HISTORY_ITEMS) {
            conversionHistory.remove(conversionHistory.size() - 1); // Cap history count
        }

        // Render history records in VBox
        historyListContainer.getChildren().clear();
        for (String record : conversionHistory) {
            Label label = new Label(record);
            label.getStyleClass().add("history-item-label");
            historyListContainer.getChildren().add(label);
        }
    }
}