package ems.server.monitor;

import ems.server.domain.*;
import ems.server.protocol.DemoEnquirer;
import ems.server.protocol.EventAwareResponseHandler;
import ems.server.protocol.LoggingResponseHandler;
import ems.server.protocol.ProtocolEnquirer;
import ems.server.protocol.jmx.JmxEnquirer;
import ems.server.protocol.modbus.ModBusEnquirer;
import ems.server.protocol.snmp.SnmpEnquirer;
import ems.server.utils.EventHelper;
import ems.server.utils.GenericException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.String.format;

/**
 * MonitoringTask
 * Created by thebaz on 17/09/14.
 */
public class MonitoringTask {
    private final List<ScheduledFuture> futures = new ArrayList<ScheduledFuture>();
    private ScheduledExecutorService executor;
    private Device device;
    private List<TaskConfiguration> taskConfigurations;
    private List<DriverConfiguration> driverConfigurations;
    private EmsConfigurationEntry retries;
    private EmsConfigurationEntry timeout;

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setTaskConfigurations(List<TaskConfiguration> taskConfigurations) {
        this.taskConfigurations = taskConfigurations;
    }

    public void setDriverConfigurations(List<DriverConfiguration> driverConfigurations) {
        this.driverConfigurations = driverConfigurations;
    }

    public void setRetries(EmsConfigurationEntry retries) {
        this.retries = retries;
    }

    public void setTimeout(EmsConfigurationEntry timeout) {
        this.timeout = timeout;
    }

    public void start() throws GenericException {
        assert device != null && taskConfigurations != null && driverConfigurations != null;
        executor = Executors.newScheduledThreadPool(taskConfigurations.size());
        ProtocolEnquirer enquirer = createProtocolEnquirer();
        for (TaskConfiguration taskConfiguration : taskConfigurations) {
            ScheduledFuture scheduledFuture;
            DriverConfiguration driverConfiguration = findConfiguration(taskConfiguration.getVariable());
            if (driverConfiguration == null) {
                throw new GenericException(format("Wrong variable [%s] configuration.", taskConfiguration.getVariable()));
            }
            Runnable task = new EnquiryTask(enquirer, driverConfiguration);
            if(taskConfiguration.isRecurrent()) {
                scheduledFuture = executor.scheduleWithFixedDelay(task, taskConfiguration.getDelay(), taskConfiguration.getFrequency(), TimeUnit.SECONDS);
            }
            else {
                scheduledFuture = executor.schedule(task, taskConfiguration.getDelay(), TimeUnit.SECONDS);
            }
            futures.add(scheduledFuture);
        }
    }

    private DriverConfiguration findConfiguration(String variable) {
        for (DriverConfiguration driverConfiguration : driverConfigurations) {
            if(driverConfiguration.getName().equalsIgnoreCase(variable)) {
                return driverConfiguration;
            }
        }
        return null;
    }

    public void stop() {
        executor.shutdownNow();
    }

    public void join() throws InterruptedException {
        executor.awaitTermination(3, TimeUnit.SECONDS);
        for (ScheduledFuture future : futures) {
            if(!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }
    }

    private ProtocolEnquirer createProtocolEnquirer() {
        ProtocolEnquirer enquirer;
        switch(device.getSpecification().getProtocolType()) {
            case SNMP:
                enquirer = new SnmpEnquirer();
                break;
            case JMX:
                enquirer = new JmxEnquirer();
                break;
            case MODBUS:
                enquirer = new ModBusEnquirer();
                break;
            default:
                enquirer = new DemoEnquirer();
        }
        enquirer.loadConfiguration(device, (Integer)retries.getValue(), (Integer)timeout.getValue());
        enquirer.addResponseHandler(new LoggingResponseHandler(device));
        enquirer.addResponseHandler(new EventAwareResponseHandler(device));

        return enquirer;
    }

    private class EnquiryTask implements Runnable {
        private ProtocolEnquirer enquirer;
        private DriverConfiguration configuration;

        private EnquiryTask(ProtocolEnquirer enquirer, DriverConfiguration configuration) {
            this.enquirer = enquirer;
            this.configuration = configuration;
        }

        @Override
        public void run() {
            try {
                enquirer.readValue(configuration);
            } catch (GenericException e) {
                EventHelper.getInstance().addEvent(device, EventType.EVENT_CONFIGURATION, EventSeverity.EVENT_FATAL);
            }
        }
    }
}
