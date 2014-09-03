package ems.server.utils;


import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

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
            return type.getEnumConstants()[Integer.parseInt((String) value)];
        }
    }
}
