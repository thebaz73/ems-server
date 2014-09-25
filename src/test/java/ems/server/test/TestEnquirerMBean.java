package ems.server.test;

/**
 * TestEnquirerMBean
 * Created by thebaz on 24/09/14.
 */
public interface TestEnquirerMBean {
    String getName();

    void setName(String name);

    int getCacheSize();

    void setCacheSize(int size);
}
