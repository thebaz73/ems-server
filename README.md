# EMS - Enterprise Monitoring System

## Java Web Application - SCADA / HMI server

### Overview
In systems engineering, a system monitor (SM) is a process within a distributed system for collecting and storing state data. **Network monitoring** or more simply monitoring is the use of a system that constantly monitors a computer or a device for slow or failing components and that notifies the administrator. The **Internet of Things (IoT)** refers to the interconnection of uniquely identifiable embedded computing-like devices within the existing Internet infrastructure. 

### Project scope
Project scope is creating an application that can monitor remote devices with different protocols. Application offers easly customizable dashboard, a pluggable and extensible data model, advanced configuration functionalities.

#### Dashboard
Application home page is a dashboard, each user can customize its widgets position and layout.  
![dashboard](docs/dashboard.png)
Dashboard is a drill-down, straight-forward access point to remote device information, in order to allow operator reach the most important information in the fastest way. 

#### Data model
Application data model is pluggable and extensible each device is easly described by the notion of driver. The driver is, somehow, the hardware description of remote device. In the driver it is described the feature list of the device that has to be monitored.

For example let's suppose we want to define a RF modulator we can prepare a JSON file that will be our new driver like this:
```JSON
{
    "type":"object",
    "title":"Modulator",
    "javaInterfaces" : ["ems.driver.domain.Driver"],
    "javaType": "ems.driver.domain.modulator.ModulatorDriver",
    "properties":{
        "status":{
            "$ref": "status.json"
        },
        "type" : {
            "$ref": "driver_type.json"
        },
        "power":{
            "title":"Power",
            "type":"number"
        },
        "reflected-power":{
            "title":"Reflected power",
            "type":"number"
        },
        "nominal-power":{
            "title":"Nominal power",
            "type":"integer"
        },
        "location":{
            "$ref": "location.json"
        },
        "alarm":{
            "title":"Alarm",
            "type":"boolean"
        }
    },
    "additionalProperties": false
}
```
The driver must be compliant to [JSON Schema and Hyper-Schema specification](http://json-schema.org/). For futher information please referer to [EMS Extension project](https://github.com/thebaz73/ems-extension).

Once the driver is defined and imported into the application the user will be allowed to add information for each feature defined into the driver as in following screenshot.
![specification](docs/specification.png)

#### Configuration
![configuration](docs/configuration.png)


### Technology
 * Java
 * Spring (Core, Security, Data)
 * Restful Web Services
 * Javascript & HTML5
 * JSON & JSON Schema
 * MongoDB

### Status & Notes
Project is still in its early stages.

### Demo
Planned...
