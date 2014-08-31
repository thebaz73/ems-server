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
 * <p>
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

    public List<EmsConfigurationEntry> findEntryByKey(String key) {
        return configurationRepository.findByKey(key);
    }

    public void editEntry(EmsConfigurationEntry entry) {
        configurationRepository.save(entry);
    }
}
