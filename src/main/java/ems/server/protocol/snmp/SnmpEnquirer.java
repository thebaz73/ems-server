package ems.server.protocol.snmp;


import ems.protocol.domain.ProtocolType;
import ems.protocol.domain.snmp.SnmpProtocol;
import ems.server.domain.DriverConfiguration;
import ems.server.protocol.ProtocolEnquirer;
import ems.server.protocol.ResponseHandler;
import ems.server.utils.GenericException;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.*;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.transport.TLSTM;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * SnmpEnquirer
 * Created by thebaz on 9/15/14.
 */
public class SnmpEnquirer extends ProtocolEnquirer {
    private Snmp snmp;
    private AbstractTarget target;
    private String deviceAddress;
    private SnmpProtocol configuration;

    /**
     * Executed after load to execute custom configurations
     */
    @Override
    protected void postLoadConfiguration() {
        try {
            if(device.getSpecification().getProtocolType().equals(ProtocolType.SNMP)) {
                deviceAddress = "udp:"+device.getAddress()+"/"+device.getPort();
                configuration = (SnmpProtocol) device.getProtocol();
                createSession();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Error creating SNMP Session: " + ex.getMessage());
            }
            if (log.isDebugEnabled()) {
                log.debug("Error creating SNMP Session: ", ex);
            }
        }
    }

    /**
     * Read a variable saved into @DriverConfiguration object
     *
     * @param propertyConfiguration driver configuration object
     *
     * @throws GenericException if configuration fatal error
     */
    @Override
    public void readValue(DriverConfiguration propertyConfiguration) throws GenericException {
        try {
            ResponseEvent response = snmpGet(String.valueOf(propertyConfiguration.getValue()));
            if (response != null) {
                if(log.isDebugEnabled()) {
                    log.debug("Got Snmp Get Response from Agent");
                }
                PDU responsePDU = response.getResponse();

                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();

                    if (errorStatus == PDU.noError) {
                        Vector<? extends VariableBinding> variableBindings = responsePDU.getVariableBindings();
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Snmp Get Response = %s", variableBindings));
                        }
                        VariableBinding variableBinding = variableBindings.get(0);
                        Variable variable = variableBinding.getVariable();
                        Object value = convert(variable, getValueType(variable));
                        setValue(propertyConfiguration, value);
                    } else {
                        notifyResponse(response, propertyConfiguration.getName());
                    }
                } else {
                    if(log.isDebugEnabled()) {
                        log.debug("Error: Response PDU is null");
                    }
                }
            } else {
                notifyResponse(response, propertyConfiguration.getName());
            }
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    private void createSession() throws IOException {
        if (snmp == null) {
            Address address = GenericAddress.parse(deviceAddress);
            AbstractTransportMapping transport;
            if (address instanceof TlsAddress) {
                transport = new TLSTM();
            } else if (address instanceof TcpAddress) {
                transport = new DefaultTcpTransportMapping();
            } else {
                transport = new DefaultUdpTransportMapping();
            }
            // Could save some CPU cycles:
            transport.setAsyncMsgProcessingSupported(false);
            snmp = new Snmp(transport);
            snmp.listen();
            if (getSnmpVersion() == SnmpConstants.version3) {
                snmp.getUSM().addUser(new OctetString("user"), new UsmUser(new OctetString("user"), AuthMD5.ID, new OctetString("user"), AuthMD5.ID, null));
                // create the target
                target = new UserTarget();
                target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
                target.setSecurityName(new OctetString("user"));
            } else {
                // create the target
                target = new CommunityTarget();
                ((CommunityTarget) target).setCommunity(new OctetString(configuration.getReadCommunity()));
            }
            target.setAddress(address);
            target.setVersion(getSnmpVersion());
            target.setRetries(retries);
            target.setTimeout(timeout);
        }
    }

    private int getSnmpVersion() {
        switch (configuration.getVersion()) {
            case VERSION_1:
                return SnmpConstants.version1;
            case VERSION_2:
            case VERSION_2_C:
                return SnmpConstants.version2c;
            default:
                return SnmpConstants.version1;
        }
    }
    private void notifyResponse(ResponseEvent response, String targetOid) {
        if (response == null) {
            for (ResponseHandler responseHandler : responseHandlers) {
                responseHandler.onTimeout(targetOid);
            }
        } else {
            PDU responsePDU = response.getResponse();
            int errorStatus = responsePDU.getErrorStatus();
            int errorIndex = responsePDU.getErrorIndex();
            String errorStatusText = responsePDU.getErrorStatusText();
            switch (errorStatus) {
                case SnmpConstants.SNMP_ERROR_SUCCESS:
                    for (ResponseHandler responseHandler : responseHandlers) {
                        responseHandler.onSuccess(targetOid);
                    }
                    break;
                default:
                    if(log.isDebugEnabled()) {
                        log.debug("Error: Request Failed");
                        log.debug("Error Status = " + errorStatus);
                        log.debug("Error Index = " + errorIndex);
                        log.debug("Error Status Text = " + errorStatusText);
                    }
                    for (ResponseHandler responseHandler : responseHandlers) {
                        responseHandler.onError(targetOid, errorStatus, errorStatusText);
                    }
                    break;
            }
        }
    }

