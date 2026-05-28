import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI Application class for the Temperature Converter.
 * 
 * This application is designed for Group 3 (Software Construction II) and
 * implements a premium, modular, and real-time interactive user interface.
 * All classes are consolidated into this single file for convenience while
 * preserving complete OOP modularity and architectural specifications.
 * 
 * Features include:
 * <ul>
 *   <li>Real-time reactive conversion (updates instantly as you type)</li>
 *   <li>Visual 3D Canvas-rendered thermometer showing current warmth/coolness</li>
 *   <li>Step-by-step arithmetic path explainer</li>
 *   <li>Conversion history log tracker showing the last 5 conversions</li>
 *   <li>Frosted-glass dark mode styling via a custom CSS system</li>
 *   <li>Strong validation preventing inputs below physical Absolute Zero</li>
 *   <li>Robust error handling and custom exceptions</li>
 *   <li>Verification contracts backed by active JVM Assertions</li>
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
     * Default constructor for the JavaFX App class.
     */
    public App() {
        super();
    }

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

/**
 * Enumeration representing the supported temperature units.
 * 
 * Each unit maintains its descriptive name, unit symbol, and physical lower limit (Absolute Zero).
 * 
 * @author Group 3
 * @version 1.0
 */
enum TemperatureUnit {
    CELSIUS("Celsius", "°C", -273.15),
    FAHRENHEIT("Fahrenheit", "°F", -459.67),
    KELVIN("Kelvin", "K", 0.0),
    RANKINE("Rankine", "°R", 0.0);

    private final String name;
    private final String symbol;
    private final double absoluteZero;

    /**
     * Constructs a TemperatureUnit with descriptive attributes.
     * 
     * @param name         the user-friendly name of the unit
     * @param symbol       the mathematical symbol of the unit (e.g., °C, K)
     * @param absoluteZero the theoretical lower limit of temperature in this unit
     */
    TemperatureUnit(String name, String symbol, double absoluteZero) {
        this.name = name;
        this.symbol = symbol;
        this.absoluteZero = absoluteZero;
    }

    /**
     * Gets the user-friendly name of this unit.
     * 
     * @return the name string
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the mathematical symbol representing this unit.
     * 
     * @return the symbol string (e.g., "°C")
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the thermodynamic limit of absolute zero in this unit.
     * 
     * @return the absolute zero temperature value
     */
    public double getAbsoluteZero() {
        return absoluteZero;
    }

    /**
     * Finds a TemperatureUnit by its display name.
     * 
     * @param name the display name to match (case-insensitive)
     * @return the matching TemperatureUnit
     * @throws IllegalArgumentException if no match is found or name is null
     */
    public static TemperatureUnit fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Unit name cannot be null");
        }
        for (TemperatureUnit unit : values()) {
            if (unit.getName().equalsIgnoreCase(name.trim())) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown temperature unit: " + name);
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}

/**
 * Custom Exception thrown when a temperature value falls below the physical limit of Absolute Zero.
 * 
 * @author Group 3
 * @version 1.0
 */
class BelowAbsoluteZeroException extends IllegalArgumentException {
    
    private final double invalidValue;
    private final TemperatureUnit unit;

    /**
     * Constructs a BelowAbsoluteZeroException with the invalid value and its corresponding unit.
     * 
     * @param invalidValue the temperature value that is below absolute zero
     * @param unit         the TemperatureUnit of the value
     */
    public BelowAbsoluteZeroException(double invalidValue, TemperatureUnit unit) {
        super(String.format("Temperature %.2f%s is below the physical limit of Absolute Zero (%.2f%s)!", 
                            invalidValue, unit.getSymbol(), unit.getAbsoluteZero(), unit.getSymbol()));
        this.invalidValue = invalidValue;
        this.unit = unit;
    }

    /**
     * Gets the invalid temperature value that caused the exception.
     * 
     * @return the invalid value
     */
    public double getInvalidValue() {
        return invalidValue;
    }

    /**
     * Gets the unit of the invalid temperature value.
     * 
     * @return the temperature unit
     */
    public TemperatureUnit getUnit() {
        return unit;
    }
}

