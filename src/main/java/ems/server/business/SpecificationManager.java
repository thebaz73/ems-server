package ems.server.business;
/*
 * Copyright
 */

import ems.server.data.SpecificationRepository;
import ems.server.domain.Specification;
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
public class SpecificationManager {
    @Autowired
    private SpecificationRepository specificationRepository;

    public Specification findSpecification(String id) {
        return specificationRepository.findOne(id);
    }

    public List<Specification> findAllSpecifications() {
        return specificationRepository.findAll();
    }

    public void editEntry(Specification entry) {
        specificationRepository.save(entry);
    }
}
