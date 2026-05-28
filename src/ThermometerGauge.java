import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

/**
 * A custom-drawn UI component that renders a stunning, dynamic, 3D glass thermometer.
 * 
 * The thermometer's fluid level and color gradients automatically adjust based on
 * the temperature input, animating dynamically between cool blue, mild teal/green,
 * and warm orange/red.
 * 
 * @author Group 3
 * @version 1.0
 */
public class ThermometerGauge extends Canvas {

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
     * The input is converted to Celsius to standardize the thermometer's
     * visual scaling coordinates.
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
