/**
 * Core utility and service class for handling temperature conversion arithmetic,
 * validation, and mathematical explanations.
 * 
 * This class operates in a modular fashion, providing distinct methods for individual
 * unit conversions, input validation, and assertion checks.
 * 
 * @author Group 3
 * @version 1.0
 */
public final class TemperatureConverter {

    // Suppress default constructor for non-instantiability
    private TemperatureConverter() {
        throw new AssertionError("Suppress utility class instantiation");
    }

    /**
     * Converts a temperature value from a source unit to a target unit.
     * 
     * <p><b>Preconditions:</b></p>
     * <ul>
     *   <li>The source and target units must not be null.</li>
     *   <li>The input value must be greater than or equal to Absolute Zero in the source unit.</li>
     * </ul>
     * 
     * <p><b>Postconditions:</b></p>
     * <ul>
     *   <li>The returned value is greater than or equal to Absolute Zero in the target unit.</li>
     * </ul>
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
