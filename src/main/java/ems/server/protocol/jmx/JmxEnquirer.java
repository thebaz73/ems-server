package ems.server.protocol.jmx;


import ems.server.domain.DriverConfiguration;
import ems.server.protocol.ProtocolEnquirer;
import ems.server.utils.GenericException;

/**
 * JmxEnquirer
 * Created by thebaz on 9/15/14.
 */
public class JmxEnquirer extends ProtocolEnquirer {
    /**
     * Executed after load to execute custom configurations
     */
    @Override
    protected void postLoadConfiguration() {
    }

    /**
     * Read a variable saved into @DriverConfiguration object
     *
     * @param propertyConfiguration driver configuration object
     * @throws ems.server.utils.GenericException
     *          if configuration fatal error
     */
    @Override
    public void readValue(DriverConfiguration propertyConfiguration) throws GenericException {
    }
}
