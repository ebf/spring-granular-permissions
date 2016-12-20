package de.ebf.security.jwt.testapp;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import de.ebf.security.jwt.JWTPermissionsConfig;
import de.ebf.security.jwt.internal.services.PermissionScanner;
import de.ebf.security.jwt.internal.services.impl.DefaultPermissionScanner;
import de.ebf.security.jwt.testapp.models.Model;

@Configuration
@EnableAutoConfiguration
@PropertySource(value = { "classpath:application.properties" })
@Import(JWTPermissionsConfig.class)
@EntityScan(basePackageClasses = Model.class)
public class TestApplication {

    @Bean
    public PermissionScanner permissionScanner() {
        DefaultPermissionScanner defaultPermissionScanner = new DefaultPermissionScanner();
        defaultPermissionScanner.setBasePackage("de.ebf.security.jwt.testapp");
        return defaultPermissionScanner;
    }

}