/**
 * Core utility and service class for handling temperature conversion arithmetic,
 * validation, and mathematical explanations.
 * 
 * @author Group 3
 * @version 1.0
 */
final class TemperatureConverter {

    // Suppress default constructor for non-instantiability
    private TemperatureConverter() {
        throw new AssertionError("Suppress utility class instantiation");
    }

    /**
     * Converts a temperature value from a source unit to a target unit.
     * 
     * @param value the temperature value to convert
     * @param from  the unit of the input value
     * @param to    the unit to convert the value to
     * @return the converted temperature value
     * @throws IllegalArgumentException if {@code from} or {@code to} is null
     * @throws BelowAbsoluteZeroException if {@code value} is below the physical limit of {@code from}
     */
    public static double convert(double value, TemperatureUnit from, TemperatureUnit to) {
        // Precondition validations
        if (from == null || to == null) {
            throw new IllegalArgumentException("Temperature units cannot be null");
        }

        if (value < from.getAbsoluteZero()) {
            throw new BelowAbsoluteZeroException(value, from);
        }

        // Assertion to verify preconditions at runtime if enabled (-ea)
        assert value >= from.getAbsoluteZero() : "Precondition violation: Temperature below Absolute Zero";

        // Perform calculation
        double kelvin = toKelvin(value, from);
        double result = fromKelvin(kelvin, to);

        // Postcondition assertions to guarantee mathematical correctness
        assert result >= to.getAbsoluteZero() : "Postcondition violation: Output below Absolute Zero in target unit";
        assert Math.abs(toKelvin(result, to) - kelvin) < 1e-9 : "Postcondition violation: Conversion is not invertible";

        return result;
    }

    /**
     * Converts any temperature value from its original unit to Kelvin.
     * 
     * @param value the temperature value to convert
     * @param unit  the unit of the value
     * @return the temperature value in Kelvin
     */
    public static double toKelvin(double value, TemperatureUnit unit) {
        assert unit != null : "Unit cannot be null in toKelvin";
        
        return switch (unit) {
            case KELVIN -> value;
            case CELSIUS -> value + 273.15;
            case FAHRENHEIT -> (value - 32.0) * 5.0 / 9.0 + 273.15;
            case RANKINE -> value / 1.8;
        };
    }

    /**
     * Converts any temperature value from its original unit to Celsius.
     * 
     * @param value the temperature value to convert
     * @param unit  the unit of the value
     * @return the temperature value in Celsius
     */
    public static double toCelsius(double value, TemperatureUnit unit) {
        assert unit != null : "Unit cannot be null in toCelsius";
        double kelvin = toKelvin(value, unit);
        return fromKelvin(kelvin, TemperatureUnit.CELSIUS);
    }

    /**
     * Converts a temperature value in Kelvin to the specified target unit.
     * 
     * @param kelvin the temperature value in Kelvin
     * @param unit   the target unit
     * @return the converted temperature value in the target unit
     */
    public static double fromKelvin(double kelvin, TemperatureUnit unit) {
        assert unit != null : "Unit cannot be null in fromKelvin";
        assert kelvin >= 0.0 : "Kelvin temperature cannot be negative";

        return switch (unit) {
            case KELVIN -> kelvin;
            case CELSIUS -> kelvin - 273.15;
            case FAHRENHEIT -> (kelvin - 273.15) * 9.0 / 5.0 + 32.0;
            case RANKINE -> kelvin * 1.8;
        };
    }

