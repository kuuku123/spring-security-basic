package com.example.corespringsecurity.security.filter;

import com.example.corespringsecurity.security.handler.AjaxAuthenticationFailureHandler;
import com.example.corespringsecurity.security.handler.AjaxAuthenticationSuccessHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AjaxFilterDsl extends AbstractHttpConfigurer<AjaxFilterDsl, HttpSecurity> {
    private boolean flag;

    @Override
    public void init(HttpSecurity http) throws Exception {
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);

        // here we lookup from the ApplicationContext. You can also just create a new instance.
        AjaxLoginProcessFilter filter = new AjaxLoginProcessFilter();
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(new AjaxAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(new AjaxAuthenticationFailureHandler());
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public AjaxFilterDsl flag(boolean value) {
        this.flag = value;
        return this;
    }

    public static AjaxFilterDsl customDsl() {
        return new AjaxFilterDsl();
    }
}
