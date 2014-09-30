package ems.server.protocol.snmp;


import ems.server.test.TestEnquirer;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.TransportMappings;

import java.io.File;
import java.io.IOException;


/**
 * TestEnquirerAgent
 * Created by thebaz on 9/30/14.
 */
public class TestEnquirerAgent extends BaseAgent implements TestEnquirer {
    private final String address;
    private int cacheSize;
    private String name;

    public TestEnquirerAgent(String address) {
        super(new File("simplest.boot"), null, new CommandProcessor(new OctetString("simplest")));
        this.address = address;
    }

    @Override
    public int getCacheSize() {
        return cacheSize;
    }

    @Override
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * We let clients of this agent register the MO they
     * need so this method does nothing
     */
    @Override
    protected void registerManagedObjects() {
    }

    /**
     * Clients can register the MO they need
     */
    public void registerManagedObject(ManagedObject mo) {
        try {
            server.register(mo, null);
        } catch (DuplicateRegistrationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /*
     * Empty implementation
     */
    @Override
    protected void addNotificationTargets(SnmpTargetMIB targetMIB,
                                          SnmpNotificationMIB notificationMIB) {
    }

    /**
     * Minimal View based Access Control
     *
     * http://www.faqs.org/rfcs/rfc2575.html
     */
    @Override
    protected void addViews(VacmMIB vacm) {

        vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(
                "cpublic"), new OctetString("v1v2group"),
                StorageType.nonVolatile);

        vacm.addAccess(new OctetString("v1v2group"), new OctetString("public"),
                SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV,
                MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
                new OctetString("fullWriteView"), new OctetString(
                "fullNotifyView"), StorageType.nonVolatile);

        vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"),
                new OctetString(), VacmMIB.vacmViewIncluded,
                StorageType.nonVolatile);
    }

    /**
     * User based Security Model, only applicable to
     * SNMP v.3
     *
     */
    protected void addUsmUser(USM usm) {
    }

    @Override
    protected void initTransportMappings() throws IOException {
        transportMappings = new TransportMapping[1];
        Address parsedAddress = GenericAddress.parse(address);
        TransportMapping tm = TransportMappings.getInstance().createTransportMapping(parsedAddress);
        transportMappings[0] = tm;
    }

    /**
     * Start method invokes some initialization methods needed to
     * start the agent
     * @throws IOException
     */
    public void start() throws IOException {

        init();
        // This method reads some old config from a file and causes
        // unexpected behavior.
        // loadConfig(ImportModes.REPLACE_CREATE);
        addShutdownHook();
        getServer().addContext(new OctetString("public"));
        finishInit();
        run();
        sendColdStartNotification();
    }

    protected void unregisterManagedObjects() {
        MOGroup moGroup = getSnmpv2MIB();
        moGroup.unregisterMOs(server, getContext(moGroup));
    }

    /**
     * The table of community strings configured in the SNMP
     * engine's Local Configuration Datastore (LCD).
     *
     * We only configure one, "public".
     */
    protected void addCommunities(SnmpCommunityMIB communityMIB) {
        Variable[] com2sec = new Variable[]{
                new OctetString("public"), // community name
                new OctetString("cpublic"), // security name
                getAgent().getContextEngineID(), // local engine ID
                new OctetString("public"), // default context name
                new OctetString(), // transport tag
                new Integer32(StorageType.nonVolatile), // storage type
                new Integer32(RowStatus.active) // row status
        };
        MOTableRow row = communityMIB.getSnmpCommunityEntry().createRow(
                new OctetString("public2public").toSubIndex(true), com2sec);
        communityMIB.getSnmpCommunityEntry().addRow((SnmpCommunityMIB.SnmpCommunityEntryRow) row);
    }}
