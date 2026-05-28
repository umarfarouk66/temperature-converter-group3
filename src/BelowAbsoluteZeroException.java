/**
 * Custom Exception thrown when a temperature value falls below the physical limit of Absolute Zero.
 * 
 * In physics, Absolute Zero is the lowest possible temperature where all classic thermodynamic 
 * motion ceases. Attempting to process temperatures below this limit is invalid.
 * 
 * @author Group 3
 * @version 1.0
 */
public class BelowAbsoluteZeroException extends IllegalArgumentException {
    
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
