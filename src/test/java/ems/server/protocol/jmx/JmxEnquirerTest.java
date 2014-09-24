package ems.server.protocol.jmx;

import ems.driver.domain.Driver;
import ems.driver.domain.DriverType;
import ems.protocol.domain.ProtocolType;
import ems.protocol.domain.jmx.JmxProtocol;
import ems.server.domain.Device;
import ems.server.domain.DriverConfiguration;
import ems.server.domain.Specification;
import ems.server.utils.GenericException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertEquals;

public class JmxEnquirerTest {
    private static final String TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;\n" +
            "}";
    public static final String OBJECT_NAME = "ems.server.protocol.jmx:type=TestEnquirer";
    private MBeanServer mBeanServer;
    private ObjectName mBeanName;

    @Before
    public void setUp() throws Exception {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanName = new ObjectName(OBJECT_NAME);
        TestEnquirer mBean = new TestEnquirer();
        mBean.setCacheSize(200);
        mBean.setName("Marco");
        mBeanServer.registerMBean(mBean, mBeanName);
    }

    @After
    public void tearDown() throws Exception {
        mBeanServer.unregisterMBean(mBeanName);
    }

    @Test
    public void testReadValue() throws Exception, GenericException {
        Specification specification = new Specification();
        specification.setName("AcmeTest");
        specification.setDriverType(DriverType.fromValue("modulator"));
        specification.setDriver("schema/modulator.json");
        specification.setProtocolType(ProtocolType.fromValue("jmx"));
        specification.setProtocol("schema/jmx.json");

        Device device = new Device();
        device.setName("Device001");
        device.setSpecification(specification);
        device.setAddress("127.0.0.1");
        device.setPort(9999);
        JmxProtocol protocol = new JmxProtocol();
        protocol.setBeanName(OBJECT_NAME);
        device.setProtocol(protocol);
        TestEnquirerDriver driver = new TestEnquirerDriver();
        device.setDriver(driver);

        JmxEnquirer enquirer = new JmxEnquirer();
        enquirer.loadConfiguration(device, 3, 1000);

        DriverConfiguration name = new DriverConfiguration();
        name.setName("name");
        name.setValue("name");
        name.setFunction(TEMPLATE_FUNCTION);
        enquirer.readValue(name);
        assertEquals(driver.getName(), "Marco");

        DriverConfiguration cacheSize = new DriverConfiguration();
        cacheSize.setName("cacheSize");
        cacheSize.setValue("cacheSize");
        cacheSize.setFunction(TEMPLATE_FUNCTION);
        enquirer.readValue(cacheSize);
        assertEquals(driver.getCacheSize(), 200);
    }

    private class TestEnquirerDriver implements Driver {
        private int cacheSize;
        private String name;

        public int getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}