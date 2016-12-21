package de.ebf.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import de.ebf.security.guard.PermissionAccessDecisionManager;
import de.ebf.security.guard.PermissionMetadataSource;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
@EnableGlobalMethodSecurity
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Override
    @Bean
    protected AccessDecisionManager accessDecisionManager() {
        return new PermissionAccessDecisionManager();
    }

    @Override
    @Bean
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new PermissionMetadataSource();
    }
}
