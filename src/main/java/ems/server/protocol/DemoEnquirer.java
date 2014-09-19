package ems.server.protocol;

import ems.server.domain.DriverConfiguration;
import ems.server.utils.GenericException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * DemoEnquirer
 * Created by thebaz on 18/09/14.
 */
public class DemoEnquirer extends ProtocolEnquirer {

    @Override
    protected void postLoadConfiguration() {

    }

    @Override
    public void readValue(DriverConfiguration propertyConfiguration) throws GenericException {
        try {
            Class<?> propertyType = PropertyUtils.getPropertyType(device.getDriver(), propertyConfiguration.getName());
            Object value = randomValue(propertyType);
            setValue(propertyConfiguration, value);
        } catch (IllegalAccessException e) {
            throw new GenericException(e);
        } catch (InvocationTargetException e) {
            throw new GenericException(e);
        } catch (NoSuchMethodException e) {
            throw new GenericException(e);
        }
    }

    private Object randomValue(Class<?> propertyType) {
        if(propertyType.isEnum()) {
            Object[] enumConstants = propertyType.getEnumConstants();
            return enumConstants[RandomUtils.nextInt(enumConstants.length)];
        }
        else if(propertyType.isAssignableFrom(Integer.class)) {
            return RandomUtils.nextInt();
        }
        else if(propertyType.isAssignableFrom(Double.class)) {
            return RandomUtils.nextDouble();
        }
        else if(propertyType.isAssignableFrom(Float.class)) {
            return RandomUtils.nextFloat();
        }
        else if(propertyType.isAssignableFrom(Boolean.class)) {
            return RandomUtils.nextBoolean();
        }
        else if(propertyType.isAssignableFrom(String.class)) {
            return "demo";
        }

        return null;
    }
}