    /**
     * Generates a clear, user-friendly step-by-step mathematical formula explanation 
     * for a conversion process.
     * 
     * @param value  the initial value
     * @param from   the initial unit
     * @param to     the target unit
     * @param result the calculated result
     * @return a formatted formula representation
     */
    public static String getFormulaExplanation(double value, TemperatureUnit from, TemperatureUnit to, double result) {
        if (from == to) {
            return String.format("Values are in the same unit (%s). No conversion necessary.", from.getSymbol());
        }

        return switch (from) {
            case CELSIUS -> switch (to) {
                case FAHRENHEIT -> String.format("[°F] = (%.2f°C × 9/5) + 32 = %.2f°F", value, result);
                case KELVIN -> String.format("[K] = %.2f°C + 273.15 = %.2f K", value, result);
                case RANKINE -> String.format("[°R] = (%.2f°C + 273.15) × 1.8 = %.2f°R", value, result);
                default -> "";
            };
            case FAHRENHEIT -> switch (to) {
                case CELSIUS -> String.format("[°C] = (%.2f°F − 32) × 5/9 = %.2f°C", value, result);
                case KELVIN -> String.format("[K] = (%.2f°F − 32) × 5/9 + 273.15 = %.2f K", value, result);
                case RANKINE -> String.format("[°R] = %.2f°F + 459.67 = %.2f°R", value, result);
                default -> "";
            };
            case KELVIN -> switch (to) {
                case CELSIUS -> String.format("[°C] = %.2f K − 273.15 = %.2f°C", value, result);
                case FAHRENHEIT -> String.format("[°F] = (%.2f K − 273.15) × 9/5 + 32 = %.2f°F", value, result);
                case RANKINE -> String.format("[°R] = %.2f K × 1.8 = %.2f°R", value, result);
                default -> "";
            };
            case RANKINE -> switch (to) {
                case CELSIUS -> String.format("[°C] = (%.2f°R − 491.67) × 5/9 = %.2f°C", value, result);
                case FAHRENHEIT -> String.format("[°F] = %.2f°R − 459.67 = %.2f°F", value, result);
                case KELVIN -> String.format("[K] = %.2f°R ÷ 1.8 = %.2f K", value, result);
                default -> "";
            };
        };
    }
}

/**
 * A custom-drawn UI component that renders a stunning, dynamic, 3D glass thermometer.
 * 
 * @author Group 3
 * @version 1.0
 */
class ThermometerGauge extends Canvas {

    private double celsiusValue = 0.0;

    // Visual Constants
    private static final double WIDTH = 100.0;
    private static final double HEIGHT = 320.0;

    private static final double BULB_CX = 50.0;
    private static final double BULB_CY = 270.0;
    private static final double BULB_RADIUS = 20.0;

    private static final double TUBE_LEFT = 42.0;
    private static final double TUBE_WIDTH = 16.0;
    private static final double TUBE_TOP = 30.0;
    private static final double TUBE_BOTTOM = 255.0;
    private static final double TUBE_HEIGHT = TUBE_BOTTOM - TUBE_TOP;

    // Thermodynamic Limits for Visual Scale (-30°C to 100°C)
    private static final double SCALE_MIN_C = -30.0;
    private static final double SCALE_MAX_C = 100.0;

    /**
     * Constructs a ThermometerGauge component with default dimensions.
     */
    public ThermometerGauge() {
        super(WIDTH, HEIGHT);
        draw();
    }

    /**
     * Updates the gauge with a new temperature value.
     * 
     * @param value the temperature value
     * @param unit  the unit of the temperature value
     */
    public void updateTemperature(double value, TemperatureUnit unit) {
        if (unit == null) return;
        
        try {
            // Convert to Celsius for visual scale representation
            this.celsiusValue = TemperatureConverter.toCelsius(value, unit);
        } catch (Exception e) {
            // Safe fallback on validation errors
            this.celsiusValue = 0.0;
        }
        draw();
    }

    /**
     * Redraws the thermometer onto the Canvas graphics context.
     */
    private void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Calculate filling percentage (clamp between 0 and 1)
        double ratio = (celsiusValue - SCALE_MIN_C) / (SCALE_MAX_C - SCALE_MIN_C);
        ratio = Math.max(0.0, Math.min(1.0, ratio));

        double fluidTopY = TUBE_BOTTOM - (TUBE_HEIGHT * ratio);

        // Determine fluid colors based on Celsius temperature
        Color fluidColorStart;
        Color fluidColorEnd;