    private Class<?> getValueType(Variable variable) {
        switch (variable.getSyntax()) {
            case SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
                return Exception.class;
            case SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:
                return Exception.class;
            case SMIConstants.EXCEPTION_NO_SUCH_OBJECT:
                return Exception.class;
            //case SMIConstants.SYNTAX_BITS:
            case SMIConstants.SYNTAX_OCTET_STRING:
                return String.class;
            case SMIConstants.SYNTAX_COUNTER32:
                return Integer.class;
            case SMIConstants.SYNTAX_COUNTER64:
                return Long.class;
            //case SMIConstants.SYNTAX_GAUGE32:
            case SMIConstants.SYNTAX_UNSIGNED_INTEGER32:
                return Long.class;
            //case SMIConstants.SYNTAX_INTEGER:
            case SMIConstants.SYNTAX_INTEGER32:
                return Integer.class;
            case SMIConstants.SYNTAX_IPADDRESS:
                return InetAddress.class;
            case SMIConstants.SYNTAX_NULL:
                return String.class;
            case SMIConstants.SYNTAX_OBJECT_IDENTIFIER:
                return String.class;
            case SMIConstants.SYNTAX_OPAQUE:
                return String.class;
            case SMIConstants.SYNTAX_TIMETICKS:
                return Long.class;
            default:
                return Object.class;
        }
    }

    /**
     * Converts variable to right object value
     *
     * @param variable snmp var
     * @param clazz    class type to narrow to
     * @return object value
     * @throws ClassCastException if error
     * @throws java.net.UnknownHostException if error
     */
    @SuppressWarnings("UnnecessaryBoxing")
    private <T> T convert(Variable variable, Class<T> clazz) throws ClassCastException, UnknownHostException {
        switch (variable.getSyntax()) {
            case org.snmp4j.smi.SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
            case org.snmp4j.smi.SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:
            case org.snmp4j.smi.SMIConstants.EXCEPTION_NO_SUCH_OBJECT:
                return clazz.cast(new Exception(variable.toString()));
            //case org.snmp4j.smi.SMIConstants.SYNTAX_BITS:
            case org.snmp4j.smi.SMIConstants.SYNTAX_OCTET_STRING:
                if (variable instanceof BitString) {
                    BitString bitString = (BitString) variable;
                    return clazz.cast(bitString.toString());
                }
                return clazz.cast(variable.toString());
            case org.snmp4j.smi.SMIConstants.SYNTAX_COUNTER32:
                return clazz.cast(new Integer(variable.toInt()));
            case org.snmp4j.smi.SMIConstants.SYNTAX_COUNTER64:
                return clazz.cast(new Long(variable.toLong()));
            //case org.snmp4j.smi.SMIConstants.SYNTAX_GAUGE32:
            case org.snmp4j.smi.SMIConstants.SYNTAX_UNSIGNED_INTEGER32:
                if (variable instanceof Gauge32) {
                    Gauge32 bitString = (Gauge32) variable;
                    return clazz.cast(new Long(bitString.toLong()));
                }
                return clazz.cast(new Long(variable.toLong()));
            //case org.snmp4j.smi.SMIConstants.SYNTAX_INTEGER:
            case org.snmp4j.smi.SMIConstants.SYNTAX_INTEGER32:
                return clazz.cast(new Integer(variable.toInt()));
            case org.snmp4j.smi.SMIConstants.SYNTAX_IPADDRESS:
                IpAddress ipAddress = (IpAddress) variable;
                return clazz.cast(InetAddress.getByAddress(ipAddress.toByteArray()));
            case org.snmp4j.smi.SMIConstants.SYNTAX_NULL:
                return clazz.cast(variable.toString());
            case org.snmp4j.smi.SMIConstants.SYNTAX_OBJECT_IDENTIFIER:
                return clazz.cast(variable.toString());
            case org.snmp4j.smi.SMIConstants.SYNTAX_OPAQUE:
                return clazz.cast(variable.toString());
            case org.snmp4j.smi.SMIConstants.SYNTAX_TIMETICKS:
                return clazz.cast(new Long(variable.toLong()));
            default:
                throw new IllegalStateException();
        }

    }

    private ResponseEvent snmpGet(String targetOid) throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(targetOid)));
        pdu.setType(PDU.GET);
        // send the PDU
        return snmp.send(pdu, target);
    }
}
