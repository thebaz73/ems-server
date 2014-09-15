package ems.server.monitor;

import ems.driver.domain.Driver;
import ems.driver.domain.common.Status;
import org.apache.commons.beanutils.BeanUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.util.Map;

import static org.junit.Assert.*;

public class VariableFunctionTest {
    protected final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    protected ScriptEngine scriptEngine;
    private static final String OK_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function() {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function() {\n" +
            "\treturn false;" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;" +
            "}";
    private static final String ERROR_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function() {\n" +
            "\treturn true;\n" +
            "}\n" +
            "obj.isWarn = function() {\n" +
            "\treturn false;" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;" +
            "}";
    private static final String WARN_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function() {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function() {\n" +
            "\treturn true;" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\treturn value;" +
            "}";
    private static final String SUM_TEMPLATE_FUNCTION = "var obj = new Object();\n" +
            "obj.isError = function() {\n" +
            "\treturn false;\n" +
            "}\n" +
            "obj.isWarn = function() {\n" +
            "\treturn false;" +
            "}\n" +
            "obj.convert = function(value, driver) {\n" +
            "\tprint('Driver: ' + driver + ', value: ' + ctx.value);" +
            "\treturn driver ? (value + ctx[value]) : 0;" +
            "}";

    @org.junit.Before
    public void setUp() throws Exception {
        scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    }

    @org.junit.Test
    public void testOkAndStringIdentity() throws Exception {
        scriptEngine.eval(OK_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError() ? Status.ERROR : function.isWarn() ? Status.WARN : Status.OK;
        assertEquals(Status.OK, status);
        Object value = function.convert("value", null);
        assertEquals("value", value);
    }

    @org.junit.Test
    public void testErrorAndIntegerIdentity() throws Exception {
        scriptEngine.eval(ERROR_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError() ? Status.ERROR : function.isWarn() ? Status.WARN : Status.OK;
        assertEquals(Status.ERROR, status);
        Object value = function.convert(1, null);
        assertEquals(1, value);
    }

    @org.junit.Test
    public void testWarnAndDoubleIdentity() throws Exception {
        scriptEngine.eval(WARN_TEMPLATE_FUNCTION);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError() ? Status.ERROR : function.isWarn() ? Status.WARN : Status.OK;
        assertEquals(Status.WARN, status);
        Object value = function.convert(1.1, null);
        assertEquals(1.1, value);
    }

    @org.junit.Test
    public void testOkAndSum() throws Exception {
        SampleDriver d = new SampleDriver();
        d.setValue(1);
        Map<String, String> ctx = BeanUtils.describe(d);
        scriptEngine.eval(SUM_TEMPLATE_FUNCTION);
        scriptEngine.put("ctx", ctx);
        Invocable invocable = (Invocable) scriptEngine;
        Object obj = scriptEngine.get("obj");
        VariableFunction function = invocable.getInterface(obj, VariableFunction.class);
        Status status = function.isError() ? Status.ERROR : function.isWarn() ? Status.WARN : Status.OK;
        assertEquals(Status.OK, status);
        Object value = function.convert(1, d);
        assertEquals(2, value);
    }

    private class SampleDriver implements Driver {
        public Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}