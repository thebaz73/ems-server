package ems.server.domain;


import org.springframework.data.annotation.Id;

/**
 * DriverConfiguration
 * Created by thebaz on 9/4/14.
 */
public class DriverConfiguration {
    @Id
    private String id;
    private String specificationId;
    private String name;
    private Object value;
    private String measure;
    private String function;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
