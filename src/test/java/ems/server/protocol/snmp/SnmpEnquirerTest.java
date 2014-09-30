package ems.server.protocol.snmp;


import ems.protocol.domain.ProtocolType;
import ems.protocol.domain.snmp.SnmpProtocol;
import ems.server.test.DeviceAwareTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import static org.junit.Assert.assertEquals;

/**
 * SnmpEnquirerTest
 * Created by thebaz on 9/30/14.
 */
public class SnmpEnquirerTest extends DeviceAwareTest {
    public static final String NAME_OID = "1.3.6.1.4.1.21345.1";
    public static final String CACHE_OID = "1.3.6.1.4.1.21346.1";
    private TestEnquirerAgent agent;

    @Before
    public void setUp() throws Exception {
        agent = new TestEnquirerAgent("udp:0.0.0.0/9999");
        agent.start();
        agent.unregisterManagedObjects();
        agent.registerManagedObject(new MOScalar(new OID(NAME_OID), MOAccessImpl.ACCESS_READ_ONLY, new OctetString("Marco")));
        agent.registerManagedObject(new MOScalar(new OID(CACHE_OID), MOAccessImpl.ACCESS_READ_ONLY, new Integer32(200)));
    }

    @After
    public void tearDown() throws Exception {
        agent.stop();
    }

    @Test
    public void testReadValue() throws Exception {
        createDataModel("schema/snmp.json", ProtocolType.fromValue("snmp"));

        device.setAddress("127.0.0.1");
        device.setPort(9999);
        name.setValue(NAME_OID);
        cacheSize.setValue(CACHE_OID);

        SnmpProtocol protocol = new SnmpProtocol();
        protocol.setVersion(SnmpProtocol.Version.VERSION_2_C);
        protocol.setReadCommunity("public");
        device.setProtocol(protocol);

        SnmpEnquirer enquirer = new SnmpEnquirer();
        enquirer.loadConfiguration(device, 3, 1000);

        enquirer.readValue(name);
        assertEquals(driver.getName(), "Marco");

        enquirer.readValue(cacheSize);
        assertEquals(driver.getCacheSize(), 200);
    }
}
