package ems.server.monitor;

import ems.driver.domain.Driver;

/**
 * ScriptingFunction
 * Created by thebaz on 15/09/14.
 */
public interface VariableFunction {
    /**
     * Check if it is in error
     *
     * @return true if is in error
     */
    public boolean isError(Object value);

    /**
     * Check if it is in warning
     *
     * @return true if is in warning
     */
    public boolean isWarn(Object value);

    /**
     * Convert value
     *
     * @param value to be converted
     * @param driver driver
     * @return converted value
     */
    public Object convert(Object value, Driver driver);
}
