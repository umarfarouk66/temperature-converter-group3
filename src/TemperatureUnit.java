/**
 * Enumeration representing the supported temperature units.
 * 
 * Each unit maintains its descriptive name, unit symbol, and physical lower limit (Absolute Zero).
 * 
 * @author Group 3
 * @version 1.0
 */
public enum TemperatureUnit {
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