        if (celsiusValue < 10.0) {
            // Cold: Blue-Cyan glow
            fluidColorStart = Color.web("#00d2ff");
            fluidColorEnd = Color.web("#0066ff");
        } else if (celsiusValue <= 32.0) {
            // Mild: Emerald Teal-Green glow
            fluidColorStart = Color.web("#2ecc71");
            fluidColorEnd = Color.web("#1abc9c");
        } else {
            // Hot: Coral Orange-Crimson glow
            fluidColorStart = Color.web("#ff9f43");
            fluidColorEnd = Color.web("#ee5253");
        }

        // --- 1. Draw Outer Glass Tube Background (Shadow effect) ---
        gc.setFill(Color.color(1.0, 1.0, 1.0, 0.05)); // Frosted inside
        gc.fillRoundRect(TUBE_LEFT, TUBE_TOP - 5.0, TUBE_WIDTH, TUBE_HEIGHT + 20.0, TUBE_WIDTH, TUBE_WIDTH);
        
        // --- 2. Draw Scale Markings (Ticks and Numbers) on the Left ---
        gc.setFont(Font.font("Segoe UI", 10.0));
        gc.setFill(Color.web("#a4b0be"));
        gc.setStroke(Color.color(1.0, 1.0, 1.0, 0.15));
        gc.setLineWidth(1.0);

        // Draw ticks from -20°C to 100°C in steps of 20
        for (int temp = -20; temp <= 100; temp += 20) {
            double tickRatio = (temp - SCALE_MIN_C) / (SCALE_MAX_C - SCALE_MIN_C);
            double tickY = TUBE_BOTTOM - (TUBE_HEIGHT * tickRatio);
            
            // Ticks
            gc.strokeLine(TUBE_LEFT - 6, tickY, TUBE_LEFT - 1, tickY);
            // Numbers
            gc.fillText(temp + "°", TUBE_LEFT - 26, tickY + 4);
        }

        // --- 3. Draw Thermometer Fluid (Bulb and Column) ---
        // Fluid Bulb (bottom sphere)
        LinearGradient bulbGrad = new LinearGradient(
            0, BULB_CY - BULB_RADIUS, 0, BULB_CY + BULB_RADIUS, false, CycleMethod.NO_CYCLE,
            new Stop(0, fluidColorStart),
            new Stop(1, fluidColorEnd)
        );
        gc.setFill(bulbGrad);
        gc.fillOval(BULB_CX - BULB_RADIUS, BULB_CY - BULB_RADIUS, BULB_RADIUS * 2, BULB_RADIUS * 2);

        // Fluid Column (inside tube)
        LinearGradient columnGrad = new LinearGradient(
            TUBE_LEFT, fluidTopY, TUBE_LEFT + TUBE_WIDTH, fluidTopY, false, CycleMethod.NO_CYCLE,
            new Stop(0, fluidColorStart),
            new Stop(1, fluidColorEnd)
        );
        gc.setFill(columnGrad);
        gc.fillRoundRect(TUBE_LEFT + 3.0, fluidTopY, TUBE_WIDTH - 6.0, TUBE_BOTTOM - fluidTopY + 5.0, 6.0, 6.0);

        // Inner glowing highlights for 3D effect
        gc.setFill(Color.color(1.0, 1.0, 1.0, 0.35));
        gc.fillOval(BULB_CX - 6.0, BULB_CY - 12.0, 6.0, 6.0); // Glossy spot on bulb

        // --- 4. Draw Glass Outlines (Tube and Bulb border) ---
        gc.setStroke(Color.color(1.0, 1.0, 1.0, 0.25));
        gc.setLineWidth(2.0);
        
        // Draw the tube border
        gc.strokeRoundRect(TUBE_LEFT, TUBE_TOP - 5.0, TUBE_WIDTH, TUBE_HEIGHT + 20.0, TUBE_WIDTH, TUBE_WIDTH);
        
        // Draw the bulb border
        gc.strokeOval(BULB_CX - BULB_RADIUS, BULB_CY - BULB_RADIUS, BULB_RADIUS * 2, BULB_RADIUS * 2);
    }
}