package ems.server.test;

import ems.driver.domain.Driver;

/**
* TestEnquirerDriver
* Created by thebaz on 25/09/14.
*/
public class TestEnquirerDriver implements Driver {
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
