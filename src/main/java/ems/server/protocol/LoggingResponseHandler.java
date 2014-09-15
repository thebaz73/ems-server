package ems.server.protocol;


import ems.server.domain.Device;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LoggingResponseHandler
 * Created by thebaz on 9/15/14.
 */
public class LoggingResponseHandler implements ResponseHandler {
    Log log = LogFactory.getLog(getClass());
    private final Device device;

    public LoggingResponseHandler(Device device) {
        this.device = device;
    }

    @Override
    public void onTimeout(String variable) {
        if (log.isWarnEnabled()) {
            log.warn(String.format("Timeout reading: %s on %s", variable, device.getName()));
        }
    }

    @Override
    public void onSuccess(String variable) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Successful reading: %s on %s", variable, device.getName()));
        }
    }

    @Override
    public void onError(String variable, int errorCode, String errorDescription) {
        if (log.isErrorEnabled()) {
            log.error(String.format("Error reading: %s on %s. [%d - %s]", variable, device.getName(), errorCode, errorDescription));
        }
    }
}
