package ems.server.utils;


import ems.driver.domain.Driver;
import ems.driver.domain.DriverType;
import ems.driver.domain.common.Location;
import ems.driver.domain.common.Status;
import ems.protocol.domain.Protocol;
import ems.protocol.domain.ProtocolType;
import ems.server.domain.Device;
import ems.server.domain.DriverConfiguration;
import ems.server.domain.Specification;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

/**
 * DeviceHelper
 * Created by thebaz on 9/2/14.
 */
@Component
public class InventoryHelper {
    private final Log logger = LogFactory.getLog(InventoryHelper.class);
    private static InventoryHelper instance;

    @Value("classpath:/extension.properties")
    private Resource resource;
    private Properties properties;

    private InventoryHelper() {
        instance = this;
    }

    @PostConstruct
    private void initialize() {
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            logger.error("Cannot load properties", e);
        }
    }

    public static InventoryHelper getInstance() {
        return instance;
    }

    public String getDriverClassName(DriverType driverType) {
        return (String) properties.get(format("extension.driver.%s.className", driverType));
    }

    public String getProtocolClassName(ProtocolType protocolType) {
        return (String) properties.get(format("extension.protocol.%s.className", protocolType));
    }

    public String getDriverJsonSchema(DriverType driverType) {
        return (String) properties.get(format("extension.driver.%s.json", driverType));
    }

    public String getProtocolJsonSchema(ProtocolType protocolType) {
        return (String) properties.get(format("extension.protocol.%s.json", protocolType));
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
        String driverClassName = getDriverClassName(specification.getDriverType());
        String protocolClassName =  getProtocolClassName(specification.getProtocolType());
        if(driverClassName != null && protocolClassName != null) {
            device = new Device();
            device.setName(name);
            device.setSpecification(specification);
            try {
                Driver driver = loadDriverClass(driverClassName);
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
                Protocol protocol = loadProtocolClass(protocolClassName);
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
        String driverClassName = getDriverClassName(specification.getDriverType());
        String protocolClassName =  getProtocolClassName(specification.getProtocolType());
        if(driverClassName != null && protocolClassName != null) {
            device = new Device();
            device.setName(name);
            device.setSpecification(specification);
            try {
                Driver driver = loadDriverClass(driverClassName);
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
                Protocol protocol = loadProtocolClass(protocolClassName);
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

    public List<DriverConfiguration> getDriverConfigurationList(DriverType driverType) {
        List<DriverConfiguration> driverConfigurations = new ArrayList<DriverConfiguration>();
        String driverClassName = getDriverClassName(driverType);
        try {
            Driver driver = loadDriverClass(getDriverClassName(driverType));
            fillDriverConfigurationList(driverConfigurations, driver, "");
        } catch (ClassNotFoundException e) {
            logger.error(format("Cannot load driver configuration list: %s class not found", driverClassName), e);
        } catch (InstantiationException e) {
            logger.error(format("Cannot load driver configuration list: %s cannot instantiate", driverClassName), e);
        } catch (IllegalAccessException e) {
            logger.error(format("Cannot load driver configuration list: %s illegal method access", driverClassName), e);
        } catch (InvocationTargetException e) {
            logger.error(format("Cannot use driver configuration list: %s invocation target", driverClassName), e);
        } catch (NoSuchMethodException e) {
            logger.error(format("Cannot load driver configuration list: %s no such method", driverClassName), e);
        } catch (NoSuchFieldException e) {
            logger.error(format("Cannot load driver configuration list: %s no such field", driverClassName), e);
        }

        return driverConfigurations;
    }

    private void fillDriverConfigurationList(List<DriverConfiguration> driverConfigurations, Object object, String parentProperty) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException {
        Map<String,String> properties = BeanUtils.describe(object);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if(!entry.getKey().equalsIgnoreCase("status") &&
                    !entry.getKey().equalsIgnoreCase("type") &&
                    !entry.getKey().equalsIgnoreCase("location") &&
                    !entry.getKey().equalsIgnoreCase("class")) {
                Class<?> propertyType = PropertyUtils.getPropertyType(object, entry.getKey());
                if(propertyType.isEnum() || propertyType.isAssignableFrom(Integer.class) || propertyType.isAssignableFrom(Double.class) || propertyType.isAssignableFrom(Float.class) || propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(String.class)) {
                    DriverConfiguration driverConfiguration = new DriverConfiguration();
                    driverConfiguration.setName(parentProperty+entry.getKey());
                    driverConfigurations.add(driverConfiguration);
                }
                else if(propertyType.isArray()) {
                    //skip
                    logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(Map.class)) {
                    //skip
                    logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(List.class)) {
                    Field field = object.getClass().getDeclaredField(entry.getKey());
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    Class clazz = (Class) pt.getActualTypeArguments()[0];
                    logger.debug(format("Entry %s property type: %s and generics: %s", entry.getKey(), propertyType.getSimpleName(), clazz.getSimpleName()));
                    List list = (List) PropertyUtils.getProperty(object, entry.getKey());
                    Object e = clazz.newInstance();
                    list.add(e);
                    fillDriverConfigurationList(driverConfigurations, e, format("%s%s[0].", parentProperty, entry.getKey()));
                }
                else {
                    //manage object
                    logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                    Object o = PropertyUtils.getProperty(object, entry.getKey());
                    if(o == null) {
                        o = propertyType.newInstance();
                    }
                    fillDriverConfigurationList(driverConfigurations, o, format("%s%s.", parentProperty, entry.getKey()));
                }
            }
        }
    }

    private Driver loadDriverClass(String driverClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<Driver> driverClass = (Class<Driver>) ClassUtils.forName(driverClassName, InventoryHelper.class.getClassLoader());
        return driverClass.newInstance();
    }

    private Protocol loadProtocolClass(String driverClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<Protocol> driverClass = (Class<Protocol>) ClassUtils.forName(driverClassName, InventoryHelper.class.getClassLoader());
        return driverClass.newInstance();
    }
}
