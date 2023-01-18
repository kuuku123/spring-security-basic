package com.example.corespringsecurity.service.impl;

import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.repository.ResourcesRepository;
import com.example.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.example.corespringsecurity.service.ResourcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ResourcesServiceImpl implements ResourcesService {

    @Autowired
    private ResourcesRepository resourcesRepository;

    @Autowired
    private UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Transactional
    public Resources getResources(long id) {
        return resourcesRepository.findById(id).orElse(new Resources());
    }

    @Transactional
    public List<Resources> getResources() {
        return resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    @Transactional
    public void createResources(Resources resources){
        resourcesRepository.save(resources);
    }

    @Transactional
    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }

    @Override
    public void updateResources() {
        urlFilterInvocationSecurityMetadataSource.reload();
    }
}