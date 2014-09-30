package ems.server.protocol.jmx;

import ems.server.test.TestEnquirer;

public class TestEnquirerMBean implements TestEnquirer {
    private int cacheSize;
    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public synchronized void setCacheSize(int size) {
        this.cacheSize = size;
    }
}
