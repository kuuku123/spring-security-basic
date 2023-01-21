package com.example.corespringsecurity.security.filter;

import com.example.corespringsecurity.security.factory.UrlResourcesMapFactoryBean;
import com.example.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.example.corespringsecurity.security.voter.IpAddressVoter;
import com.example.corespringsecurity.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CustomFilterSecurityInterceptorDsl extends AbstractHttpConfigurer<CustomFilterSecurityInterceptorDsl, HttpSecurity> {

    private final SecurityResourceService securityResourceService;
    private final RoleHierarchyImpl roleHierarchy;

    private boolean flag;
    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

    @Override
    public void init(HttpSecurity http) throws Exception {
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);

        // here we lookup from the ApplicationContext. You can also just create a new instance.
        FilterSecurityInterceptor filterSecurityInterceptor = new PermitAllFilter(permitAllResources);
        filterSecurityInterceptor.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filterSecurityInterceptor.setSecurityMetadataSource(new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService));
        filterSecurityInterceptor.setAccessDecisionManager(new AffirmativeBased(getAccessDecisionVoters()));

        http.addFilterBefore(filterSecurityInterceptor, FilterSecurityInterceptor.class);

    }

    public CustomFilterSecurityInterceptorDsl flag(boolean value) {
        this.flag = value;
        return this;
    }

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(new IpAddressVoter(securityResourceService));
        accessDecisionVoters.add(roleVoter());

        return accessDecisionVoters;
    }

    private AccessDecisionVoter<? extends Object> roleVoter() {

        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy);
        return roleHierarchyVoter;
    }


}
