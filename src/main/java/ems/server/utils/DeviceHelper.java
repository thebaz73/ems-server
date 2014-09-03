package ems.server.utils;


import ems.driver.domain.Driver;
import ems.driver.domain.common.Location;
import ems.driver.domain.common.Status;
import ems.protocol.domain.Protocol;
import ems.server.domain.Device;
import ems.server.domain.Specification;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import static java.lang.String.format;

/**
 * DeviceHelper
 * Created by thebaz on 9/2/14.
 */
@Component
public class DeviceHelper {
    private final Log logger = LogFactory.getLog(DeviceHelper.class);
    private static DeviceHelper instance;

    @Value("classpath:/extension.properties")
    private Resource resource;

    private DeviceHelper() {
        instance = this;
    }

    public static DeviceHelper getInstance() {
        return instance;
    }

    /**
     * Creates a new Device
     *
     * @param name currentDevice name
     * @param specification currentDevice specification
     * @param status initial currentDevice status
     * @return created currentDevice
     */
    public Device createDevice(String name, Specification specification, Status status) {
        Device device = null;
        String driverClassName = null;
        String protocolClassName = null;
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
            driverClassName = (String) properties.get(format("extension.driver.%s.className", specification.getDriverType()));
            protocolClassName = (String) properties.get(format("extension.protocol.%s.className", specification.getProtocolType()));
        } catch (IOException e) {
            logger.error("Cannot load properties", e);
        }
        if(driverClassName != null && protocolClassName != null) {
            device = new Device();
            device.setName(name);
            device.setSpecification(specification);
            try {
                Class<Driver> driverClass = (Class<Driver>) ClassUtils.forName(driverClassName, DeviceHelper.class.getClassLoader());
                Driver driver = driverClass.newInstance();
                BeanUtils.setProperty(driver, "status", status);
                device.setDriver(driver);
            } catch (ClassNotFoundException e) {
                logger.error(format("Cannot load class: %s class not found", driverClassName), e);
            } catch (InstantiationException e) {
                logger.error(format("Cannot load class: %s cannot instantiate", driverClassName), e);
            } catch (IllegalAccessException e) {
                logger.error(format("Cannot load class: %s illegal method access", driverClassName), e);
            } catch (InvocationTargetException e) {
                logger.error(format("Cannot use class: %s invocation target", driverClassName), e);
            }
            try {
                Class<Protocol> protocolClass = (Class<Protocol>) ClassUtils.forName(protocolClassName, DeviceHelper.class.getClassLoader());
                Protocol protocol = protocolClass.newInstance();
                device.setProtocol(protocol);
            } catch (ClassNotFoundException e) {
                logger.error(format("Cannot load class: %s class not found", protocolClassName), e);
            } catch (InstantiationException e) {
                logger.error(format("Cannot load class: %s cannot instantiate", protocolClassName), e);
            } catch (IllegalAccessException e) {
                logger.error(format("Cannot load class: %s illegal method access", protocolClassName), e);
            }

        }

        return device;
    }

    /**
     * Create a new Device
     *
     * @param name currentDevice name
     * @param specification currentDevice specification
     * @param status initial currentDevice status
     * @param location currentDevice WGS84 location
     * @return created currentDevice
     */
    public Device createDevice(String name, Specification specification, Status status, Location location) {
        Device device = null;
        String driverClassName = null;
        String protocolClassName = null;
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
            driverClassName = (String) properties.get(format("extension.driver.%s.className", specification.getDriverType()));
            protocolClassName = (String) properties.get(format("extension.protocol.%s.className", specification.getProtocolType()));
        } catch (IOException e) {
            logger.error("Cannot load properties", e);
        }
        if(driverClassName != null && protocolClassName != null) {
            device = new Device();
            device.setName(name);
            device.setSpecification(specification);
            try {
                Class<Driver> driverClass = (Class<Driver>) ClassUtils.forName(driverClassName, DeviceHelper.class.getClassLoader());
                Driver driver = driverClass.newInstance();
                BeanUtils.setProperty(driver, "status", status);
                BeanUtils.setProperty(driver, "location", location);
                device.setDriver(driver);
            } catch (ClassNotFoundException e) {
                logger.error(format("Cannot load class: %s class not found", driverClassName), e);
            } catch (InstantiationException e) {
                logger.error(format("Cannot load class: %s cannot instantiate", driverClassName), e);
            } catch (IllegalAccessException e) {
                logger.error(format("Cannot load class: %s illegal method access", driverClassName), e);
            } catch (InvocationTargetException e) {
                logger.error(format("Cannot use class: %s invocation target", driverClassName), e);
            }
            try {
                Class<Protocol> protocolClass = (Class<Protocol>) ClassUtils.forName(protocolClassName, DeviceHelper.class.getClassLoader());
                Protocol protocol = protocolClass.newInstance();
                device.setProtocol(protocol);
            } catch (ClassNotFoundException e) {
                logger.error(format("Cannot load class: %s class not found", protocolClassName), e);
            } catch (InstantiationException e) {
                logger.error(format("Cannot load class: %s cannot instantiate", protocolClassName), e);
            } catch (IllegalAccessException e) {
                logger.error(format("Cannot load class: %s illegal method access", protocolClassName), e);
            }

        }

        return device;
    }
}
