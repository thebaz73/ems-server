package ems.server.protocol.jmx;


import ems.protocol.domain.ProtocolType;
import ems.protocol.domain.jmx.JmxProtocol;
import ems.server.domain.DriverConfiguration;
import ems.server.protocol.ProtocolEnquirer;
import ems.server.utils.GenericException;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * JmxEnquirer
 * Created by thebaz on 9/15/14.
 */
public class JmxEnquirer extends ProtocolEnquirer {
    private MBeanServerConnection mBeanServerConnection;
    private ObjectName beanName;
    private String deviceAddress;
    private JmxProtocol configuration;

    /**
     * Executed after load to execute custom configurations
     */
    @Override
    protected void postLoadConfiguration() {
        try {
            if(device.getSpecification().getProtocolType().equals(ProtocolType.SNMP)) {
                deviceAddress = "service:jmx:rmi:///jndi/rmi://"+device.getAddress()+":"+device.getPort()+"/jmxrmi";
                configuration = (JmxProtocol) device.getProtocol();
                createSession();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Error creating JMX Session: " + ex.getMessage());
            }
            if (log.isDebugEnabled()) {
                log.debug("Error creating JMX Session: ", ex);
            }
        }
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
        try {
            Object value = mBeanServerConnection.getAttribute(beanName, String.valueOf(propertyConfiguration.getValue()));
            setValue(propertyConfiguration, value);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    private void createSession() throws IOException, MalformedObjectNameException {
        JMXServiceURL url = new JMXServiceURL(deviceAddress);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null);
        mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        beanName = new ObjectName(configuration.getBeanName());
    }
}
