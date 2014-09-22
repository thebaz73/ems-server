package ems.server.monitor;

/**
 * MonitoringStatus
 * Created by thebaz on 22/09/14.
 */
public enum MonitoringStatus {
    RUNNING("RUNNING"), STOPPED("STOPPED");

    private final String name;

    MonitoringStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
