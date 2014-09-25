package ems.server.protocol.jmx;

import ems.protocol.domain.ProtocolType;
import ems.protocol.domain.jmx.JmxProtocol;
import ems.server.test.DeviceAwareTest;
import ems.server.test.TestEnquirer;
import ems.server.test.TestEnquirerMBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertEquals;

public class JmxEnquirerTest extends DeviceAwareTest {
    public static final String OBJECT_NAME = "ems.server.protocol.jmx:type=TestEnquirer";
    private MBeanServer mBeanServer;
    private ObjectName mBeanName;

    @Before
    public void setUp() throws Exception {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanName = new ObjectName(OBJECT_NAME);
        TestEnquirerMBean mBean = new TestEnquirer();
        mBean.setCacheSize(200);
        mBean.setName("Marco");
        mBeanServer.registerMBean(mBean, mBeanName);
    }

    @After
    public void tearDown() throws Exception {
        mBeanServer.unregisterMBean(mBeanName);
    }

    @Test
    public void testReadValue() throws Exception {
        createDataModel("schema/jmx.json", ProtocolType.fromValue("jmx"));

        device.setAddress("127.0.0.1");
        device.setPort(9999);
        name.setValue("Name");
        cacheSize.setValue("CacheSize");

        JmxProtocol protocol = new JmxProtocol();
        protocol.setBeanName(OBJECT_NAME);
        device.setProtocol(protocol);

        JmxEnquirer enquirer = new JmxEnquirer();
        enquirer.loadConfiguration(device, 3, 1000);

        enquirer.readValue(name);
        assertEquals(driver.getName(), "Marco");

        enquirer.readValue(cacheSize);
        assertEquals(driver.getCacheSize(), 200);
    }

}