package ems.server.test;

/**
 * TestEnquirer
 * Created by thebaz on 24/09/14.
 */
public interface TestEnquirer {
    String getName();

    void setName(String name);

    int getCacheSize();

    void setCacheSize(int size);
}
