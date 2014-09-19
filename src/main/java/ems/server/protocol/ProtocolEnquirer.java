package ems.server.protocol;


import ems.driver.domain.common.Status;
import ems.server.domain.Device;
import ems.server.domain.DriverConfiguration;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;
import ems.server.monitor.VariableFunction;
import ems.server.utils.EnumAwareConvertUtilsBean;
import ems.server.utils.EventHelper;
import ems.server.utils.GenericException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ProtocolEnquirer
 * Created by thebaz on 9/15/14.
 */
public abstract class ProtocolEnquirer {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    protected final Log log = LogFactory.getLog(getClass());
    protected String sessionId;
    protected Device device;
    protected int retries;
    protected long timeout;
    protected final List<ResponseHandler> responseHandlers = new ArrayList<ResponseHandler>();
    protected final BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new EnumAwareConvertUtilsBean());
    protected final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    protected final ScriptEngine scriptEngine;

    public ProtocolEnquirer() {
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    }

    /**
     * Loads protocol configuration for device
     *
     * @param device device
     * @param retries retries times
     * @param timeout timeout millis
     */
    public void loadConfiguration(Device device, int retries, long timeout) {
        sessionId = UUID.randomUUID().toString();
        this.device = device;
        this.retries = retries;
        this.timeout = timeout;
        postLoadConfiguration();
    }

    /**
     * Executed after load to execute custom configurations
     */
    protected abstract void postLoadConfiguration();

    /**
     * Read a variable saved into @DriverConfiguration object
     *
     * @param propertyConfiguration driver configuration object
     *
     * @throws ems.server.utils.GenericException if configuration fatal error
     */
    public abstract void readValue(DriverConfiguration propertyConfiguration) throws GenericException;
//
//    /**
//     * Return response handlers
//     *
//     * @return response handlers
//     */
//    public List<ResponseHandler> getResponseHandlers() {
//        return responseHandlers;
//    }

    /**
     * Adds a @ResponseHandler response handler
     *
     * @param responseHandler a response handler
     */
    public void addResponseHandler(ResponseHandler responseHandler) {
        synchronized (responseHandlers) {
            responseHandlers.add(responseHandler);
        }
    }
//
//    /**
//     * Remove a@ResponseHandler response handler
//     *
//     * @param responseHandler a response handler
//     */
//    public void removeResponseHandler(ResponseHandler responseHandler) {
//        synchronized (responseHandlers) {
//            responseHandlers.remove(responseHandler);
//        }
//    }
//
//    /**
//     * Returns session id
//     * @return session id
//     */
//    public String getSessionId() {
//        return sessionId;
//    }

    /**
     * Sets read value to the @Driver object of the device if a valid JavaScript function is provided
     * the script engine is executed
     * @param propertyConfiguration driver configuration object
     * @param value value to be set
     * @throws NoSuchMethodException if no such method error
     * @throws IllegalAccessException if illegal method access
     * @throws InvocationTargetException if invocation target error
     */
    protected final void setValue(DriverConfiguration propertyConfiguration, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        //Invoking function
        if(!propertyConfiguration.getFunction().isEmpty() && !propertyConfiguration.getFunction().equals("\n\r")) {
            try {
                scriptEngine.eval(propertyConfiguration.getFunction());
                Invocable invocable = (Invocable) scriptEngine;
                Object obj = scriptEngine.get("obj");
                VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
                Status status = function.isError(obj) ? Status.ERROR : function.isWarn(obj) ? Status.WARN : Status.OK;

                try {
                    beanUtilsBean.setProperty(device.getDriver(), "status", status);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                value = function.convert(value, device.getDriver());
            } catch (ScriptException e) {
                EventType eventType = EventType.EVENT_CONFIGURATION;
                EventSeverity eventSeverity = EventSeverity.EVENT_FATAL;
                String description = "Event of type: \'" + eventType + "\' at: " +
                        format.format(new Date(System.currentTimeMillis())) + " with severity: \'" +
                        eventSeverity + "\' for device: " + device.getName() + ". Error executing script: " + e.getMessage();
                EventHelper.getInstance().addEvent(device, eventType, eventSeverity, description);
            }
        }
        beanUtilsBean.setProperty(device.getDriver(), propertyConfiguration.getName(), value);
    }
}
