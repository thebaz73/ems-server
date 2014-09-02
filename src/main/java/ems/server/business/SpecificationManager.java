package ems.server.business;
/*
 * Copyright
 */

import ems.server.data.SpecificationRepository;
import ems.server.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class SpecificationManager {
    @Autowired
    private SpecificationRepository specificationRepository;

    public Specification findSpecification(String id) {
        return specificationRepository.findOne(id);
    }

    public List<Specification> findAllSpecifications() {
        return specificationRepository.findAll();
    }

    public void createSpecification(Specification specification) {
        if(specification.getId() != null) {
            specification.setId(null);
        }
        specificationRepository.save(specification);
    }

    public void editSpecification(Specification specification) {
        if(specification.getId() != null && specificationRepository.findOne(specification.getId()) != null ) {
            specificationRepository.save(specification);
        }
    }

    public void deleteSpecification(String id) {
        specificationRepository.delete(id);
    }

    public Page<Specification> findAllSpecifications(Pageable pageable) {
        return specificationRepository.findAll(pageable);
    }
}
