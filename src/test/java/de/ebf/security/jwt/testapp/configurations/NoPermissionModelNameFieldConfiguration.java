package de.ebf.security.jwt.testapp.configurations;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import de.ebf.security.PermissionsConfig;
import de.ebf.security.jwt.testapp.othermodels.PermissionModelWithoutPermissionNameField;

@Configuration
@EnableAutoConfiguration
@PropertySource(value = { "classpath:init-permissions-disabled.properties" })
@Import(PermissionsConfig.class)
@EntityScan(basePackageClasses = { PermissionModelWithoutPermissionNameField.class })
public class NoPermissionModelNameFieldConfiguration {

}
