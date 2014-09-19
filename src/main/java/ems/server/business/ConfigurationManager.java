package ems.server.business;
/*
 * Copyright
 */

import ems.server.data.EmsConfigurationRepository;
import ems.server.domain.EmsConfigurationEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ConfigurationManager
 * <p/>
 * User: mbazzoni
 * Date: 8/28/14
 * Time: 9:16 AM
 */
@Component
public class ConfigurationManager {
    @Autowired
    private EmsConfigurationRepository configurationRepository;

    public EmsConfigurationEntry findEntry(String id) {
        return configurationRepository.findOne(id);
    }

    public List<EmsConfigurationEntry> findAllEntries() {
        return configurationRepository.findAll();
    }

    public EmsConfigurationEntry findEntryByKey(String key) {
        List<EmsConfigurationEntry> configurationEntries = configurationRepository.findByKey(key);
        if(!configurationEntries.isEmpty()) {
            return configurationEntries.get(0);
        }
        return null;
    }

    public void editEntry(EmsConfigurationEntry entry) {
        configurationRepository.save(entry);
    }
}
