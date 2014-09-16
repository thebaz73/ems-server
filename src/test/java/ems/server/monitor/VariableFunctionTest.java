package ems.server.monitor;

import ems.driver.domain.Driver;
import ems.driver.domain.common.Status;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.junit.Assert.assertEquals;

public class VariableFunctionTest {
    protected final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    protected ScriptEngine scriptEngine;
    private static final String OK_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;\n" +
            "}";
    private static final String ERROR_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn true;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;\n" +
            "}";
    private static final String WARN_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn true;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;\n" +
            "}";
    private static final String SUM_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function(value) {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value + Number(driver['property']);\n" +
            "}";

    @org.junit.Before
    public void setUp() throws Exception {
        scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    }

    @org.junit.Test
    public void testOkAndStringIdentity() throws Exception {
        //System.out.println("OK_TEMPLATE_FUNCTION = " + OK_TEMPLATE_FUNCTION);
        scriptEngine.eval(OK_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError(obj) ? Status.ERROR : function.isWarn(obj) ? Status.WARN : Status.OK;
        assertEquals(Status.OK, status);
        String value = function.convert("value", null);
        assertEquals("value", value);
    }

    @org.junit.Test
    public void testErrorAndIntegerIdentity() throws Exception {
        //System.out.println("ERROR_TEMPLATE_FUNCTION = " + ERROR_TEMPLATE_FUNCTION);
        scriptEngine.eval(ERROR_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError(obj) ? Status.ERROR : function.isWarn(obj) ? Status.WARN : Status.OK;
        assertEquals(Status.ERROR, status);
        Number value = function.convert((Number)1, null);
        assertEquals(1, value.intValue());
    }

    @org.junit.Test
    public void testWarnAndDoubleIdentity() throws Exception {
        //System.out.println("WARN_TEMPLATE_FUNCTION = " + WARN_TEMPLATE_FUNCTION);
        scriptEngine.eval(WARN_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError(obj) ? Status.ERROR : function.isWarn(obj) ? Status.WARN : Status.OK;
        assertEquals(Status.WARN, status);
        Number value = function.convert((Number)1.1, null);
        assertEquals(1.1, value.doubleValue(), 0.0);
    }

    @org.junit.Test
    public void testOkAndSum() throws Exception {
        //System.out.println("SUM_TEMPLATE_FUNCTION = " + SUM_TEMPLATE_FUNCTION);
        scriptEngine.eval(SUM_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError(obj) ? Status.ERROR : function.isWarn(obj) ? Status.WARN : Status.OK;
        assertEquals(Status.OK, status);
        SampleDriver d = new SampleDriver();
        d.setProperty(1);
        Number value = function.convert((Number)1.0, d);
        assertEquals(2.0, value.doubleValue(), 0.0);
    }

    private class SampleDriver implements Driver {
        private Integer property;

        public Integer getProperty() {
            return property;
        }

        public void setProperty(Integer property) {
            this.property = property;
        }
    }
}