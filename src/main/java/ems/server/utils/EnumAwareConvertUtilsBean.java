package ems.server.utils;


import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * EnumAwareConvertUtilsBean
 * Created by thebaz on 9/3/14.
 */
public class EnumAwareConvertUtilsBean  extends ConvertUtilsBean {
    private final EnumConverter enumConverter = new EnumConverter();

    public Converter lookup(Class clazz) {
        final Converter converter = super.lookup(clazz);
        // no specific converter for this class, so it's neither a String, (which has a default converter),
        // nor any known object that has a custom converter for it. It might be an enum !
        if (converter == null && clazz.isEnum()) {
            return enumConverter;
        } else {
            return converter;
        }
    }

    private class EnumConverter implements Converter {
        public Object convert(Class type, Object value) {
            if(value.getClass().isEnum()) return value;
            try {
                int index = Integer.parseInt((String) value);
                return type.getEnumConstants()[index];
            } catch (NumberFormatException e) {
                //noop
            }
            try {
                Method method = type.getDeclaredMethod("fromName", String.class);
                if(method != null) {
                    return method.invoke(type.getEnumConstants()[0], value);
                }
            } catch (NoSuchMethodException e) {
                //noop
            } catch (InvocationTargetException e) {
                //noop
            } catch (IllegalAccessException e) {
                //noop
            }

            return null;
        }
    }
}
