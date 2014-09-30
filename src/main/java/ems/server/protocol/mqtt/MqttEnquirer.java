package ems.server.protocol.mqtt;


import ems.protocol.domain.ProtocolType;
import ems.protocol.domain.mqtt.MqttProtocol;
import ems.server.domain.DriverConfiguration;
import ems.server.protocol.ProtocolEnquirer;
import ems.server.utils.GenericException;
import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;

/**
 * MqttEnquirer
 * Created by thebaz on 9/30/14.
 */
public class MqttEnquirer extends ProtocolEnquirer {
    private final DriverConfigurationCallback callback = new DriverConfigurationCallback();
    private MqttClient client;
    private String deviceAddress;
    private MqttProtocol configuration;

    /**
     * Executed after load to execute custom configurations
     */
    @Override
    protected void postLoadConfiguration() {
        try {
            if(device.getSpecification().getProtocolType().equals(ProtocolType.JMX)) {
                configuration = (MqttProtocol) device.getProtocol();
                switch (configuration.getConnectionType()) {
                    case TCP:
                        deviceAddress = "tcp://"+device.getAddress()+":"+device.getPort();
                    case SSL:
                        deviceAddress = "ssl://"+device.getAddress()+":"+device.getPort();
                }
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
            callback.addDriverConfiguration(propertyConfiguration);
            client.subscribe(String.valueOf(propertyConfiguration.getValue()));
        } catch (MqttException e) {
            throw new GenericException(e);
        }
    }

    private void createSession() throws MqttException {
        client = new MqttClient(deviceAddress, MqttClient.generateClientId());
        client.connect();
        client.setCallback(callback);
    }

    private class DriverConfigurationCallback implements MqttCallback {
        private Map<String, DriverConfiguration> propertyConfigurationMap = new HashMap<String, DriverConfiguration>();

        public void addDriverConfiguration(DriverConfiguration propertyConfiguration) {
            propertyConfigurationMap.put(String.valueOf(propertyConfiguration.getValue()), propertyConfiguration);
        }

        @Override
        public void connectionLost(Throwable cause) {
            log.error("MQTT Connection lost", cause);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            if(propertyConfigurationMap.containsKey(topic)) {
                client.unsubscribe(topic);
                DriverConfiguration propertyConfiguration = propertyConfigurationMap.remove(topic);
                setValue(propertyConfiguration, new String(message.getPayload()));
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            log.debug("MQTT delivery complete: " + token.toString());
        }
    }
}
