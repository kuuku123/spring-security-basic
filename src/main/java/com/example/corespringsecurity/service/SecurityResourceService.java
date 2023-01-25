package com.example.corespringsecurity.service;

import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.repository.AccessIpRepository;
import com.example.corespringsecurity.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;
    private final AccessIpRepository accessIpRepository;


    @Transactional
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher,List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourceList = resourcesRepository.findAllResources();
        resourceList.forEach(re -> {
            List<ConfigAttribute> configAttributesList = new ArrayList<>();
            re.getResourcesRoles().forEach(resourcesRole -> {
                Role role = resourcesRole.getRole();
                configAttributesList.add(new SecurityConfig(role.getRoleName()));
            });
            result.put(new AntPathRequestMatcher(re.getResourceName()),configAttributesList);
        });
        return result;
    }
    @Transactional
    public LinkedHashMap<String, List<ConfigAttribute>> getMethodResourceList() {

        LinkedHashMap<String,List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourceList = resourcesRepository.findAllMethodResources();
        resourceList.forEach(re -> {
            List<ConfigAttribute> configAttributesList = new ArrayList<>();
            re.getResourcesRoles().forEach(resourcesRole -> {
                Role role = resourcesRole.getRole();
                configAttributesList.add(new SecurityConfig(role.getRoleName()));
            });
            result.put(re.getResourceName(),configAttributesList);
        });
        return result;
    }
    public List<String> getAccessIpList() {
        List<String> collect = accessIpRepository.findAll().stream()
                .map(accessIp -> accessIp.getIpAddress())
                .collect(Collectors.toList());
        return collect;
    }
}
