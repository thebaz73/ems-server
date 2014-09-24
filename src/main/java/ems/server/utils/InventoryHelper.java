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
import javax.validation.constraints.Size;
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
    private static final String TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;\n" +
            "}";
    private static final String BOOLEAN_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn value;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn value;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;\n" +
            "}";
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

    /**
     * Reads driver class name
     *
     * @param driverType driver type
     * @return class name
     */
    public String getDriverClassName(DriverType driverType) {
        return (String) properties.get(format("extension.driver.%s.className", driverType));
    }

    /**
     * Reads protocol class name
     *
     * @param protocolType protocol type
     * @return class name
     */
    public String getProtocolClassName(ProtocolType protocolType) {
        return (String) properties.get(format("extension.protocol.%s.className", protocolType));
    }

    /**
     * Reads driver JSON schema
     *
     * @param driverType driver type
     * @return JSON schema
     */
    public String getDriverJsonSchema(DriverType driverType) {
        return (String) properties.get(format("extension.driver.%s.json", driverType));
    }

    /**
     * Reads protocol JSON schema
     *
     * @param protocolType protocol type
     * @return JSON schema
     */
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
        return createDevice(name, specification, status, null);
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
                Driver driver = createDriver(driverClassName, specification, status, location);
                device.setDriver(driver);
            } catch (ClassNotFoundException e) {
                logger.error(format("Cannot load class: %s class not found", driverClassName), e);
            } catch (InstantiationException e) {
                logger.error(format("Cannot load class: %s cannot instantiate", driverClassName), e);
            } catch (IllegalAccessException e) {
                logger.error(format("Cannot load class: %s illegal method access", driverClassName), e);
            } catch (InvocationTargetException e) {
                logger.error(format("Cannot use class: %s invocation target", driverClassName), e);
            } catch (NoSuchMethodException e) {
                logger.error(format("Cannot allocate class: %s no such method", driverClassName), e);
            } catch (NoSuchFieldException e) {
                logger.error(format("Cannot allocate class: %s no such field", driverClassName), e);
            }

            try {
                Protocol protocol = createProtocol(protocolClassName);
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
     * Creates a full driver
     *
     * @param driverClassName driver class name
     * @param specification specification
     * @param status status
     * @param location location
     * @return created driver
     * @throws ClassNotFoundException if error
     * @throws IllegalAccessException if error
     * @throws InstantiationException if error
     * @throws InvocationTargetException if error
     * @throws NoSuchMethodException if error
     * @throws NoSuchFieldException if error
     */
    public Driver createDriver(String driverClassName, Specification specification, Status status, Location location) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        Driver driver = loadDriverClass(driverClassName);
        allocate(driver);
        BeanUtils.setProperty(driver, "status", status);
        BeanUtils.setProperty(driver, "type", specification.getDriverType());
        if(location != null) {
            BeanUtils.setProperty(driver, "location", location);
        }

        return driver;
    }

    /**
     * creates a protocol class
     *
     * @param protocolClassName protocol class name
     * @return created protocol
     * @throws ClassNotFoundException if error
     * @throws InstantiationException if error
     * @throws IllegalAccessException if error
     */
    public Protocol createProtocol(String protocolClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return loadProtocolClass(protocolClassName);
    }

    /**
     * Retrieve driver configurations list given driver type
     *
     * @param driverType driver type
     * @return driver configuration list
     */
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

    public List<String> getDriverPropertyNames(DriverType driverType) {
        List<String> propertyNames = new ArrayList<String>();
        String driverClassName = getDriverClassName(driverType);
        try {
            Driver driver = loadDriverClass(getDriverClassName(driverType));
            fillPropertyNameList(propertyNames, driver, "");
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

        return propertyNames;
    }

    private void allocate(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException {
        Map<String,String> properties = BeanUtils.describe(object);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if(!entry.getKey().equalsIgnoreCase("status") &&
                    !entry.getKey().equalsIgnoreCase("type") &&
                    !entry.getKey().equalsIgnoreCase("location") &&
                    !entry.getKey().equalsIgnoreCase("class")) {
                Class<?> propertyType = PropertyUtils.getPropertyType(object, entry.getKey());

                if(propertyType.isEnum() || propertyType.isAssignableFrom(Integer.class) || propertyType.isAssignableFrom(Double.class) || propertyType.isAssignableFrom(Float.class) || propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(String.class)) {
                    continue;
                    //skip no operation
                    //logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isArray()) {
                    //skip
                    logger.warn(format("Skipping Array Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(Map.class)) {
                    //skip
                    logger.warn(format("Skipping Map Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(List.class)) {
                    Field field = object.getClass().getDeclaredField(entry.getKey());
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    Class clazz = (Class) pt.getActualTypeArguments()[0];
                    //logger.debug(format("Entry %s property type: %s and generics: %s", entry.getKey(), propertyType.getSimpleName(), clazz.getSimpleName()));
                    List list = (List) PropertyUtils.getProperty(object, entry.getKey());
                    Size size = field.getAnnotation(Size.class);
                    if(size != null) {
                        for (int i = 0; i < size.min(); i++) {
                            Object e = clazz.newInstance();
                            list.add(e);
                            allocate(e);
                        }
                    }
                }
                else {
                    //manage object
                    //logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                    Object o = PropertyUtils.getProperty(object, entry.getKey());
                    if(o == null) {
                        o = propertyType.newInstance();
                        BeanUtils.setProperty(object, entry.getKey(), o);
                    }
                    allocate(o);
                }
            }
        }
    }

    private void fillPropertyNameList(List<String> propertyNames, Object object, String parentProperty) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException, InstantiationException {
        Map<String,String> properties = BeanUtils.describe(object);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if(!entry.getKey().equalsIgnoreCase("status") &&
                    !entry.getKey().equalsIgnoreCase("type") &&
                    !entry.getKey().equalsIgnoreCase("location") &&
                    !entry.getKey().equalsIgnoreCase("class")) {
                Class<?> propertyType = PropertyUtils.getPropertyType(object, entry.getKey());
                if(propertyType.isEnum() || propertyType.isAssignableFrom(Integer.class) || propertyType.isAssignableFrom(Double.class) || propertyType.isAssignableFrom(Float.class) || propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(String.class)) {
                    propertyNames.add(parentProperty+entry.getKey());
                }
                else if(propertyType.isArray()) {
                    //skip
                    logger.warn(format("Skipping Array Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(Map.class)) {
                    //skip
                    logger.warn(format("Skipping Map Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(List.class)) {
                    Field field = object.getClass().getDeclaredField(entry.getKey());
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    Class clazz = (Class) pt.getActualTypeArguments()[0];
                    //logger.debug(format("Entry %s property type: %s and generics: %s", entry.getKey(), propertyType.getSimpleName(), clazz.getSimpleName()));
                    List list = (List) PropertyUtils.getProperty(object, entry.getKey());
                    Size size = field.getAnnotation(Size.class);
                    if(size != null) {
                        for (int i = 0; i < size.max(); i++) {
                            Object e = clazz.newInstance();
                            list.add(e);
                            fillPropertyNameList(propertyNames, e, format("%s%s.[%s].", parentProperty, entry.getKey(), i));
                        }
                    }
                }
                else {
                    //manage object
                    //logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                    Object o = PropertyUtils.getProperty(object, entry.getKey());
                    if(o == null) {
                        o = propertyType.newInstance();
                    }
                    fillPropertyNameList(propertyNames, o, format("%s%s.", parentProperty, entry.getKey()));
                }
            }
        }
    }

    private void fillDriverConfigurationList(List<DriverConfiguration> driverConfigurations, Object object, String parentProperty) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException {
        Map<String,String> properties = BeanUtils.describe(object);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if(!entry.getKey().equalsIgnoreCase("status") &&
                    !entry.getKey().equalsIgnoreCase("type") &&
                    !entry.getKey().equalsIgnoreCase("location") &&
                    !entry.getKey().equalsIgnoreCase("class")) {
                Class<?> propertyType = PropertyUtils.getPropertyType(object, entry.getKey());
                if(propertyType.isEnum() || propertyType.isAssignableFrom(Integer.class) || propertyType.isAssignableFrom(Double.class) || propertyType.isAssignableFrom(Float.class) || propertyType.isAssignableFrom(String.class)) {
                    DriverConfiguration driverConfiguration = new DriverConfiguration();
                    driverConfiguration.setName(parentProperty + entry.getKey());
                    driverConfiguration.setFunction(TEMPLATE_FUNCTION);
                    driverConfigurations.add(driverConfiguration);
                }
                else if(propertyType.isAssignableFrom(Boolean.class)) {
                    DriverConfiguration driverConfiguration = new DriverConfiguration();
                    driverConfiguration.setName(parentProperty + entry.getKey());
                    driverConfiguration.setFunction(BOOLEAN_TEMPLATE_FUNCTION);
                    driverConfigurations.add(driverConfiguration);
                }
                else if(propertyType.isArray()) {
                    //skip
                    logger.warn(format("Skipping Array Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(Map.class)) {
                    //skip
                    logger.warn(format("Map Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
                }
                else if(propertyType.isAssignableFrom(List.class)) {
                    Field field = object.getClass().getDeclaredField(entry.getKey());
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    Class clazz = (Class) pt.getActualTypeArguments()[0];
                    //logger.debug(format("Entry %s property type: %s and generics: %s", entry.getKey(), propertyType.getSimpleName(), clazz.getSimpleName()));
                    List list = (List) PropertyUtils.getProperty(object, entry.getKey());
                    Size size = field.getAnnotation(Size.class);
                    if(size != null) {
                        for (int i = 0; i < size.max(); i++) {
                            Object e = clazz.newInstance();
                            list.add(e);
                            fillDriverConfigurationList(driverConfigurations, e, format("%s%s.[%s].", parentProperty, entry.getKey(), i));
                        }
                    }
                }
                else {
                    //manage object
                    //logger.debug(format("Entry %s property type: %s", entry.getKey(), propertyType.getSimpleName()));
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
