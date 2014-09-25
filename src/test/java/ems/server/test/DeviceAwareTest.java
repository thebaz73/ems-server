package ems.server.test;

import ems.driver.domain.DriverType;
import ems.protocol.domain.ProtocolType;
import ems.server.domain.Device;
import ems.server.domain.DriverConfiguration;
import ems.server.domain.Specification;

/**
 * DeviceAwareTest
 * Created by thebaz on 25/09/14.
 */
public class DeviceAwareTest {
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
    protected Device device;
    protected TestEnquirerDriver driver;
    protected DriverConfiguration name;
    protected DriverConfiguration cacheSize;

    public void createDataModel(String schema, ProtocolType protocolType) {
        Specification specification = new Specification();
        specification.setName("AcmeTest");
        specification.setDriverType(DriverType.fromValue("modulator"));
        specification.setDriver("schema/modulator.json");
        specification.setProtocolType(protocolType);
        specification.setProtocol(schema);

        device = new Device();
        device.setName("Device001");
        device.setSpecification(specification);
        driver = new TestEnquirerDriver();
        device.setDriver(driver);
        name = new DriverConfiguration();
        name.setName("name");
        name.setFunction(TEMPLATE_FUNCTION);
        cacheSize = new DriverConfiguration();
        cacheSize.setName("cacheSize");
        cacheSize.setFunction(TEMPLATE_FUNCTION);
    }
}
