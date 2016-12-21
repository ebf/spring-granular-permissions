package de.ebf.security.jwt.testapp;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import de.ebf.security.PermissionsConfig;
import de.ebf.security.jwt.testapp.controllers.TestController;
import de.ebf.security.jwt.testapp.models.Model;
import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;

@Configuration
@EnableAutoConfiguration
@PropertySource(value = { "classpath:application.properties" })
@Import(PermissionsConfig.class)
@EntityScan(basePackageClasses = Model.class)
@ComponentScan(basePackageClasses = TestController.class)
public class TestApplication {


    @Bean
    public PermissionScanner permissionScanner() {
        DefaultPermissionScanner defaultPermissionScanner = new DefaultPermissionScanner();
        defaultPermissionScanner.setBasePackage("de.ebf.security.jwt.testapp");
        return defaultPermissionScanner;
    }

}
