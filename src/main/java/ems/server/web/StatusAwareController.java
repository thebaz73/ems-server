package ems.server.web;

import ems.server.business.ConfigurationManager;
import ems.server.domain.EmsConfigurationEntry;
import ems.server.monitor.MonitorService;
import ems.server.monitor.MonitoringStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConfigurationAwareController
 * Created by thebaz on 30/08/14.
 */
public abstract class StatusAwareController {
    @Autowired
    private ConfigurationManager configurationManager;
    @Autowired
    private MonitorService monitorService;

    @ModelAttribute("configuration")
    public Map<String, Object> getConfiguration() {
        Map<String, Object> model = new HashMap<String, Object>();
        List<EmsConfigurationEntry> allEntries = configurationManager.findAllEntries();
        for (EmsConfigurationEntry entry : allEntries) {
            model.put(entry.getKey(), entry.getValue());
        }

        return model;
    }

    @ModelAttribute("monitoringStatus")
    public MonitoringStatus getMonitoringStatus() {
        return monitorService.getMonitoringStatus();
    }
}
