package ems.server.protocol;


/**
 * ResponseHandler
 * Created by thebaz on 9/15/14.
 */
public interface ResponseHandler {
    /**
     * Callback method called when a snmp command on the specified
     * variable ends with a timeout
     *
     * @param variable variable
     */
    public void onTimeout(String variable);

    /**
     * Callback method called when a snmp command on the specified
     * variable ends with a success
     *
     * @param variable variable
     */
    public void onSuccess(String variable);

    /**
     * Callback method called when a snmp command on the specified
     * variable ends with an error
     *
     * @param variable              variable
     * @param errorCode        error code
     * @param errorDescription error description
     */
    public void onError(String variable, int errorCode, String errorDescription);
}
